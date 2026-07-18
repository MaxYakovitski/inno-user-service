package com.innowise.userservice.dto.user;

import java.time.Instant;
import java.time.LocalDate;

/**
 * @author ma_yak
 */

public record UserResponseDto(
        Long id,
        String name,
        String surname,
        LocalDate birthDate,
        String email,
        Boolean active,
        Instant createdAt,
        Instant updatedAt
) {}
