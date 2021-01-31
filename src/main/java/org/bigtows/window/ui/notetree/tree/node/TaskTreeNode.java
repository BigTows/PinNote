package org.bigtows.window.ui.notetree.tree.node;

import org.bigtows.window.ui.notetree.tree.entity.Task;

public class TaskTreeNode extends AbstractTaskTreeNode {
    public TaskTreeNode(Task task) {
        super(task, true);
    }


    @Override
    public Object clone() {
        return new TaskTreeNode(super.getUserObject());
    }
}
