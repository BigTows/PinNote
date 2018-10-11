/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note.storage.parse.evernote;

/**
 * Context for parsing XML Evernote note vision
 */
public class SubTaskContext {

    /**
     * Unique identification
     */
    private String uniqueId;

    /**
     * Task is completed
     */
    private boolean isCompleted;

    /**
     * Task name
     */
    private String name;

    /**
     * This task completed
     *
     * @return is Completed
     */
    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * Set status completed
     *
     * @param completed is completed
     */
    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }


    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get Unique identification
     *
     * @return Unique identification
     */
    public String getUniqueId() {
        return uniqueId;
    }

    /**
     * Setup Unique identification
     *
     * @param uniqueId Unique identification
     */
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public boolean hasUniqueId() {
        return uniqueId != null && !uniqueId.equals("");
    }
}
