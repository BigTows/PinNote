/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note;

import java.util.List;

/**
 * Interface target
 */
public interface NoteTarget<T> extends RemovableObject {


    /**
     * Added EvernoteTask for target
     *
     * @param nameTask name of task
     * @return self
     */
    T addTask(String nameTask);


    /**
     * Added EvernoteTask for target
     *
     * @param isCompleted task is completed
     * @param nameTask    name of task
     * @return self
     */
    T addTask(boolean isCompleted, String nameTask);

    /**
     * Get name target
     *
     * @return name target
     */
    String getName();


    /**
     * Get all task
     *
     * @return list task
     */
    List<T> getAllTask();
}
