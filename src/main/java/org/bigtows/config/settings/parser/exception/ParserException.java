package org.bigtows.config.settings.parser.exception;

public class ParserException extends Exception {
    public ParserException(String message) {
        super(message);
    }

    public ParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
