package com.innowise.userservice.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * @author ma_yak
 */

public record UserUpdateDto(
        @Size(max = 128) String name,
        @Size(max = 128) String surname,
        LocalDate birthDate,
        @Email String email
) {}
