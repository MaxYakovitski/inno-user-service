package com.innowise.userservice.dto.user;

import com.innowise.userservice.dto.paymentcard.PaymentCardResponseDto;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

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
        List<PaymentCardResponseDto> cards,
        Instant createdAt,
        Instant updatedAt
) implements Serializable {}
