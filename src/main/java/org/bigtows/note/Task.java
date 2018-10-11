/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note;

public interface Task extends RemovableObject {

    public String getNameTask();

    public void setCompleted(boolean isCompleted);

    public boolean isCompleted();

    public void editNameTask(String newName);

    public void remove();
}
