/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.window.component.form;

import org.bigtows.window.component.form.event.SettingsWindowLogoutEvent;

import javax.swing.*;

/**
 * The type Settings window form.
 */
public class SettingsWindowForm {
    private JPanel root;
    private JButton logoutButton;

    private final String DEFAULT_NAME_LOGOUT_BUTTON = "Logout, %_USER_%.";

    private SettingsWindowLogoutEvent storageEvent = null;

    public SettingsWindowForm() {
        logoutButton.addActionListener(listener -> {
            if (storageEvent != null) {
                storageEvent.onEvent();
            }
        });
    }

    /**
     * Gets root.
     *
     * @return the root
     */
    public JPanel getRoot() {
        return root;
    }

    /**
     * Sets account name.
     *
     * @param accountName the account name
     */
    public void setAccountName(String accountName) {
        if (accountName == null) {
            logoutButton.setEnabled(false);
        } else {
            logoutButton.setEnabled(true);
            logoutButton.setText(DEFAULT_NAME_LOGOUT_BUTTON.replace("%_USER_%", accountName));
        }
    }


    public void setOnLogout(SettingsWindowLogoutEvent event) {
        this.storageEvent = event;
    }
}
