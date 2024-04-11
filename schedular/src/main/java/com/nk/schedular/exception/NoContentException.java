package com.nk.schedular.exception;

public class NoContentException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NoContentException(String message) {
        super(message);
    }
}

