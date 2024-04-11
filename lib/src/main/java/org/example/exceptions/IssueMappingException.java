package org.example.exceptions;

public class IssueMappingException extends RuntimeException {

    public IssueMappingException(String message) {
        super(message);
    }

    public IssueMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
