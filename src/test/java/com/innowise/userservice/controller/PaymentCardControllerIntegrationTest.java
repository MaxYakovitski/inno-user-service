package com.innowise.userservice.controller;

import com.innowise.userservice.dto.paymentcard.PaymentCardCreateDto;
import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.Month;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaymentCardControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/api/payment_cards";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentCardRepository paymentCardRepository;

    private User user;
    private PaymentCardCreateDto dto;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .name("Maxim")
                .surname("Maximov")
                .birthDate(LocalDate.of(1995, Month.JANUARY, 1))
                .email("m@test.com")
                .active(true)
                .build());

        dto = new PaymentCardCreateDto(
                user.getId(),
                "1234123412349999",
                "Maxim Maximov",
                LocalDate.of(2030, Month.JANUARY, 1));
    }

    @Test
    void create_should_persist_payment_card_and_return_201() throws Exception {
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.active").value(true));

        assertThat(paymentCardRepository.findAll()).hasSize(1);
    }

    @Test
    void create_should_return_409_when_card_limit_exceeded() throws Exception {
        for (int i = 0; i < 5; i++) {
            paymentCardRepository.save(PaymentCard.builder()
                    .user(user)
                    .number("123412341234" + i)
                    .holder("Maxim Maximov")
                    .expirationDate(LocalDate.of(2030,Month.JANUARY,1))
                    .active(true)
                    .build());
        }

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("card_limit_exceeded"));
    }

}
