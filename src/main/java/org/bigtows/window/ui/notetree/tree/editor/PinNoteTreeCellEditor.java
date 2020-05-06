package org.bigtows.window.ui.notetree.tree.editor;

import org.bigtows.window.ui.menu.DeletePopupMenu;
import org.bigtows.window.ui.menu.adapter.ClickMouseAdapter;
import org.bigtows.window.ui.notetree.tree.TaskPanel;
import org.bigtows.window.ui.notetree.tree.entity.Task;
import org.bigtows.window.ui.notetree.tree.event.TreeChanged;
import org.bigtows.window.ui.notetree.tree.event.UserShortcutPressed;
import org.bigtows.window.ui.notetree.tree.node.*;

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
                                tree.updateUI();
                                tree.expandPath(new TreePath(((DefaultMutableTreeNode) abstractTaskTreeNode.getParent()).getPath()));
                            }
                        }

                        @Override
                        public void delete() {
                            ((AbstractTaskTreeNode) value).removeFromParent();
                            tree.updateUI();
                            treeChanged.onChange();
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
            var currentValue = (NoteTreeNode) value;
            var label = new JLabel(currentValue.getUserObject().getName());
            label.addMouseListener(
                    new ClickMouseAdapter(
                            new DeletePopupMenu(
                                    actionEvent -> {
                                        var treeNode = ((DefaultMutableTreeNode) tree.getModel().getRoot());
                                        treeNode.remove(currentValue);
                                        tree.updateUI();
                                        treeChanged.onChange();
                                    }
                            )
                    )
            );
            return label;
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
}
