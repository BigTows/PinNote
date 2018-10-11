/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note.exception;

/**
 * Throws when storage already has target
 */
public class TargetAlreadyCreated extends NoteException {

    public TargetAlreadyCreated(String message) {
        super(message);
    }

    public TargetAlreadyCreated(String message, Throwable cause) {
        super(message, cause);
    }
}
