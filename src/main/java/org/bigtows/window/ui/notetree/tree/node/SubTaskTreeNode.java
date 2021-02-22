package org.bigtows.window.ui.notetree.tree.node;

import org.bigtows.window.ui.notetree.tree.entity.Task;

public class SubTaskTreeNode extends AbstractTaskTreeNode {
    public SubTaskTreeNode(Task task) {
        super(task, false);
    }

    @Override
    public SubTaskTreeNode clone() {
        return new SubTaskTreeNode(super.getUserObject());
    }
}
