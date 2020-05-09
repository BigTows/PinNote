/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.notebook.evernote.parser;


import java.util.ArrayList;
import java.util.List;

/**
 * Context for parsing XML Evernote note vision
 */
public class TaskContext {

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
     * List sub task
     */
    private List<SubTaskContext> subTaskContexts = new ArrayList<>();


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
     * Gets sub task contexts.
     *
     * @return the sub task contexts
     */
    public List<SubTaskContext> getSubTaskContexts() {
        return subTaskContexts;
    }

    /**
     * Add sub task contexts.
     *
     * @param subTaskContexts the sub task contexts
     */
    public void addSubTaskContexts(SubTaskContext subTaskContexts) {
        this.subTaskContexts.add(subTaskContexts);
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
