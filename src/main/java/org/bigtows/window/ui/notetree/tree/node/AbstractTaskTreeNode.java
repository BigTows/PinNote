package org.bigtows.window.ui.notetree.tree.node;

import org.bigtows.window.ui.notetree.tree.entity.Task;

import javax.swing.tree.DefaultMutableTreeNode;

public abstract class AbstractTaskTreeNode extends DefaultMutableTreeNode {

    private CreationReason creationReason = CreationReason.UNKNOWN;

    public AbstractTaskTreeNode(Task task, boolean allowsChildren) {
        super(task, allowsChildren);
    }

    @Override
    public Task getUserObject() {
        return (Task) super.getUserObject();
    }


    public CreationReason getCreationReason() {
        return creationReason;
    }

    public void setCreationReason(CreationReason creationReason) {
        this.creationReason = creationReason;
    }


    @Override
    public abstract Object clone();
}
