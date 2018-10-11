/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note.visual.exception;

/**
 * Basic runtime exception for visual adapters
 */
public class VisualAdapterException extends RuntimeException {
    public VisualAdapterException(String message) {
        super(message);
    }

    public VisualAdapterException(String message, Throwable cause) {
        super(message, cause);
    }
}
