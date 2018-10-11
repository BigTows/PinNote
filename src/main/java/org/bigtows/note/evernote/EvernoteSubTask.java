/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note.evernote;

import org.bigtows.note.NoteSubTask;

/**
 * Evernote representation of SubTask
 */
public class EvernoteSubTask implements NoteSubTask<EvernoteTask> {

    private transient EvernoteTask task;

    /**
     * Name of subTask
     */
    private String nameTask;

    private boolean isComplete;

    private String uniqueId;

    EvernoteSubTask(EvernoteTask task, String nameSubTask, boolean isCompleted, UniqueIdGenerator uniqueIdGenerator) {
        this.task = task;
        this.nameTask = nameSubTask;
        this.isComplete = isCompleted;
        this.uniqueId = uniqueIdGenerator.getUniqueId(task.getNameTask(), nameSubTask);
    }

    EvernoteSubTask(EvernoteTask task, String nameSubTask, UniqueIdGenerator uniqueIdGenerator) {
        this(task, nameSubTask, false, uniqueIdGenerator);
    }

    public EvernoteSubTask(EvernoteTask task, String uniqueId, String nameSubTask, boolean isCompleted) {
        this(task, nameSubTask, isCompleted, new SimpleUniqueIdGenerator());
        this.uniqueId = uniqueId;
    }


    @Override
    public EvernoteTask getTask() {
        return task;
    }

    @Override
    public String getNameTask() {
        return nameTask;
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


    public String getUniqueId() {
        return uniqueId;
    }

    public void remove() {
        this.getTask().removeSubTask(this);
    }
}
