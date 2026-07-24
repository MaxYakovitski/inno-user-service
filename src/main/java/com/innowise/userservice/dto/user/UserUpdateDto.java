package com.innowise.userservice.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserUpdateDto(
        @Size(max = 128) String name,
        @Size(max = 128) String surname,
        LocalDate birthDate,
        @Email String email
) {}
