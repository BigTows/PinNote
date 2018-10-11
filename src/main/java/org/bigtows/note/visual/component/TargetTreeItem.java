/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note.visual.component;

import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import org.bigtows.note.NoteTarget;

public class TargetTreeItem extends TreeItem<Label> implements NoteCustomComponent {

    private NoteTarget target;
    private Label label;

    public TargetTreeItem(NoteTarget target) {
        super(new Label(target.getName()));
        this.target = target;
        this.label = this.getValue();
    }

    @Override
    public void update() {
        label.setText(target.getName());
    }

    public NoteTarget getTarget() {
        return target;
    }
}
