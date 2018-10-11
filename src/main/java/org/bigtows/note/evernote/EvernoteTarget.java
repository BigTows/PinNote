/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note.evernote;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.bigtows.note.NoteTarget;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class EvernoteTarget implements NoteTarget<EvernoteTask> {

    /**
     * Name of target
     */
    private String nameTarget;
    /**
     * Identification note
     */
    private String guid;

    @SerializedName("tasks")
    private List<EvernoteTask> storageTask = new ArrayList<>();

    private transient final UniqueIdGenerator uniqueIdGenerator;

    private transient final EvernoteNotes notes;

    /**
     * Constructor
     *
     * @param nameTarget name of target
     */
    public EvernoteTarget(EvernoteNotes notes, String nameTarget) {
        this.notes = notes;
        this.nameTarget = nameTarget;
        this.uniqueIdGenerator = new SimpleUniqueIdGenerator();
    }

    /**
     * Constructor with self uniqueIdGenerator
     *
     * @param notes             Notes
     * @param nameTarget        name of target
     * @param uniqueIdGenerator unique id generator
     */
    public EvernoteTarget(EvernoteNotes notes, String nameTarget, UniqueIdGenerator uniqueIdGenerator) {
        this.notes = notes;
        this.nameTarget = nameTarget;
        this.uniqueIdGenerator = uniqueIdGenerator;
    }

    @Override
    public EvernoteTask addTask(String nameTask) {
        EvernoteTask task = new EvernoteTask(this, nameTask, uniqueIdGenerator);
        storageTask.add(task);
        return task;
    }


    public EvernoteTask addTask(EvernoteTask task) {
        storageTask.add(task);
        return task;
    }

    @Override
    public EvernoteTask addTask(boolean isCompleted, String nameTask) {
        EvernoteTask task = new EvernoteTask(this, nameTask, isCompleted, uniqueIdGenerator);
        storageTask.add(task);
        return task;
    }

    public EvernoteTask addTask(String uniqueId, boolean isCompleted, String nameTask) {
        EvernoteTask task = new EvernoteTask(this, uniqueId, isCompleted, nameTask);
        storageTask.add(task);
        return task;
    }


    @Override
    public String getName() {
        return nameTarget;
    }


    @Override
    public List<EvernoteTask> getAllTask() {
        return storageTask;
    }

    /**
     * Set identification note in Evernote
     *
     * @param guid Identification note
     */
    public EvernoteTarget setGuid(String guid) {
        this.guid = guid;
        return this;
    }

    public EvernoteTask getTask(EvernoteTask seached) {
        for (EvernoteTask task : storageTask) {
            if (seached.getUniqueId().equals(task.getUniqueId())) {
                return task;
            }
        }
        //TODO Exception..
        return null;
    }

    /**
     * Identification note in Evernote
     *
     * @return Identification note
     */
    public String getGuid() {
        return guid;
    }

    /**
     * Set name target
     * This method available only for Evernote
     *
     * @param nameTarget name of Target
     */
    public void setNameTarget(String nameTarget) {
        this.nameTarget = nameTarget;
    }


    /**
     * Remove from storage task
     *
     * @param searchedTask searched task
     */
    public void removeTask(EvernoteTask searchedTask) {
        for (EvernoteTask task : storageTask) {
            if (task.getUniqueId().equals(searchedTask.getUniqueId())) {
                storageTask.remove(task);
                break;
            }
        }
    }

    @Override
    public String toString() {
        Gson g = new Gson();
        return g.toJson(this);
    }

    @Override
    public void remove() {
        this.notes.removeTarget(this);
    }
}
