package com.innowise.userservice.dto.paymentcard;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record PaymentCardCreateDto(
        @NotNull UUID userId,
        @NotBlank @Size(max = 32) String number,
        @NotBlank @Size(max = 128) String holder,
        @NotNull LocalDate expirationDate
) {}
