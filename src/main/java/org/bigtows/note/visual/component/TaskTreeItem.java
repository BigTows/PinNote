/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note.visual.component;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyEvent;
import org.bigtows.note.Task;

public class TaskTreeItem extends TreeItem<TextField> implements NoteCustomComponent {

    private Task task;

    private TextField textField;

    private NoteCheckBox checkBox;

    public TaskTreeItem(Task task) {
        super(new TextField(task.getNameTask()), new NoteCheckBox(task));
        this.task = task;
        this.textField = this.getValue();
        this.checkBox = (NoteCheckBox) this.getGraphic();
        this.checkBox.setSelected(task.isCompleted());
    }


    public void setOnActionTextField(EventHandler<? super KeyEvent> event) {
        textField.setOnKeyPressed(event);
    }

    public void setOnActionCheckBox(EventHandler<ActionEvent> event) {
        checkBox.setOnAction(event);
    }

    @Override
    public void update() {
        textField.setText(task.getNameTask());
        checkBox.setSelected(task.isCompleted());
    }

    public Task getTask() {
        return task;
    }
}
