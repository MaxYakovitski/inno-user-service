package com.innowise.userservice.service;

import com.innowise.userservice.dto.paymentcard.PaymentCardResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface PaymentCardQueryService {

    PaymentCardResponseDto getById(UUID id);
    List <PaymentCardResponseDto> getAllByUserId(UUID userId);

    Page<PaymentCardResponseDto> getAll(String holder, Pageable pageable);
}
