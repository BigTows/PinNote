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
import com.intellij.openapi.components.BaseComponent;

import javax.swing.*;

public class PinNoteNotification implements BaseComponent {


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
                "Please create issues: " + e.getMessage() + "\n" + this.getTextStackTraceByStackTrace(e.getStackTrace()),
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

    private String getTextStackTraceByStackTrace(StackTraceElement[] stackTraceElements) {
        StringBuilder builder = new StringBuilder();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            builder.append(stackTraceElement.toString()).append("\n");
        }
        return builder.toString();
    }

    public void errorNotification(String title, String content) {
        Notification notification = new Notification(
                title,
                title,
                content,
                NotificationType.ERROR
        );

        this.initializeNotification(notification);
    }


    private void initializeNotification(Notification notification) {
        SwingUtilities.invokeLater(() -> notification.notify(null));
    }

}
