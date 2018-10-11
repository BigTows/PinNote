/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note.visual.component;

import javafx.scene.control.CheckBox;
import org.bigtows.note.Task;

public class NoteCheckBox extends CheckBox {


    private Task task;


    public NoteCheckBox(Task task) {
        super();
        this.task = task;
    }

    public void changeSelectedStatus(boolean isSelected) {
        super.setSelected(isSelected);
        task.setCompleted(isSelected);
    }
}
