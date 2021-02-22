package org.bigtows.window.ui.notetree.tree.component;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.ui.JBUI;
import org.bigtows.window.ui.notetree.tree.event.MultiKeyAdapter;
import org.bigtows.window.ui.notetree.tree.event.TreeChanged;
import org.bigtows.window.ui.notetree.tree.event.UserAction;
import org.bigtows.window.ui.notetree.tree.node.AbstractTaskTreeNode;
import org.bigtows.window.ui.text.JTextFieldWithPlaceholder;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;

/**
 * Panel of task
 */
public final class TaskPanel extends JPanel {

    /**
     * Task data
     */
    private final AbstractTaskTreeNode source;

    /**
     * Text filed instance with placeholder
     */
    public final JTextFieldWithPlaceholder textField = new JTextFieldWithPlaceholder("...");
    /**
     * Checkbox for display of status task
     */
    public final JBCheckBox check = new JBCheckBox();

    /**
     * Notify about changes
     */
    private final TreeChanged treeChanged;

    /**
     * Constructor.
     *
     * @param value       data of task
     * @param userAction  user actions
     * @param treeChanged callback
     */
    public TaskPanel(AbstractTaskTreeNode value, UserAction userAction, TreeChanged treeChanged) {
        this.treeChanged = treeChanged;
        this.source = value;
        setLayout(new BorderLayout());
        add(check, BorderLayout.WEST);
        add(textField, BorderLayout.CENTER);


        this.initializeCheckbox(userAction);
        this.initializeTextField(userAction);

    }

    /**
     * Initialize checkbox view
     *
     * @param userAction callback of user actions
     */
    private void initializeCheckbox(UserAction userAction) {
        this.check.setMargin(JBUI.emptyInsets());
        check.setSelected(this.source.getUserObject().getChecked());
        check.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                userAction.onEditing();
                textField.requestFocus();
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });
        check.addItemListener(this::onCheckBoxChange);
    }

    /**
     * Initialize text field
     */
    private void initializeTextField(UserAction userAction) {
        textField.setSize(textField.getWidth() + 500, textField.getHeight() + 4);
        textField.setText(this.source.getUserObject().getText());
        textField.setOpaque(false);


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

        textField.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, new HashSet<>());

        textField.addKeyListener(new MultiKeyAdapter() {
            @Override
            public void keyPressed() {
                if (super.hasKeys(KeyEvent.VK_SHIFT, KeyEvent.VK_ENTER)) {
                    userAction.newTask(true);
                } else if (super.hasKeys(KeyEvent.VK_ENTER)) {
                    userAction.newTask(false);
                } else if (super.hasKeys(KeyEvent.VK_TAB)) {
                    userAction.newSubTask();
                } else if (super.hasKeys(KeyEvent.VK_UP)) {
                    userAction.selectPreviousTask();
                } else if (super.hasKeys(KeyEvent.VK_DOWN)) {
                    userAction.selectNextTask();
                }
            }
        });

        textField.setBorder(BorderFactory.createEmptyBorder());
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
}
