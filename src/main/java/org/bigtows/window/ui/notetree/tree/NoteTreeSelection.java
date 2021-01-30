package org.bigtows.window.ui.notetree.tree;

import org.bigtows.window.ui.notetree.tree.node.NoteTreeNode;

import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

public class NoteTreeSelection extends DefaultTreeSelectionModel {


    public NoteTreeSelection() {
        setSelectionMode(super.SINGLE_TREE_SELECTION);
    }

    @Override
    public void setSelectionPath(TreePath path) {
        super.clearSelection();
        if (!(path.getLastPathComponent() instanceof NoteTreeNode)) {
            return;
        }
        super.setSelectionPath(path);
    }

}
