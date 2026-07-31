package com.innowise.userservice.controller;

import com.innowise.userservice.dto.user.UserCreateDto;
import com.innowise.userservice.dto.user.UserResponseDto;
import com.innowise.userservice.dto.user.UserUpdateDto;
import com.innowise.userservice.entity.PaymentCard;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.repository.PaymentCardRepository;
import com.innowise.userservice.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String BASE_URL = "/api/users";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    PaymentCardRepository paymentCardRepository;

    @Autowired
    private CacheManager cacheManager;

    @PersistenceContext
    private EntityManager entityManager;

    private User user;
    private final Instant now = Instant.now();

    @BeforeEach
    void setUp() {
        user = User.builder()
                .name("Maxim")
                .surname("M")
                .birthDate(LocalDate.of(1995, Month.JANUARY, 1))
                .email("m@test.com")
                .active(true)
                .cards(List.of())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    @Test
    void create_should_persist_user_and_return_201() throws Exception {
        UserCreateDto dto = new UserCreateDto("Maxim", "M", LocalDate.of(1995, Month.JANUARY, 1), "m@test.com");

        String response = mockMvc.perform(post(BASE_URL)
                        .with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(1);
        User savedUser = users.getFirst();

        UserResponseDto actual = objectMapper.readValue(response, UserResponseDto.class);

        UserResponseDto expected = new UserResponseDto(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getSurname(),
                savedUser.getBirthDate(),
                savedUser.getEmail(),
                savedUser.getActive(),
                List.of(),
                savedUser.getCreatedAt(),
                savedUser.getUpdatedAt()
        );

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void create_should_return_400_when_dto_is_invalid() throws Exception {
        UserCreateDto invalidDto = new UserCreateDto(null, null, null, "not email");

        mockMvc.perform(post(BASE_URL)
                        .with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("validation_failed"));
    }

    @Test
    void get_should_return_user_when_exists() throws Exception {
        User saved = userRepository.save(user);

        mockMvc.perform(get(BASE_URL + "/" + saved.getId()).with(asAdmin()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(saved.getId().toString()))
        .andExpect(jsonPath("$.name").value(saved.getName()));
    }

    @Test
    void get_should_return_404_when_not_exists() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        mockMvc.perform(get(BASE_URL + "/" + nonExistentId).with(asAdmin()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("not_found"));
    }

    @Test
    void update_should_evict_cache() throws Exception {
        User saved = userRepository.save(user);
        mockMvc.perform(get(BASE_URL + "/" + saved.getId()).with(asAdmin()))
                .andExpect(status().isOk());

        Cache cache = Objects.requireNonNull(cacheManager.getCache("users"), "cache 'users' not initialized");
        assertThat(cache.get(saved.getId())).isNotNull();

        mockMvc.perform(patch(BASE_URL + "/{id}", saved.getId()).with(asAdmin())
        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new UserUpdateDto(null, "Ivanov", null, null))))
                .andExpect(status().isOk());

        assertThat(cache.get(saved.getId())).isNull();

        mockMvc.perform(get(BASE_URL+ "/" + saved.getId()).with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.surname").value("Ivanov"));

    }

    @Test
    void delete_should_remove_user_with_cards() throws Exception {
        User saved = userRepository.save(user);
        paymentCardRepository.save(PaymentCard.builder()
                .user(saved)
                .number("1234123412341234")
                .holder("Maxim M")
                .expirationDate(LocalDate.now().plusYears(5))
                .active(true)
                .build());

        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(delete(BASE_URL + "/{id}", saved.getId()).with(asAdmin()))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(saved.getId())).isEmpty();
        assertThat(paymentCardRepository.findByUserId(saved.getId())).isEmpty();
    }

    @Test
    void delete_should_return_404_when_not_exists() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{id}", UUID.randomUUID()).with(asAdmin()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("not_found"));
    }
}
