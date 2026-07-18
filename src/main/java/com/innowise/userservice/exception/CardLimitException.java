package com.innowise.userservice.exception;

/**
 * @author ma_yak
 */

public class CardLimitException extends RuntimeException {
    public CardLimitException(String message) {
        super(message);
    }
}
