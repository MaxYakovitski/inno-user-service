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
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author ma_yak
 */

@Service
@RequiredArgsConstructor
public class PaymentCardCommandServiceImpl implements PaymentCardCommandService {

    private final PaymentCardRepository paymentCardRepository;
    private final PaymentCardMapper paymentCardMapper;

    private final UserRepository userRepository;
    private final RedisCacheManager cacheManager;

    private final static int MAX_CARDS_PER_USER = 5;

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#dto.userId()")
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
    @CacheEvict(value = "users", key = "#result.userId()")
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
        evictUserCache(card.getUser().getId());
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment card not found: " + id));
        card.setActive(false);
        evictUserCache(card.getUser().getId());
    }

    private void evictUserCache(Long userId) {
        Cache cache = cacheManager.getCache("users");
        if (cache != null) {
            cache.evict(userId);
        }
    }
}
