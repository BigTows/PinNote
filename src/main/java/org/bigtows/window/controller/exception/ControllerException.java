/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.window.controller.exception;

/**
 * The type Controller exception.
 */
public class ControllerException extends RuntimeException {
    /**
     * Instantiates a new Controller exception.
     *
     * @param message the message
     */
    public ControllerException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Controller exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public ControllerException(String message, Throwable cause) {
        super(message, cause);
    }
}
