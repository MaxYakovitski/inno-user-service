package com.innowise.userservice.controller;

import com.innowise.userservice.dto.user.UserCreateDto;
import com.innowise.userservice.dto.user.UserUpdateDto;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.Month;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author ma_yak
 */


class UserControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void create_should_persist_user_and_return_201() throws Exception {
        UserCreateDto dto = new UserCreateDto("Maxim", "M", LocalDate.of(1995, Month.JANUARY, 1), "m@test.com");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Maxim"))
                .andExpect(jsonPath("$.surname").value("M"))
                .andExpect(jsonPath("$.email").value("m@test.com"));

        assertThat(userRepository.findAll()).hasSize(1);
    }

    @Test
    void get_should_return_user_when_exists() throws Exception {
        User user = User.builder()
                .name("Maxim")
                .surname("M")
                .birthDate(LocalDate.of(1995, Month.JANUARY, 1))
                .email("m@test.com")
                .active(true)
                .build();

        User saved = userRepository.save(user);

        mockMvc.perform(get("/api/users/" + saved.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(saved.getId()))
        .andExpect(jsonPath("$.name").value(saved.getName()));
    }

    @Test
    void get_should_return_404_when_not_exists() throws Exception {
        long nonExistentId = Long.MAX_VALUE;
        mockMvc.perform(get("/api/users/" + nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("not_found"));
    }

    @Test
    void update_should_evict_cache() throws Exception {
        User user = User.builder()
                .name("Maxim")
                .surname("M")
                .birthDate(LocalDate.of(1995, Month.JANUARY, 1))
                .email("m@test.com")
                .active(true)
                .build();

        User saved = userRepository.save(user);
        mockMvc.perform(get("/api/users/" + saved.getId()))
                .andExpect(status().isOk());

        Cache cache = Objects.requireNonNull(cacheManager.getCache("users"), "cache 'users' not initialized");
        assertThat(cache.get(saved.getId())).isNotNull();

        mockMvc.perform(patch("/api/users/{id}", saved.getId())
        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new UserUpdateDto(null, "Ivanov", null, null))))
                .andExpect(status().isOk());

        assertThat(cache.get(saved.getId())).isNull();

        mockMvc.perform(get("/api/users/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.surname").value("Ivanov"));

    }

    @Test
    void create_should_return_400_when_dto_is_invalid() throws Exception {
        UserCreateDto invalidDto = new UserCreateDto(null, null, null, "not email");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("validation_failed"));
    }
}