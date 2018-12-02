/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note.visual.component;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.input.KeyEvent;
import org.bigtows.note.Task;
import org.bigtows.note.visual.component.action.ActionDelete;

/**
 * The type Task tree item.
 */
public class TaskTreeItem extends TreeItem<TextField> implements NoteCustomComponent {

    /**
     * Instance of task
     */
    private Task task;

    /**
     * Instance of JavaFx Component TextField
     */
    private TextField textField;

    /**
     * Instance of PinNote Component CheckBox
     */
    private NoteCheckBox checkBox;
    private ActionDelete actionDelete;

    /**
     * Instantiates a new Task tree item.
     *
     * @param task the task
     */
    public TaskTreeItem(Task task) {
        super(new TextField(task.getNameTask()), new NoteCheckBox(task));
        this.task = task;
        this.textField = this.getValue();
        this.checkBox = (NoteCheckBox) this.getGraphic();
        this.checkBox.setSelected(task.isCompleted());
        this.buildContextMenuForTask();
    }


    /**
     * Sets on action text field.
     *
     * @param event the event
     */
    public void setOnActionTextField(EventHandler<? super KeyEvent> event) {
        textField.setOnKeyPressed(event);
    }

    /**
     * Sets on action check box.
     *
     * @param event the event
     * @return self
     */
    public TaskTreeItem setOnActionCheckBox(EventHandler<ActionEvent> event) {
        checkBox.setOnAction(event);
        return this;
    }

    @Override
    public void update() {
        int currentCaretPosition = textField.getCaretPosition();
        textField.setText(task.getNameTask());
        checkBox.setSelected(task.isCompleted());
        int newCaretPosition = this.processCaretPosition(currentCaretPosition);
        if (newCaretPosition != 0) {
            textField.positionCaret(newCaretPosition);
        }
    }

    /**
     * Process new Caret position
     *
     * @param beforeCaretPosition caret position before
     * @return new caret position
     */
    private int processCaretPosition(int beforeCaretPosition) {
        if (textField.getText().length() < beforeCaretPosition) {
            beforeCaretPosition = task.getNameTask().length();
        }
        return beforeCaretPosition;
    }

    /**
     * Build context menu for task
     */
    private void buildContextMenuForTask() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem menuItem = new MenuItem("Remove");

        menuItem.setOnAction((actionEvent) -> {
            task.remove();
            this.getParent().getChildren().remove(this);
            this.actionDelete.onDelete();
        });
        contextMenu.getItems().add(menuItem);
        this.getValue().setContextMenu(contextMenu);
    }

    /**
     * Set action on delete
     *
     * @param actionDelete action on delete
     * @return self
     */
    public TaskTreeItem setOnActionDelete(ActionDelete actionDelete) {
        this.actionDelete = actionDelete;
        return this;
    }

    /**
     * Get  task
     *
     * @return task task
     */
    public Task getTask() {
        return task;
    }
}
