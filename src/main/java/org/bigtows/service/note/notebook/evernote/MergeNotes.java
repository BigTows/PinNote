package org.bigtows.service.note.notebook.evernote;

import org.bigtows.service.note.notebook.Task;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MergeNotes {

    private List<EvernoteNote> cacheNotes = new ArrayList<>();

    private List<Runnable> outdatedObjects = new ArrayList<>();

    public void setCacheNotes(List<EvernoteNote> cacheNotes) {
        this.cacheNotes = cacheNotes;
    }

    public List<EvernoteNote> sync(List<EvernoteNote> clientNotes, List<EvernoteNote> serverNotes) {
        for (EvernoteNote target : serverNotes) {
            this.syncTarget(
                    clientNotes,
                    this.searchTarget(cacheNotes, target),
                    this.searchTarget(clientNotes, target),
                    target
            );
        }

        for (EvernoteNote target : clientNotes) {
            this.syncTarget(
                    clientNotes,
                    this.searchTarget(cacheNotes, target),
                    target,
                    this.searchTarget(serverNotes, target)
            );
        }

        outdatedObjects.forEach(Runnable::run);
        outdatedObjects.clear();
        return clientNotes;
    }


    private void syncTarget(List<EvernoteNote> clientNotes, EvernoteNote cacheTarget, EvernoteNote clientTarget, EvernoteNote serverTarget) {
        if (cacheTarget != null && clientTarget == null) {
            //Client remove this target
            return;
        }
        if (cacheTarget == null && clientTarget == null) {
            clientNotes.add(serverTarget);
            return;
        }

        if (serverTarget == null && cacheTarget != null) {
            //Memory leak? mb not XD
            //Server remove this target
            outdatedObjects.add(
                    () -> clientNotes.remove(clientTarget)
            );
            return;
        }
        if (serverTarget == null) {
            return;
        }

        clientTarget.setName(
                this.resolveTargetName(cacheTarget, clientTarget, serverTarget)
        );

        for (EvernoteTask task : serverTarget.getTasks()) {
            this.syncTask(
                    clientTarget,
                    this.searchTask(cacheTarget, task),
                    this.searchTask(clientTarget, task),
                    task
            );
        }

        for (EvernoteTask task : clientTarget.getTasks()) {
            this.syncTask(
                    clientTarget,
                    this.searchTask(cacheTarget, task),
                    task,
                    this.searchTask(serverTarget, task)
            );
        }

    }

    private void syncTask(@NotNull EvernoteNote clientTarget, EvernoteTask cacheTask, EvernoteTask clientTask, EvernoteTask serverTask) {
        if (cacheTask != null && clientTask == null) {
            //Client remove this task
            return;
        }
        if (cacheTask == null && clientTask == null) {
            clientTarget.getTasks().add(
                    EvernoteTask.builder()
                            .id(serverTask.getId())
                            .checked(serverTask.isChecked())
                            .name(serverTask.getName())
                            .build()
            );
            return;
        }

        if (serverTask == null && cacheTask != null) {
            //Memory leak? mb not XD
            //Server remove this task
            outdatedObjects.add(() -> {
                clientTarget.getTasks().remove(clientTask);
            });
            return;
        }
        if (serverTask == null) {
            return;
        }

        clientTask.setName(
                this.resolveTaskName(cacheTask, clientTask, serverTask)
        );

        clientTask.setChecked(
                this.resolveTaskCompleted(cacheTask, clientTask, serverTask)
        );
        for (EvernoteSubTask subTask : serverTask.getSubTask()) {
            this.syncSubTask(
                    clientTask,
                    this.searchSubTask(cacheTask, subTask),
                    this.searchSubTask(clientTask, subTask),
                    subTask
            );
        }

        for (EvernoteSubTask subTask : clientTask.getSubTask()) {
            this.syncSubTask(
                    clientTask,
                    this.searchSubTask(cacheTask, subTask),
                    subTask,
                    this.searchSubTask(serverTask, subTask)
            );
        }

    }

    private void syncSubTask(@NotNull EvernoteTask clientTask, EvernoteSubTask cacheSubTask, EvernoteSubTask clientSubTask, EvernoteSubTask serverSubTask) {
        if (cacheSubTask != null && clientSubTask == null) {
            //Client remove this target
            return;
        }
        if (cacheSubTask == null && clientSubTask == null) {
            clientTask.getSubTask().add(
                    EvernoteSubTask.builder()
                            .id(serverSubTask.getId())
                            .name(serverSubTask.getName())
                            .checked(serverSubTask.isChecked())
                            .build()
            );
            return;
        }
        if (serverSubTask == null && cacheSubTask != null) {
            //Memory leak? mb not XD
            //Server remove this task
            outdatedObjects.add(() -> {
                clientTask.getSubTask().remove(clientSubTask);
            });
            return;
        }
        if (serverSubTask == null) {
            return;
        }
        clientSubTask.setName(
                this.resolveTaskName(cacheSubTask, clientSubTask, serverSubTask)
        );
        clientSubTask.setChecked(
                this.resolveTaskCompleted(cacheSubTask, clientSubTask, serverSubTask)
        );
    }


    private EvernoteNote searchTarget(List<EvernoteNote> storage, EvernoteNote searched) {
        for (EvernoteNote target : storage) {
            if (target.getId() != null && target.getId().equals(searched.getId())) {
                return target;
            }
        }
        return null;
    }

    private EvernoteTask searchTask(EvernoteNote storage, EvernoteTask searched) {
        if (storage == null) {
            return null;
        }
        for (EvernoteTask task : storage.getTasks()) {
            if (task.getId().equals(searched.getId()) || task.getName().equals(searched.getName())) {
                return task;
            }
        }
        return null;
    }

    private EvernoteSubTask searchSubTask(EvernoteTask storage, EvernoteSubTask searched) {
        if (storage == null) {
            return null;
        }
        for (EvernoteSubTask subTask : storage.getSubTask()) {
            if (subTask.getId().equals(searched.getId()) || subTask.getName().equals(searched.getName())) {
                return subTask;
            }
        }
        return null;
    }


    private String resolveTargetName(EvernoteNote cacheTarget, @NotNull EvernoteNote clientTarget, @NotNull EvernoteNote serverTarget) {
        if (cacheTarget != null && cacheTarget.getName().equals(clientTarget.getName())) {
            //Client don't rename Task
            return serverTarget.getName().trim();
        } else {
            return clientTarget.getName().trim();
        }
    }

    private String resolveTaskName(Task cacheTask, @NotNull Task clientTask, @NotNull Task serverTask) {
        if (cacheTask != null && cacheTask.getName().equals(clientTask.getName())) {
            //Client don't rename Task
            return serverTask.getName().trim();
        } else {
            return clientTask.getName().trim();
        }
    }

    private boolean resolveTaskCompleted(Task cacheTask, @NotNull Task clientTask, @NotNull Task serverTask) {
        if (cacheTask != null && cacheTask.isChecked() == clientTask.isChecked()) {
            //Client don't change Task
            return serverTask.isChecked();
        } else {
            return clientTask.isChecked();
        }
    }
}