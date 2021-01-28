package org.bigtows.window.ui.notetree.tree.editor;

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
                        public void newTask(boolean isRoot) {
                            AbstractTaskTreeNode abstractTaskTreeNode = null;
                            if (value instanceof TaskTreeNode || isRoot) {
                                abstractTaskTreeNode = new TaskTreeNode(Task.builder().build());
                            } else if (value instanceof SubTaskTreeNode) {
                                abstractTaskTreeNode = new SubTaskTreeNode(Task.builder().build());
                            }

                            if (abstractTaskTreeNode != null) {
                                abstractTaskTreeNode.setCreationReason(CreationReason.USER);
                                var parent = ((DefaultMutableTreeNode) (sourceTaskTreeNode).getParent());
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
                                ((TaskTreeNode) value).getUserObject().setChecked(false);
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
                            var parent = ((AbstractTaskTreeNode) value).getParent();
                            int indexValue = parent.getIndex((AbstractTaskTreeNode) value);
                            ((AbstractTaskTreeNode) value).removeFromParent();
                            if (parent instanceof NoteTreeNode && parent.getChildCount() == 0) {
                                ((NoteTreeNode) parent).add(new TaskTreeNode(Task.builder().build()));
                            }
                            tree.updateUI();
                            treeChanged.onChange();


                            //Set cursor at nearby position
                            TreePath cursorPath;
                            if (parent instanceof TaskTreeNode && parent.getChildCount() == 0) {
                                cursorPath = new TreePath(((TaskTreeNode) parent).getPath());
                            } else {
                                cursorPath = new TreePath(((AbstractTaskTreeNode) parent.getChildAt(
                                        parent.getChildCount() - 1 < indexValue ? indexValue - 1 : indexValue
                                )).getPath());
                            }

                            SwingUtilities.invokeLater(() -> {
                                tree.startEditingAtPath(cursorPath);
                            });
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
                    new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            tree.getSelectionModel().clearSelection();
                            tree.setSelectionPath(new TreePath(((NoteTreeNode) value).getPath()));
                        }
                    }
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
