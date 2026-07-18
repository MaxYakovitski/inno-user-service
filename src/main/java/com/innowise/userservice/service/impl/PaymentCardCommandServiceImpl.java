package com.innowise.userservice.service.impl;

import com.innowise.userservice.dto.paymentcard.PaymentCardCreateDto;
import com.innowise.userservice.dto.paymentcard.PaymentCardResponseDto;
import com.innowise.userservice.dto.paymentcard.PaymentCardUpdateDto;
import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.CardLimitException;
import com.innowise.userservice.exception.ResourceNotFoundException;
import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.PaymentCardCommandService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author ma_yak
 */

@Service
@AllArgsConstructor
public class PaymentCardCommandServiceImpl implements PaymentCardCommandService {

    private final PaymentCardRepository paymentCardRepository;
    private final PaymentCardMapper paymentCardMapper;

    private final static int MAX_CARDS_PER_USER = 5;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PaymentCardResponseDto create(PaymentCardCreateDto dto) {
        long currentCardsQty = paymentCardRepository.countByUserId(dto.userId());
        if (currentCardsQty >= MAX_CARDS_PER_USER) {
            throw new CardLimitException("User: " + dto.userId() + " already has the maximum of " + MAX_CARDS_PER_USER + " cards");
        }
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + dto.userId()));
        PaymentCard card = paymentCardMapper.toEntity(dto);
        card.setUser(user);
        card.setActive(true);
        return paymentCardMapper.toDto(paymentCardRepository.save(card));
    }

    @Override
    @Transactional
    public PaymentCardResponseDto update(Long id, PaymentCardUpdateDto dto) {
        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment card not found: " + id));
        paymentCardMapper.updateEntity(dto, card);
        return paymentCardMapper.toDto(card);
    }

    @Override
    @Transactional
    public void activate(Long id) {
        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment card not found: " + id));
        card.setActive(true);
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment card not found: " + id));
        card.setActive(false);
    }
}
