package org.bigtows.window.ui.notetree.tree;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.ui.JBUI;
import org.bigtows.window.ui.menu.DeletePopupMenu;
import org.bigtows.window.ui.menu.adapter.RightClickMouseAdapter;
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

    public final JTextFieldWithPlaceholder textField = new JTextFieldWithPlaceholder("New value.");
    public final JBCheckBox check = new JBCheckBox();
    private final TreeChanged treeChanged;

    public TaskPanel(AbstractTaskTreeNode value, UserShortcutPressed userShortcutPressed, TreeChanged treeChanged) {
        this.treeChanged = treeChanged;
        this.source = value;
        this.check.setMargin(JBUI.emptyInsets());
        setLayout(new BorderLayout());
        add(check, BorderLayout.WEST);
        add(textField, BorderLayout.CENTER);
        textField.setSize(textField.getWidth(), textField.getHeight() + 4);
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
                treeChanged.onChange();
            }
        });
        check.addItemListener(this::onCheckBoxChange);
        textField.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.EMPTY_SET);

        textField.addMouseListener(new RightClickMouseAdapter(
                new DeletePopupMenu(
                        actionEvent -> {
                            userShortcutPressed.delete();
                            treeChanged.onChange();
                        }
                ))
        );

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getExtendedKeyCode() == KeyEvent.VK_ENTER) {
                    userShortcutPressed.newTask();
                } else if (e.getExtendedKeyCode() == KeyEvent.VK_TAB) {
                    userShortcutPressed.newSubTask();
                }
            }

        });
        this.calculateBorder();
    }

    private void onCheckBoxChange(ItemEvent event) {
        source.getUserObject().setChecked(check.isSelected());
        treeChanged.onChange();
    }

    private void calculateBorder() {
        textField.setBorder(BorderFactory.createEmptyBorder());
    }
}
