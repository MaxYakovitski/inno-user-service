package com.innowise.userservice.controller;

import com.innowise.userservice.dto.paymentcard.PaymentCardCreateDto;
import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author ma_yak
 */

public class PaymentCardControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentCardRepository paymentCardRepository;

    @Test
    void create_should_return_409_when_card_limit_exceeded() throws Exception {
        User user = userRepository.save(User.builder()
                .name("Maxim")
                .surname("Maximov")
                .birthDate(LocalDate.of(1995, 1, 1))
                .email("m@test.com")
                .active(true)
                .build());

        for (int i = 0; i < 5; i++) {
            paymentCardRepository.save(PaymentCard.builder()
                    .user(user)
                    .number("123412341234" + i)
                    .holder("Maxim Maximov")
                    .expirationDate(LocalDate.of(2030,1,1))
                    .active(true)
                    .build());
        }

        PaymentCardCreateDto dto = new PaymentCardCreateDto(
                user.getId(),
                "1234123412349999",
                "Maxim Maximov",
                LocalDate.of(2030, 1, 1));

        mockMvc.perform(post("/api/payment_cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("card_limit_exceeded"));
    }

}
