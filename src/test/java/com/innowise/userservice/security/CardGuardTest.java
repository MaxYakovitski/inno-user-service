package com.innowise.userservice.security;

import com.innowise.userservice.repository.PaymentCardRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardGuardTest {

    @Mock
    private PaymentCardRepository paymentCardRepository;

    @InjectMocks
    private CardGuard cardGuard;

    private final UUID cardId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @Test
    void isOwner_returnsTrueForOwnCard() {
        when(paymentCardRepository.existsByIdAndUserId(cardId, userId)).thenReturn(true);
        assertThat(cardGuard.isOwner(cardId, userId.toString())).isTrue();
    }

    @Test
    void isOwner_returnsFalseForForeignCard() {
        when(paymentCardRepository.existsByIdAndUserId(cardId, userId)).thenReturn(false);
        assertThat(cardGuard.isOwner(cardId, userId.toString())).isFalse();
    }

    @Test
    void isOwner_returnsFalseForNonUuidSubject() {
        assertThat(cardGuard.isOwner(cardId, "auth-service")).isFalse();
        verifyNoInteractions(paymentCardRepository);
    }

}