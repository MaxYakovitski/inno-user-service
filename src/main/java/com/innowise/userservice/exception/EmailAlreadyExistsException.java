package com.innowise.userservice.exception;

/**
 * @author ma_yak
 */

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }

    public static EmailAlreadyExistsException emailAlreadyExists(String email) {
        return new EmailAlreadyExistsException("User with following: " + email +  " already exists");
    }
}
