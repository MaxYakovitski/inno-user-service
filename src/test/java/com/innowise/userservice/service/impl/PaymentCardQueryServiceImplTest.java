package com.innowise.userservice.service.impl;

import com.innowise.userservice.dto.paymentcard.PaymentCardResponseDto;
import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.ResourceNotFoundException;
import com.innowise.userservice.mapper.PaymentCardMapper;
import com.innowise.userservice.repository.PaymentCardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PaymentCardQueryServiceImplTest {

    @Mock
    private PaymentCardRepository paymentCardRepository;

    @Mock
    private PaymentCardMapper paymentCardMapper;

    @InjectMocks
    private PaymentCardQueryServiceImpl paymentCardQueryService;

    UUID id = UUID.randomUUID();
    Instant now = Instant.now();
    User user = User.builder().id(id).build();

    PaymentCard card = PaymentCard.builder()
            .id(id)
            .number("1234123412341234")
            .holder("Maxim M")
            .expirationDate(LocalDate.of(2030, Month.JANUARY, 1))
            .active(true)
            .createdAt(now)
            .updatedAt(now)
            .build();

    PaymentCardResponseDto expectedDto = new PaymentCardResponseDto(
            card.getId(),
            user.getId(),
            card.getNumber(),
            card.getHolder(),
            card.getExpirationDate(),
            card.getActive(),
            card.getCreatedAt(),
            card.getUpdatedAt());

    @Test
    void getById_should_return_payment_card_response_dto_when_card_exists() {
        when(paymentCardRepository.findById(id)).thenReturn(Optional.of(card));
        when(paymentCardMapper.toDto(card)).thenReturn(expectedDto);
        PaymentCardResponseDto result = paymentCardQueryService.getById(id);
        assertThat(result).isEqualTo(expectedDto);

    }

    @Test
    void getById_should_throw_resource_not_found_exception_when_card_not_found() {
        when(paymentCardRepository.findById(id)).thenReturn(Optional.empty());
        verify(paymentCardMapper, never()).toDto(any());
        assertThatThrownBy(() -> paymentCardQueryService.getById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Payment card not found: " + id);
    }

    List<PaymentCard> list = List.of(card);

    @Test
    void getAllByUserId_should_return_list_of_payment_cards_belonging_to_user_by_id() {
        when(paymentCardRepository.findByUserId(user.getId())).thenReturn(list);
        when(paymentCardMapper.toDto(card)).thenReturn(expectedDto);
        List<PaymentCardResponseDto> result = paymentCardQueryService.getAllByUserId(user.getId());
        assertThat(result).containsExactly(expectedDto);
    }

    @Test
    void getAllByUserId_should_return_empty_list_when_user_has_no_cards() {
        when(paymentCardRepository.findByUserId(user.getId())).thenReturn(List.of());
        List<PaymentCardResponseDto> result = paymentCardQueryService.getAllByUserId(user.getId());
        assertThat(result).isEmpty();
    }

    Page<PaymentCard> page = new PageImpl<>(List.of(card));

    @SuppressWarnings("unchecked")
    @Test
    void getAll_should_return_page_of_cards_when_cards_exists() {
        when(paymentCardRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(paymentCardMapper.toDto(card)).thenReturn(expectedDto);
        Page<PaymentCardResponseDto> result = paymentCardQueryService.getAll(null, Pageable.unpaged());
        assertThat(result.getContent()).containsExactly(expectedDto);
    }

    @SuppressWarnings("unchecked")
    @Test
    void getAll_should_return_empty_page_when_cards_exists() {
        when(paymentCardRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(Page.empty());
        Page<PaymentCardResponseDto> result = paymentCardQueryService.getAll(null, Pageable.unpaged());
        assertThat(result.getContent()).isEmpty();
    }

}