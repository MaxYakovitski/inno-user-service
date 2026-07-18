package com.innowise.userservice.dto.paymentcard;

import java.time.Instant;
import java.time.LocalDate;

/**
 * @author ma_yak
 */

public record PaymentCardResponseDto(
        Long id,
        Long userId,
        String number,
        String holder,
        LocalDate expirationDate,
        Boolean active,
        Instant createdAt,
        Instant updatedAt
) {}
