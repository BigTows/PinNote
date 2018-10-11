/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: PinNote
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package org.bigtows.note.storage.exception;

public class SaveNotesException extends StorageException {
    public SaveNotesException(String message) {
        super(message);
    }

    public SaveNotesException(String message, Throwable cause) {
        super(message, cause);
    }
}
