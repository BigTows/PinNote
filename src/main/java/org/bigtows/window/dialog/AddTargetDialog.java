/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.window.dialog;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog about added new target
 */
public class AddTargetDialog extends DialogWrapper {


    /**
     * Component JPanel
     */
    private JPanel panel;

    /**
     * Constructor override
     *
     * @param canBeParent specifies whether the dialog can be parent for other windows. This parameter is used
     *                    by {@code WindowManager}.
     */
    public AddTargetDialog(boolean canBeParent) {
        super(canBeParent);
        init();
    }


    @Override
    protected void init() {
        JLabel label = new JLabel("Target name:");
        JTextField textFieldTarget = new JTextField("");

        panel = new JPanel(new BorderLayout());
        panel.add(label, BorderLayout.WEST);
        panel.add(textFieldTarget, BorderLayout.CENTER);
        super.init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return panel;
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        JTextField textField = this.getTextField();

        if (textField.getText().length() == 0) {
            return new ValidationInfo("The target name must contain at least 1 character", textField);
        }

        return null;
    }

    /**
     * Return name target
     * Recommended use this method, when user click "OK"
     * <code>
     * if (true == dialog.showAndGet()){
     * String nameTarget = dialog.getNameTarget();
     * }
     * </code>
     *
     * @return name of target
     */
    public String getNameTarget() {
        return this.getTextField().getText();
    }

    /**
     * Return TextField from Form
     *
     * @return Component TextField
     */
    private JTextField getTextField() {
        return (JTextField) panel.getComponent(1);
    }

}
