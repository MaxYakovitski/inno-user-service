package com.innowise.userservice.exception;

/**
 * @author ma_yak
 */

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
