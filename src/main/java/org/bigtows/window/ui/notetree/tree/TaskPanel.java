package org.bigtows.window.ui.notetree.tree;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.ui.JBUI;
import org.bigtows.window.ui.menu.DeletePopupMenu;
import org.bigtows.window.ui.menu.adapter.RightClickPopupMenuMouseAdapter;
import org.bigtows.window.ui.notetree.tree.event.TreeChanged;
import org.bigtows.window.ui.notetree.tree.event.UserShortcutPressed;
import org.bigtows.window.ui.notetree.tree.node.AbstractTaskTreeNode;
import org.bigtows.window.ui.text.JTextFieldWithPlaceholder;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Collections;

public class TaskPanel extends JPanel {
    private final AbstractTaskTreeNode source;

    /**
     * Text filed instance with placeholder
     */
    public final JTextFieldWithPlaceholder textField = new JTextFieldWithPlaceholder("...");
    public final JBCheckBox check = new JBCheckBox();
    private final TreeChanged treeChanged;

    public TaskPanel(AbstractTaskTreeNode value, UserShortcutPressed userShortcutPressed, TreeChanged treeChanged) {
        this.treeChanged = treeChanged;
        this.source = value;
        this.check.setMargin(JBUI.emptyInsets());
        setLayout(new BorderLayout());
        add(check, BorderLayout.WEST);
        add(textField, BorderLayout.CENTER);
        textField.setSize(textField.getWidth() + 500, textField.getHeight() + 4);
        textField.setText(this.source.getUserObject().getText());
        textField.setOpaque(false);
        check.setSelected(this.source.getUserObject().getChecked());
        check.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.requestFocus();
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });
        var panel = this;
        textField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                onChange();
            }

            public void removeUpdate(DocumentEvent e) {
                onChange();
            }

            public void insertUpdate(DocumentEvent e) {
                onChange();
            }

            public void onChange() {
                source.getUserObject().setText(textField.getText());
                var width = textField.getText().length() * 7 + 1000;
                textField.setSize(new Dimension(width, textField.getHeight()));
                panel.setSize(new Dimension(width, panel.getHeight()));
                treeChanged.onChange();
            }
        });
        check.addItemListener(this::onCheckBoxChange);
        textField.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);

        textField.addMouseListener(new RightClickPopupMenuMouseAdapter(
                new DeletePopupMenu(
                        actionEvent -> userShortcutPressed.delete()
                ))
        );

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getExtendedKeyCode() == KeyEvent.VK_ENTER
                        || e.getKeyCode() == KeyEvent.VK_ENTER
                        || e.getKeyChar() == KeyEvent.VK_ENTER
                ) {
                    userShortcutPressed.newTask();
                } else if (e.getExtendedKeyCode() == KeyEvent.VK_TAB
                        || e.getKeyCode() == KeyEvent.VK_TAB
                        || e.getKeyChar() == KeyEvent.VK_TAB
                ) {
                    userShortcutPressed.newSubTask();
                }
            }

        });
        this.calculateBorder();
    }

    /**
     * Mechanic "Mechanic automatic task completion" calling after check box select changed
     *
     * @param event event
     */
    private void onCheckBoxChange(ItemEvent event) {
        source.getUserObject().setChecked(check.isSelected());

        if (source.getParent() instanceof AbstractTaskTreeNode) {
            var parent = (AbstractTaskTreeNode) source.getParent();
            parent.getUserObject().setChecked(checkAllSubTaskSelected(parent));
        } else {
            for (int i = 0; i < source.getChildCount(); i++) {
                var children = source.getChildAt(i);
                if (children instanceof AbstractTaskTreeNode) {
                    //TODO if need recursion
                    ((AbstractTaskTreeNode) children).getUserObject().setChecked(check.isSelected());
                }
            }
        }
        treeChanged.onChange();
    }

    /**
     * Check all subTask for selected
     *
     * @param parent root of task
     * @return {@code true} if all subTask of root(parent) selected else {@code false}
     */
    private boolean checkAllSubTaskSelected(AbstractTaskTreeNode parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            var children = parent.getChildAt(i);
            //TODO if need recursion
            if (children instanceof AbstractTaskTreeNode) {
                if (!((AbstractTaskTreeNode) children).getUserObject().getChecked()) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    private void calculateBorder() {
        textField.setBorder(BorderFactory.createEmptyBorder());
    }
}
