package com.helpdesk.exception;

public class RevokedTokenException extends RuntimeException {

    public RevokedTokenException(String message) {
        super(message);
    }
}