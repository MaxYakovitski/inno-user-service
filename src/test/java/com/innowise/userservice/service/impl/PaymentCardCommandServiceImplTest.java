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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * @author ma_yak
 */

@ExtendWith(MockitoExtension.class)
class PaymentCardCommandServiceImplTest {

    @Mock
    private PaymentCardRepository paymentCardRepository;

    @Mock
    private PaymentCardMapper paymentCardMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RedisCacheManager cacheManager;

    @Mock
    Cache cache;

    @InjectMocks
    private PaymentCardCommandServiceImpl paymentCardCommandService;

    UUID id = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    Instant now = Instant.now();
    User user = User.builder().id(userId).build();
    PaymentCardCreateDto paymentCardCreateDto = new PaymentCardCreateDto(
            userId,
            "1234123412341234",
            "Maxim Maximov",
            LocalDate.of(2030, Month.JANUARY,1)
    );

    PaymentCardUpdateDto paymentCardUpdateDto = new PaymentCardUpdateDto(null, "Maxim M", null);

    @Test
    void create_should_return_paymentCardResponseDto() {
        PaymentCard fromDto = PaymentCard.builder()
                .number("1234123412341234")
                .holder("Maxim Maximov")
                .expirationDate(LocalDate.of(2030, Month.JANUARY, 1))
                .build();

        PaymentCard savedPaymentCard = PaymentCard.builder()
                .number("1234123412341234")
                .holder("Maxim Maximov")
                .expirationDate(LocalDate.of(2030, Month.JANUARY, 1))
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

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(paymentCardRepository.countByUserId(userId)).thenReturn(0L);
        when(paymentCardMapper.toEntity(paymentCardCreateDto)).thenReturn(fromDto);
        when(paymentCardRepository.save(fromDto)).thenReturn(savedPaymentCard);
        when(paymentCardMapper.toDto(savedPaymentCard)).thenReturn(expected);

        PaymentCardResponseDto actualDto = paymentCardCommandService.create(paymentCardCreateDto);
        assertThat(actualDto).isEqualTo(expected);
    }

    @Test
    void create_should_throw_exception_when_card_limit_exceeded() {
        when(paymentCardRepository.countByUserId(userId)).thenReturn(5L);
        verify(paymentCardRepository, never()).save(any());
        assertThatThrownBy(() -> paymentCardCommandService.create(paymentCardCreateDto))
                .isInstanceOf(CardLimitException.class);
    }

    @Test
    void update_should_return_paymentCardResponseDto() {
        PaymentCard existingCard = PaymentCard.builder()
                .id(id)
                .number("1234123412341234")
                .holder("Maxim Maximov")
                .expirationDate(LocalDate.of(2030, Month.JANUARY, 1))
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        PaymentCardResponseDto expectedDto = new PaymentCardResponseDto(
                id,
                userId,
                "1234123412341234",
                "Maxim M",
                LocalDate.of(2030, Month.JANUARY, 1),
                true,
                now,
                Instant.now()
        );

        when(paymentCardRepository.findById(id)).thenReturn(Optional.of(existingCard));
        when(paymentCardMapper.toDto(existingCard)).thenReturn(expectedDto);

        PaymentCardResponseDto actualCard = paymentCardCommandService.update(id, paymentCardUpdateDto);
        verify(paymentCardMapper).updateEntity(paymentCardUpdateDto, existingCard);
        assertThat(actualCard).isEqualTo(expectedDto);

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
        PaymentCard card = PaymentCard.builder().id(id).user(user).active(false).build();

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
    void deactivate_should_set_active_false_and_evict_cache() {
        PaymentCard card = PaymentCard.builder().id(id).user(user).active(true).build();

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