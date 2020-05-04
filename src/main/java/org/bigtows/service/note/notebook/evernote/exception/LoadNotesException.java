package org.bigtows.service.note.notebook.evernote.exception;

public class LoadNotesException extends RuntimeException {

    public LoadNotesException(String message) {
        super(message);
    }

    public LoadNotesException(String message, Throwable cause) {
        super(message, cause);
    }
}
