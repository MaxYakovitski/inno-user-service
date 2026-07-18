package com.innowise.userservice.service;

import com.innowise.userservice.dto.paymentcard.PaymentCardCreateDto;
import com.innowise.userservice.dto.paymentcard.PaymentCardResponseDto;
import com.innowise.userservice.dto.paymentcard.PaymentCardUpdateDto;

/**
 * @author ma_yak
 */

public interface PaymentCardCommandService {

    PaymentCardResponseDto create(PaymentCardCreateDto dto);
    PaymentCardResponseDto update(Long id, PaymentCardUpdateDto dto);

    void activate(Long id);
    void deactivate(Long id);
}
