package com.innowise.userservice.service;

import com.innowise.userservice.dto.paymentcard.PaymentCardCreateDto;
import com.innowise.userservice.dto.paymentcard.PaymentCardResponseDto;
import com.innowise.userservice.dto.paymentcard.PaymentCardUpdateDto;

import java.util.UUID;

/**
 * @author ma_yak
 */

public interface PaymentCardCommandService {

    PaymentCardResponseDto create(PaymentCardCreateDto dto);
    PaymentCardResponseDto update(UUID id, PaymentCardUpdateDto dto);

    void activate(UUID id);
    void deactivate(UUID id);
}
