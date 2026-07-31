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
import com.innowise.userservice.service.PaymentCardCommandService;
import com.innowise.userservice.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentCardCommandServiceImpl implements PaymentCardCommandService {

    private final PaymentCardRepository paymentCardRepository;
    private final PaymentCardMapper paymentCardMapper;

    private final UserQueryService userQueryService;
    private final RedisCacheManager cacheManager;

    private static final int MAX_CARDS_PER_USER = 5;

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public PaymentCardResponseDto create(UUID userId, PaymentCardCreateDto dto) {
        User user = userQueryService.getUserWithCardsForUpdate(userId);
        if (user.getCards().size() >= MAX_CARDS_PER_USER) {
            throw new CardLimitException("User: " + userId + " already has the maximum of " + MAX_CARDS_PER_USER + " cards");
        }
        PaymentCard card = paymentCardMapper.toEntity(dto);
        card.setUser(user);
        card.setActive(true);
        return paymentCardMapper.toDto(paymentCardRepository.save(card));
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#result.userId()")
    public PaymentCardResponseDto update(UUID id, PaymentCardUpdateDto dto) {
        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.paymentCard(id));
        paymentCardMapper.updateEntity(dto, card);
        return paymentCardMapper.toDto(card);
    }

    @Override
    @Transactional
    public void activate(UUID id) {
        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.paymentCard(id));
        card.setActive(true);
        evictUserCache(card.getUser().getId());
    }

    @Override
    @Transactional
    public void deactivate(UUID id) {
        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.paymentCard(id));
        card.setActive(false);
        evictUserCache(card.getUser().getId());
    }

    private void evictUserCache(UUID userId) {
        Cache cache = cacheManager.getCache("users");
        if (cache != null) {
            cache.evict(userId);
        }
    }
}
