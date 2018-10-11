/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note.storage.util;

import org.bigtows.note.RemovableObject;
import org.bigtows.note.Task;
import org.bigtows.note.evernote.EvernoteNotes;
import org.bigtows.note.evernote.EvernoteSubTask;
import org.bigtows.note.evernote.EvernoteTarget;
import org.bigtows.note.evernote.EvernoteTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Merge mechanism for Evernote notes.
 */
public class MergeNotes {

    private EvernoteNotes cacheNotes = new EvernoteNotes();

    private List<RemovableObject> outdatedObjects = new ArrayList<>();

    public void setCacheNotes(EvernoteNotes cacheNotes) {
        this.cacheNotes = cacheNotes;
    }

    public EvernoteNotes sync(EvernoteNotes clientNotes, EvernoteNotes serverNotes) {
        for (EvernoteTarget target : serverNotes.getAllTarget()) {
            this.syncTarget(
                    clientNotes,
                    this.searchTarget(cacheNotes, target),
                    this.searchTarget(clientNotes, target),
                    target
            );
        }

        for (EvernoteTarget target : clientNotes.getAllTarget()) {
            this.syncTarget(
                    clientNotes,
                    this.searchTarget(cacheNotes, target),
                    target,
                    this.searchTarget(serverNotes, target)
            );
        }

        outdatedObjects.forEach(RemovableObject::remove);
        outdatedObjects.clear();
        return clientNotes;
    }


    private void syncTarget(EvernoteNotes clientNotes, EvernoteTarget cacheTarget, EvernoteTarget clientTarget, EvernoteTarget serverTarget) {
        if (cacheTarget != null && clientTarget == null) {
            //Client remove this target
            return;
        }
        if (cacheTarget == null && clientTarget == null) {
            clientNotes.addTarget(serverTarget);
            return;
        }

        if (serverTarget == null && cacheTarget != null) {
            //Memory leak? mb not XD
            //Server remove this target
            outdatedObjects.add(clientTarget);
            return;
        }
        if (serverTarget == null) {
            return;
        }

        for (EvernoteTask task : serverTarget.getAllTask()) {
            this.syncTask(
                    clientTarget,
                    this.searchTask(cacheTarget, task),
                    this.searchTask(clientTarget, task),
                    task
            );
        }

        for (EvernoteTask task : clientTarget.getAllTask()) {
            this.syncTask(
                    clientTarget,
                    this.searchTask(cacheTarget, task),
                    task,
                    this.searchTask(serverTarget, task)
            );
        }

    }

    private void syncTask(@NotNull EvernoteTarget clientTarget, EvernoteTask cacheTask, EvernoteTask clientTask, EvernoteTask serverTask) {
        if (cacheTask != null && clientTask == null) {
            //Client remove this task
            return;
        }
        if (cacheTask == null && clientTask == null) {
            clientTarget.addTask(serverTask.getUniqueId(), serverTask.isCompleted(), serverTask.getNameTask());
            return;
        }

        if (serverTask == null && cacheTask != null) {
            //Memory leak? mb not XD
            //Server remove this task
            outdatedObjects.add(clientTask);
            return;
        }
        if (serverTask == null) {
            return;
        }

        clientTask.editNameTask(
                this.resolveTaskName(cacheTask, clientTask, serverTask)
        );

        clientTask.setCompleted(
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
            clientTask.addSubTask(serverSubTask.getUniqueId(), serverSubTask.isCompleted(), serverSubTask.getNameTask());
            return;
        }
        if (serverSubTask == null && cacheSubTask != null) {
            //Memory leak? mb not XD
            //Server remove this task
            outdatedObjects.add(clientSubTask);
            return;
        }
        if (serverSubTask == null) {
            return;
        }
        clientSubTask.editNameTask(
                this.resolveTaskName(cacheSubTask, clientSubTask, serverSubTask)
        );
        clientSubTask.setCompleted(
                this.resolveTaskCompleted(cacheSubTask, clientSubTask, serverSubTask)
        );
    }


    private EvernoteTarget searchTarget(EvernoteNotes storage, EvernoteTarget searched) {
        for (EvernoteTarget target : storage.getAllTarget()) {
            if (target.getGuid() != null && target.getGuid().equals(searched.getGuid())) {
                return target;
            }
        }
        return null;
    }

    private EvernoteTask searchTask(EvernoteTarget storage, EvernoteTask searched) {
        if (storage == null) {
            return null;
        }
        for (EvernoteTask task : storage.getAllTask()) {
            if (task.getUniqueId().equals(searched.getUniqueId()) || task.getNameTask().equals(searched.getNameTask())) {
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
            if (subTask.getUniqueId().equals(searched.getUniqueId()) || subTask.getNameTask().equals(searched.getNameTask())) {
                return subTask;
            }
        }
        return null;
    }


    private String resolveTaskName(Task cacheTask, @NotNull Task clientTask, @NotNull Task serverTask) {
        if (cacheTask != null && cacheTask.getNameTask().equals(clientTask.getNameTask())) {
            //Client don't rename Task
            return serverTask.getNameTask();
        } else {
            return clientTask.getNameTask();
        }
    }

    private boolean resolveTaskCompleted(Task cacheTask, @NotNull Task clientTask, @NotNull Task serverTask) {
        if (cacheTask != null && cacheTask.isCompleted() == clientTask.isCompleted()) {
            //Client don't change Task
            return serverTask.isCompleted();
        } else {
            return clientTask.isCompleted();
        }
    }
}
