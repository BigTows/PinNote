/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.components;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.ApplicationComponent;

import javax.swing.*;

public class PinNoteNotification implements ApplicationComponent {


    /**
     * Error notification
     *
     * @param title
     * @param e
     */
    public void errorNotification(String title, Exception e) {

        Notification notification = new Notification(
                "Exception-" + e.getClass(),
                title,
                e.getMessage(),
                NotificationType.ERROR
        );

        this.initializeNotification(notification);
    }

    /**
     * Error notification
     *
     * @param title
     */
    public void errorNotification(String title) {

        Notification notification = new Notification(
                "Exception-" + title,
                title,
                "",
                NotificationType.ERROR
        );

        this.initializeNotification(notification);
    }


    private void initializeNotification(Notification notification) {
        SwingUtilities.invokeLater(() -> notification.notify(null));
    }

}
