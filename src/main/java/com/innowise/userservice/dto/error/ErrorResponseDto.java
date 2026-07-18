package com.innowise.userservice.dto.error;

/**
 * @author ma_yak
 */

public record ErrorResponseDto(
        String code,
        String message
) {}
