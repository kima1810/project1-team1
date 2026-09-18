package org.half.exceptions;

public class DatabaseConnectionFailure extends RuntimeException {
    public DatabaseConnectionFailure(String message) {
        super(message);
    }
}
