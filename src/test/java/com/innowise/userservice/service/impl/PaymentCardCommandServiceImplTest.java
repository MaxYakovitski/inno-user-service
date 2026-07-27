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
import com.innowise.userservice.service.UserQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheManager;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCardCommandServiceImplTest {

    @Mock
    private PaymentCardRepository paymentCardRepository;

    @Mock
    private PaymentCardMapper paymentCardMapper;

    @Mock
    private UserQueryService userQueryService;

    @Mock
    private RedisCacheManager cacheManager;

    @Mock
    Cache cache;

    @InjectMocks
    private PaymentCardCommandServiceImpl paymentCardCommandService;

    private static final int MAX_CARDS_PER_USER = 5;

    UUID id = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    Instant now = Instant.now();

    User savedUser = User.builder().id(userId).build();

    PaymentCardCreateDto paymentCardCreateDto = new PaymentCardCreateDto(
            userId,
            "1234123412341234",
            "Maxim Maximov",
            LocalDate.of(2030, Month.JANUARY,1)
    );

    PaymentCard savedCard = PaymentCard.builder()
            .id(id)
            .user(savedUser)
            .number("1234123412341234")
            .holder("Maxim Maximov")
            .expirationDate(LocalDate.of(2030, Month.JANUARY, 1))
            .active(true)
            .build();

    PaymentCardResponseDto expected = new PaymentCardResponseDto(
            id,
            userId,
            "1234123412341234",
            "Maxim Maximov",
            LocalDate.of(2030, Month.JANUARY,1),
            true,
            now,
            now
    );

    PaymentCardUpdateDto paymentCardUpdateDto = new PaymentCardUpdateDto(null, "Maxim M", null);

    @Test
    void create_should_return_paymentCardResponseDto() {
        PaymentCard fromDto = PaymentCard.builder()
                .number("1234123412341234")
                .holder("Maxim Maximov")
                .expirationDate(LocalDate.of(2030, Month.JANUARY, 1))
                .build();

        when(userQueryService.getUserWithCardsForUpdate(userId)).thenReturn(savedUser);
        when(paymentCardMapper.toEntity(paymentCardCreateDto)).thenReturn(fromDto);
        when(paymentCardRepository.save(fromDto)).thenReturn(savedCard);
        when(paymentCardMapper.toDto(savedCard)).thenReturn(expected);

        PaymentCardResponseDto actualDto = paymentCardCommandService.create(paymentCardCreateDto);
        assertThat(actualDto).isEqualTo(expected);
    }

    @Test
    void create_should_throw_resource_not_found_exception_when_user_id_not_found() {
        when(userQueryService.getUserWithCardsForUpdate(userId)).thenThrow(ResourceNotFoundException.user(userId));
        verify(paymentCardRepository, never()).save(any());
        assertThatThrownBy(() -> paymentCardCommandService.create(paymentCardCreateDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_should_throw_exception_when_card_limit_exceeded() {
        User withMaxCards = User.builder()
                .id(userId)
                .cards(IntStream.range(0, MAX_CARDS_PER_USER).mapToObj(_ -> new PaymentCard()).toList())
                .build();
        when(userQueryService.getUserWithCardsForUpdate(userId)).thenReturn(withMaxCards);
        assertThatThrownBy(() -> paymentCardCommandService.create(paymentCardCreateDto))
                .isInstanceOf(CardLimitException.class);
        verify(paymentCardRepository, never()).save(any());
    }

    @Test
    void update_should_return_paymentCardResponseDto() {
        when(paymentCardRepository.findById(id)).thenReturn(Optional.of(savedCard));
        when(paymentCardMapper.toDto(savedCard)).thenReturn(expected);

        PaymentCardResponseDto actualCard = paymentCardCommandService.update(id, paymentCardUpdateDto);
        verify(paymentCardMapper).updateEntity(paymentCardUpdateDto, savedCard);
        assertThat(actualCard).isEqualTo(expected);

    }

    @Test
    void update_should_throw_resource_not_found_exception_when_card_not_found() {
        when(paymentCardRepository.findById(id)).thenReturn(Optional.empty());
        verify(paymentCardMapper, never()).updateEntity(any(), any());
        verify(paymentCardMapper, never()).toDto(any());
        assertThatThrownBy(() -> paymentCardCommandService.update(id, paymentCardUpdateDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Payment card not found: " + id);
    }

    @Test
    void activate_should_set_active_true_and_evict_cache() {
        PaymentCard card = PaymentCard.builder().id(id).user(savedUser).active(false).build();

        when(paymentCardRepository.findById(id)).thenReturn(Optional.of(card));
        when(cacheManager.getCache("users")).thenReturn(cache);

        paymentCardCommandService.activate(id);
        verify(cache).evict(userId);
        assertThat(card.getActive()).isTrue();
    }

    @Test
    void activate_should_throw_resource_not_found_exception_when_card_not_found() {
        when(paymentCardRepository.findById(id)).thenReturn(Optional.empty());
        verify(cache, never()).evict(any());
        assertThatThrownBy(() -> paymentCardCommandService.activate(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Payment card not found: " + id);
    }

    @Test
    void activate_should_not_throw_when_cache_is_null() {
        when(paymentCardRepository.findById(id)).thenReturn(Optional.of(savedCard));
        when(cacheManager.getCache("users")).thenReturn(null);

        assertThatCode(() -> paymentCardCommandService.activate(id))
                .doesNotThrowAnyException();
    }

    @Test
    void deactivate_should_set_active_false_and_evict_cache() {
        PaymentCard card = PaymentCard.builder().id(id).user(savedUser).active(true).build();

        when(paymentCardRepository.findById(id)).thenReturn(Optional.of(card));
        when(cacheManager.getCache("users")).thenReturn(cache);

        paymentCardCommandService.deactivate(id);
        verify(cache).evict(userId);
        assertThat(card.getActive()).isFalse();
    }

    @Test
    void deactivate_should_throw_resource_not_found_exception_when_card_not_found() {
        when(paymentCardRepository.findById(id)).thenReturn(Optional.empty());
        verify(cache, never()).evict(any());
        assertThatThrownBy(() -> paymentCardCommandService.deactivate(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Payment card not found: " + id);

    }

}