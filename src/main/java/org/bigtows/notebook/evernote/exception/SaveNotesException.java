package org.bigtows.notebook.evernote.exception;

public class SaveNotesException extends RuntimeException {

    public SaveNotesException(String message) {
        super(message);
    }

    public SaveNotesException(String message, Throwable cause) {
        super(message, cause);
    }
}
