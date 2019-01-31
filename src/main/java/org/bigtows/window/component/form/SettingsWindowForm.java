/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.window.component.form;

import org.bigtows.window.component.form.event.SettingsWindowLogoutEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

/**
 * The type Settings window form.
 */
public class SettingsWindowForm {

    /**
     * Logger
     */
    private Logger logger = LoggerFactory.getLogger("Settings window form");

    /**
     * Root panel
     */
    private JPanel root;

    /**
     * Button for logout
     */
    private JButton logoutButton;

    /**
     * Default name logout button
     */
    private final String DEFAULT_NAME_LOGOUT_BUTTON = "Logout.";

    /**
     * Default name login button
     */
    private final String DEFAULT_NAME_LOGIN_BUTTON = "Please login.";

    /**
     * Path to resource icon
     */
    private final String pathToResource = "/icons/evernote.png";

    /**
     * Event callable
     */
    private SettingsWindowLogoutEvent storageEvent = null;

    /**
     * Instantiates a new Settings window form.
     */
    public SettingsWindowForm() {
        this.initializeIcon();
        logoutButton.addActionListener(listener -> {
            if (storageEvent != null) {
                storageEvent.onEvent();
            }
        });
    }

    /**
     * Initialize icon
     */
    private void initializeIcon() {
        try {
            Image image = ImageIO.read(getClass().getResource(pathToResource));
            logoutButton.setIcon(new ImageIcon(FormUtility.getScaledImage(image, 30, 30)));
        } catch (Exception e) {
            logger.error("Error load icon", e);
        }
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
     * Change view if client is logged
     *
     * @param clientIsLogged status client
     */
    public void setClientIsLogged(boolean clientIsLogged) {
        if (clientIsLogged) {
            logoutButton.setEnabled(true);
            logoutButton.setText(DEFAULT_NAME_LOGOUT_BUTTON);
        } else {
            logoutButton.setEnabled(false);
            logoutButton.setText(DEFAULT_NAME_LOGIN_BUTTON);
        }
    }

    /**
     * Sets on logout.
     *
     * @param event the event
     */
    public void setOnLogout(SettingsWindowLogoutEvent event) {
        this.storageEvent = event;
    }
}
