package org.bigtows.window.ui.notetree.tree.node;

import org.bigtows.window.ui.notetree.tree.entity.Task;

public class TaskTreeNode extends AbstractTaskTreeNode {
    public TaskTreeNode(Task task) {
        super(task, true);
    }


    @Override
    public TaskTreeNode clone() {
        var taskTreeNode = new TaskTreeNode(super.getUserObject());
        for (int i = 0; i < super.getChildCount(); i++) {
            var child = super.getChildAt(i);

            if (child instanceof AbstractTaskTreeNode) {
                taskTreeNode.add(((AbstractTaskTreeNode) child).clone());
            }
        }
        return taskTreeNode;
    }
}
