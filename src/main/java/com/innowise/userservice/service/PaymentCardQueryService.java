package com.innowise.userservice.service;

import com.innowise.userservice.dto.paymentcard.PaymentCardResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author ma_yak
 */

public interface PaymentCardQueryService {

    PaymentCardResponseDto getById(Long id);
    List <PaymentCardResponseDto> getAllByUserId(Long userId);

    Page<PaymentCardResponseDto> getAll(String holder, Pageable pageable);
}
