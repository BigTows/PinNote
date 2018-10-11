/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note.evernote;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.bigtows.note.Notes;

import java.util.ArrayList;
import java.util.List;

/**
 * Evernote representation of Notes
 */
public class EvernoteNotes implements Notes<EvernoteTarget>, Cloneable {


    @SerializedName("targets")
    private List<EvernoteTarget> storageTarget = new ArrayList<>();


    private transient final UniqueIdGenerator uniqueIdGenerator;

    public EvernoteNotes() {
        this.uniqueIdGenerator = new SimpleUniqueIdGenerator();
    }

    public EvernoteNotes(UniqueIdGenerator uniqueIdGenerator) {
        this.uniqueIdGenerator = uniqueIdGenerator;
    }


    @Override
    public EvernoteTarget addTarget(String name) {
        EvernoteTarget target = new EvernoteTarget(this, name, uniqueIdGenerator);
        storageTarget.add(target);
        return target;
    }

    @Override
    public EvernoteTarget addTarget(EvernoteTarget target) {
        storageTarget.add(target);
        return target;
    }

    @Override
    public List<EvernoteTarget> getAllTarget() {
        return storageTarget;
    }

    public EvernoteTarget getTarget(EvernoteTarget target) {
        for (EvernoteTarget searchedTarget : getAllTarget()) {
            if (searchedTarget.getGuid() != null && searchedTarget.getGuid().equals(target.getGuid())) {
                return searchedTarget;
            }
        }
        return null;
    }

    /**
     * Remove target
     *
     * @param target
     */
    public void removeTarget(EvernoteTarget target) {
        for (EvernoteTarget searchedTarget : getAllTarget()) {
            if (searchedTarget.getGuid().equals(target.getGuid())) {
                storageTarget.remove(searchedTarget);
                break;
            }
        }
    }

    @Override
    public EvernoteNotes clone() throws CloneNotSupportedException {
        return (EvernoteNotes) super.clone();
    }

    @Override
    public String toString() {
        Gson g = new Gson();
        return g.toJson(this);
    }
}
