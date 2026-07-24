package com.innowise.userservice.dto.paymentcard;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;



public record PaymentCardResponseDto(
        UUID id,
        UUID userId,
        String number,
        String holder,
        LocalDate expirationDate,
        Boolean active,
        Instant createdAt,
        Instant updatedAt
) implements Serializable {}
