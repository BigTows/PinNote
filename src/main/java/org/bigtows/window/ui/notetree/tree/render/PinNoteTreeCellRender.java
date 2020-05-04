package org.bigtows.window.ui.notetree.tree.render;

import org.bigtows.window.ui.notetree.tree.TaskPanel;
import org.bigtows.window.ui.notetree.tree.entity.Task;
import org.bigtows.window.ui.notetree.tree.event.TreeChanged;
import org.bigtows.window.ui.notetree.tree.event.UserShortcutPressed;
import org.bigtows.window.ui.notetree.tree.node.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import java.awt.*;

public class PinNoteTreeCellRender implements TreeCellRenderer {
    private final TreeChanged treeChanged;

    public PinNoteTreeCellRender(TreeChanged treeChanged) {
        this.treeChanged = treeChanged;
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        if (value instanceof AbstractTaskTreeNode) {
            var sourceTaskTreeNode = (AbstractTaskTreeNode) value;
            var panel = new TaskPanel(sourceTaskTreeNode,
                    new UserShortcutPressed() {
                        @Override
                        public void newTask() {
                            AbstractTaskTreeNode abstractTaskTreeNode = null;
                            if (value instanceof TaskTreeNode) {
                                abstractTaskTreeNode = new TaskTreeNode(Task.builder().build());
                            } else if (value instanceof SubTaskTreeNode) {
                                abstractTaskTreeNode = new SubTaskTreeNode(Task.builder().build());
                            }

                            if (abstractTaskTreeNode != null) {
                                abstractTaskTreeNode.setCreationReason(CreationReason.USER);
                                ((DefaultMutableTreeNode) (sourceTaskTreeNode).getParent()).add(abstractTaskTreeNode);
                                tree.updateUI();
                            }
                        }

                        @Override
                        public void newSubTask() {
                            AbstractTaskTreeNode abstractTaskTreeNode = null;
                            if (value instanceof TaskTreeNode) {
                                abstractTaskTreeNode = new SubTaskTreeNode(Task.builder().build());
                            }

                            if (abstractTaskTreeNode != null) {
                                abstractTaskTreeNode.setCreationReason(CreationReason.USER);
                                sourceTaskTreeNode.add(abstractTaskTreeNode);
                                tree.expandPath(new TreePath(abstractTaskTreeNode.getPath()));
                                tree.updateUI();
                            }
                        }
                    }, treeChanged);
            if (sourceTaskTreeNode.getCreationReason() == CreationReason.USER) {
                SwingUtilities.invokeLater(() -> {
                    var treePath = new TreePath(sourceTaskTreeNode.getPath());
                    tree.expandPath(treePath);
                    tree.expandRow(row);
                    tree.startEditingAtPath(treePath);
                });
                sourceTaskTreeNode.setCreationReason(CreationReason.UNKNOWN);
            }

            return panel;
        } else if (value instanceof NoteTreeNode) {
            return new JLabel(((NoteTreeNode) value).getUserObject().getName());
        }
        return new JLabel("root");
    }
}
