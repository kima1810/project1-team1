package org.half.exceptions;

public class EmptyStringException extends IllegalArgumentException {
    public EmptyStringException(String message) {
        super(message);
    }
}
