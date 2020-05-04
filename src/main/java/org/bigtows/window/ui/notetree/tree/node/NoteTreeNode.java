package org.bigtows.window.ui.notetree.tree.node;

import org.bigtows.window.ui.notetree.tree.entity.Note;

import javax.swing.tree.DefaultMutableTreeNode;

public class NoteTreeNode extends DefaultMutableTreeNode {

    public NoteTreeNode(Note note) {
        super(note, true);
    }

    @Override
    public Note getUserObject() {
        return (Note) super.getUserObject();
    }
}
