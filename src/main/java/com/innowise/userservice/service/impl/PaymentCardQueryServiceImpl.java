package com.innowise.userservice.service.impl;

import com.innowise.userservice.dto.paymentcard.PaymentCardResponseDto;
import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.specification.PaymentCardSpecification;
import com.innowise.userservice.service.PaymentCardQueryService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author ma_yak
 */

@Service
@AllArgsConstructor
public class PaymentCardQueryServiceImpl implements PaymentCardQueryService {

    private final PaymentCardRepository paymentCardRepository;
    private final PaymentCardMapper paymentCardMapper;

    @Override
    @Transactional(readOnly = true)
    public PaymentCardResponseDto getById(Long id) {
        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment card not found: " + id));
        return paymentCardMapper.toDto(card);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentCardResponseDto> getAllByUserId(Long id) {
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
