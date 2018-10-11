/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note.evernote;

import com.google.gson.annotations.SerializedName;
import org.bigtows.note.NoteTask;

import java.util.ArrayList;
import java.util.List;

public class EvernoteTask implements NoteTask<EvernoteTarget, EvernoteSubTask> {

    private transient EvernoteTarget target;

    private String nameTask;

    private boolean isComplete;

    private String uniqueId;

    private transient UniqueIdGenerator uniqueIdGenerator;

    @SerializedName("subTasks")
    private final List<EvernoteSubTask> storageSubTask = new ArrayList<>();


    EvernoteTask(EvernoteTarget target, String nameTask, boolean isCompleted, UniqueIdGenerator uniqueIdGenerator) {
        this.target = target;
        this.nameTask = nameTask;
        this.isComplete = isCompleted;
        this.uniqueIdGenerator = uniqueIdGenerator;
        this.uniqueId = uniqueIdGenerator.getUniqueId(target.getName(), nameTask);
    }

    public EvernoteTask(EvernoteTarget target, String nameTask, UniqueIdGenerator uniqueIdGenerator) {
        this(target, nameTask, false, uniqueIdGenerator);
    }

    public EvernoteTask(EvernoteTarget target, String uniqueId, boolean isCompleted, String nameTask) {
        this(target, nameTask, isCompleted, new SimpleUniqueIdGenerator());
        this.uniqueId = uniqueId;
    }


    @Override
    public EvernoteSubTask addSubTask(String nameSubTask) {
        EvernoteSubTask subTask = new EvernoteSubTask(this, nameSubTask, uniqueIdGenerator);
        storageSubTask.add(subTask);
        return subTask;
    }

    @Override
    public EvernoteSubTask addSubTask(boolean isCompleted, String nameSubTask) {
        EvernoteSubTask subTask = new EvernoteSubTask(this, nameSubTask, isCompleted, uniqueIdGenerator);
        storageSubTask.add(subTask);
        return subTask;
    }

    public EvernoteSubTask addSubTask(String uniqueId, boolean isCompleted, String nameSubTask) {
        EvernoteSubTask subTask = new EvernoteSubTask(this, uniqueId, nameSubTask, isCompleted);
        storageSubTask.add(subTask);
        return subTask;
    }

    public EvernoteSubTask addSubTask(EvernoteSubTask subTask) {
        storageSubTask.add(subTask);
        return subTask;
    }


    @Override
    public String getNameTask() {
        return nameTask;
    }

    public EvernoteTarget getTarget() {
        return target;
    }

    @Override
    public void setCompleted(boolean isCompleted) {
        this.isComplete = isCompleted;
    }

    @Override
    public boolean isCompleted() {
        return isComplete;
    }

    @Override
    public void editNameTask(String newName) {
        this.nameTask = newName;
    }

    @Override
    public List<EvernoteSubTask> getSubTask() {
        return storageSubTask;
    }

    public String getUniqueId() {
        return uniqueId;
    }


    /**
     * Remove this task
     */
    public void remove() {
        getTarget().removeTask(this);
    }

    public void removeSubTask(EvernoteSubTask searchedSubTask) {
        for (EvernoteSubTask subTask : storageSubTask) {
            if (subTask.getUniqueId().equals(searchedSubTask.getUniqueId())) {
                storageSubTask.remove(subTask);
                break;
            }
        }
    }
}
