/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note.visual.component;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import org.bigtows.note.NoteTarget;
import org.bigtows.note.visual.component.action.ActionDelete;

public class TargetTreeItem extends TreeItem<Label> implements NoteCustomComponent {

    private NoteTarget target;
    private Label label;
    private ActionDelete actionDelete;

    public TargetTreeItem(NoteTarget target) {
        super(new Label(target.getName()));
        this.target = target;
        this.label = this.getValue();
        this.buildContextMenuForTarget();
    }

    @Override
    public void update() {
        label.setText(target.getName());
    }

    public NoteTarget getTarget() {
        return target;
    }


    /**
     * Set action on delete
     *
     * @param actionDelete action on delete
     * @return self
     */
    public TargetTreeItem setOnActionDelete(ActionDelete actionDelete) {
        this.actionDelete = actionDelete;
        return this;
    }


    /**
     * Build context menu for task
     */
    private void buildContextMenuForTarget() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem menuItem = new MenuItem("Remove");

        menuItem.setOnAction((actionEvent) -> {
            this.getParent().getChildren().remove(this);
            this.actionDelete.onDelete();
        });
        contextMenu.getItems().add(menuItem);
        this.getValue().setContextMenu(contextMenu);
    }
}
