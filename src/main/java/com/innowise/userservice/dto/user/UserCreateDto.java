package com.innowise.userservice.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * @author ma_yak
 */

public record UserCreateDto(
        @NotBlank @Size(max = 128) String name,
        @NotBlank @Size(max = 128) String surname,
        @NotNull LocalDate birthDate,
        @NotBlank @Email @Size(max = 255) String email
) {}
