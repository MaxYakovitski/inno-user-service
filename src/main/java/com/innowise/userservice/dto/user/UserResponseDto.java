package com.innowise.userservice.dto.user;

import com.innowise.userservice.dto.paymentcard.PaymentCardResponseDto;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String name,
        String surname,
        LocalDate birthDate,
        String email,
        Boolean active,
        List<PaymentCardResponseDto> cards,
        Instant createdAt,
        Instant updatedAt
) implements Serializable {}
