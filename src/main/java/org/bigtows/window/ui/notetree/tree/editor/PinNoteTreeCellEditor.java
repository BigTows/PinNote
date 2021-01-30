package org.bigtows.window.ui.notetree.tree.editor;

import org.bigtows.window.ui.notetree.tree.component.TargetPanel;
import org.bigtows.window.ui.notetree.tree.component.TaskPanel;
import org.bigtows.window.ui.notetree.tree.entity.Task;
import org.bigtows.window.ui.notetree.tree.event.TreeChanged;
import org.bigtows.window.ui.notetree.tree.event.UserAction;
import org.bigtows.window.ui.notetree.tree.node.*;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.EventObject;

public class PinNoteTreeCellEditor implements TreeCellEditor {

    private final TreeChanged treeChanged;

    public PinNoteTreeCellEditor(TreeChanged treeChanged) {
        this.treeChanged = treeChanged;
    }

    @Override
    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
        if (value instanceof AbstractTaskTreeNode) {
            var sourceTaskTreeNode = (AbstractTaskTreeNode) value;
            var panel = new TaskPanel(
                    sourceTaskTreeNode,
                    new UserActionForPinNoteTreeCellEditor(tree, sourceTaskTreeNode),
                    treeChanged
            );
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
            return new TargetPanel((NoteTreeNode) value);
        }
        return new JLabel("#ROOT");
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        return false;
    }

    @Override
    public boolean stopCellEditing() {
        return false;
    }

    @Override
    public void cancelCellEditing() {

    }

    @Override
    public void addCellEditorListener(CellEditorListener l) {

    }

    @Override
    public void removeCellEditorListener(CellEditorListener l) {

    }

    final class UserActionForPinNoteTreeCellEditor implements UserAction {

        private final AbstractTaskTreeNode value;

        private final JTree tree;

        UserActionForPinNoteTreeCellEditor(JTree tree, AbstractTaskTreeNode value) {
            this.value = value;
            this.tree = tree;
        }


        @Override
        public void newTask(boolean isRoot) {
            AbstractTaskTreeNode abstractTaskTreeNode = null;
            if (value instanceof TaskTreeNode || isRoot) {
                abstractTaskTreeNode = new TaskTreeNode(Task.builder().build());
            } else if (value instanceof SubTaskTreeNode) {
                abstractTaskTreeNode = new SubTaskTreeNode(Task.builder().build());
            }

            if (abstractTaskTreeNode != null) {
                abstractTaskTreeNode.setCreationReason(CreationReason.USER);
                var parent = ((DefaultMutableTreeNode) (value).getParent());
                if (parent instanceof AbstractTaskTreeNode && isRoot) {
                    parent = (DefaultMutableTreeNode) parent.getParent();
                }
                parent.add(abstractTaskTreeNode);
                if (parent instanceof AbstractTaskTreeNode) {
                    ((Task) parent.getUserObject()).setChecked(false);
                }
                tree.updateUI();
            }
        }

        @Override
        public void newSubTask() {
            AbstractTaskTreeNode abstractTaskTreeNode = null;
            if (value instanceof TaskTreeNode) {
                value.getUserObject().setChecked(false);
                abstractTaskTreeNode = new SubTaskTreeNode(Task.builder().build());
            }

            if (abstractTaskTreeNode != null) {
                abstractTaskTreeNode.setCreationReason(CreationReason.USER);
                value.add(abstractTaskTreeNode);
                tree.updateUI();
                tree.expandPath(new TreePath(((DefaultMutableTreeNode) abstractTaskTreeNode.getParent()).getPath()));
            }
        }

        @Override
        public void selectPreviousTask() {
            AbstractTaskTreeNode contextValue = value;

            var parent = contextValue.getParent();
            var index = parent.getIndex(contextValue);

            if (index == -1) {
                return;
            }

            AbstractTaskTreeNode target = null;
            if (contextValue instanceof SubTaskTreeNode && index == 0) {
                contextValue = (AbstractTaskTreeNode) contextValue.getParent();
                parent = contextValue.getParent();
                index = parent.getIndex(contextValue);
                target = (AbstractTaskTreeNode) parent.getChildAt(index);
            } else if (index > 0) {
                target = (AbstractTaskTreeNode) parent.getChildAt(index - 1);
                if (target instanceof TaskTreeNode && tree.isExpanded(new TreePath(target.getPath()))) {
                    target = (AbstractTaskTreeNode) target.getChildAt(target.getChildCount() - 1);
                }
            }
            this.setEditingAtTarget(target);
        }

        @Override
        public void selectNextTask() {
            AbstractTaskTreeNode contextValue = value;

            var parent = contextValue.getParent();
            var index = parent.getIndex(contextValue);
            if (index == -1) {
                return;
            }

            AbstractTaskTreeNode target = null;


            if (tree.isExpanded(new TreePath(contextValue.getPath()))) {
                target = (AbstractTaskTreeNode) contextValue.getChildAt(0);
            } else if (contextValue instanceof SubTaskTreeNode && parent.getChildCount() - 1 == index) {
                contextValue = (AbstractTaskTreeNode) contextValue.getParent();
                parent = contextValue.getParent();
                index = parent.getIndex(contextValue);
                if (parent.getChildCount() - 1 != index) {
                    target = (AbstractTaskTreeNode) parent.getChildAt(index + 1);
                }
            } else if (parent.getChildCount() - 1 > index) {
                target = (AbstractTaskTreeNode) parent.getChildAt(index + 1);
            }

            this.setEditingAtTarget(target);
        }


        @Override
        public void onEditing() {
            tree.clearSelection();
        }

        private void setEditingAtTarget(@Nullable AbstractTaskTreeNode target) {
            if (target != null) {
                var cursorPath = new TreePath(target.getPath());
                SwingUtilities.invokeLater(() -> tree.startEditingAtPath(cursorPath));
            }
        }
    }
}
