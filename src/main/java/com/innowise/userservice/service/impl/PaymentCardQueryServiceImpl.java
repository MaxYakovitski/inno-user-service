package com.innowise.userservice.service.impl;

import com.innowise.userservice.dto.paymentcard.PaymentCardResponseDto;
import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.exception.ResourceNotFoundException;
import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.specification.PaymentCardSpecification;
import com.innowise.userservice.service.PaymentCardQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentCardQueryServiceImpl implements PaymentCardQueryService {

    private final PaymentCardRepository paymentCardRepository;
    private final PaymentCardMapper paymentCardMapper;

    @Override
    @Transactional(readOnly = true)
    public PaymentCardResponseDto getById(UUID id) {
        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.paymentCard(id));
        return paymentCardMapper.toDto(card);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentCardResponseDto> getAllByUserId(UUID id) {
        List<PaymentCard> cards = paymentCardRepository.findByUserId(id);
        return cards.stream().map(paymentCardMapper::toDto).toList();
    }

    @Override
    public Page<PaymentCardResponseDto> getAll(String holder, Pageable pageable) {
        Specification<PaymentCard> specification = Specification
                .where(PaymentCardSpecification.hasHolder(holder));
        return paymentCardRepository.findAll(specification, pageable)
                .map(paymentCardMapper::toDto);
    }
}
