package com.innowise.userservice.security;

import com.innowise.userservice.repository.PaymentCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("cardGuard")
@RequiredArgsConstructor
public class CardGuard {

    private final PaymentCardRepository paymentCardRepository;

    public boolean isOwner(UUID cardId, String subject) {
        try {
            return paymentCardRepository.existsByIdAndUserId(cardId, UUID.fromString(subject));
        } catch (IllegalArgumentException _) {
            return false;
        }
    }
}
