package org.bigtows.components.exception;

/**
 * The type Pin note component exception.
 */
public class PinNoteComponentException extends RuntimeException {
    /**
     * Instantiates a new Pin note component exception.
     *
     * @param message the message
     */
    public PinNoteComponentException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Pin note component exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public PinNoteComponentException(String message, Throwable cause) {
        super(message, cause);
    }
}
