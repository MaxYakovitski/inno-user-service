package com.innowise.userservice.exception;

import java.util.UUID;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static  ResourceNotFoundException paymentCard(UUID id) {
        return new ResourceNotFoundException("Payment card not found: " + id);
    }

    public static  ResourceNotFoundException user(UUID id) {
        return new ResourceNotFoundException("User not found: " + id);
    }
}
