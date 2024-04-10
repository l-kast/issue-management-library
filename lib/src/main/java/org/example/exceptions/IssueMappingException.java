package org.example.exceptions;

public class IssueMappingException extends RuntimeException {

    public IssueMappingException(String message) {
        super(message);
    }

    // Optional: Constructor that takes a message and a cause
    public IssueMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
