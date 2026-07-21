package com.innowise.userservice.exception;

/**
 * @author ma_yak
 */

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static  ResourceNotFoundException paymentCard(Long id) {
        return new ResourceNotFoundException("Payment card not found: " + id);
    }

    public static  ResourceNotFoundException user(Long id) {
        return new ResourceNotFoundException("User not found: " + id);
    }
}
