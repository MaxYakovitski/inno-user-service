package com.innowise.userservice.service.impl;

import com.innowise.userservice.dto.user.UserCreateDto;
import com.innowise.userservice.dto.user.UserResponseDto;
import com.innowise.userservice.dto.user.UserUpdateDto;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.ResourceNotFoundException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

/**
 * @author ma_yak
 */

@ExtendWith(MockitoExtension.class)
class UserCommandServiceImplTest {

    @InjectMocks
    private UserCommandServiceImpl userCommandService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    Long id = 1L;
    Instant now = Instant.now();
    UserUpdateDto userUpdateDto = new UserUpdateDto(null, "Ivanov", null, null);

    @Test
    void create_should_return_userResponseDto() {
        UserCreateDto userCreateDto = new UserCreateDto(
            "Maxim",
            "Maximov",
            LocalDate.of(1995, Month.JANUARY,1),
            "m@test.com");

        User userFromDto = User.builder().name("Maxim").surname("Maximov").build();
        User savedUser = User.builder().name("Maxim").surname("Maximov").build();

        UserResponseDto expectedDto = new UserResponseDto(
            1L,
            "Maxim",
            "Maximov",
            LocalDate.of(1995, Month.JANUARY, 1),
            "m@test.com",
            true,
            List.of(),
            now,
            now);

        when(userMapper.toEntity(userCreateDto)).thenReturn(userFromDto);
        when(userRepository.save(userFromDto)).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(expectedDto);

        UserResponseDto actualDto = userCommandService.create(userCreateDto);
        assertThat(actualDto).isEqualTo(expectedDto);
    }

    @Test
    void update_should_return_userResponseDto() {
        User existingUser = User.builder()
                .id(id)
                .name("Maxim")
                .surname("Maximov")
                .birthDate(LocalDate.of(1995, Month.JANUARY, 1))
                .email("m@test.com")
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        UserResponseDto expectedDto = new UserResponseDto(
                id,
                "Maxim",
                "Ivanov",
                LocalDate.of(1995, Month.JANUARY, 1),
                "m@test.com",
                true,
                List.of(),
                now,
                Instant.now());

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userMapper.toDto(existingUser)).thenReturn(expectedDto);

        UserResponseDto actualDto = userCommandService.update(id, userUpdateDto);
        verify(userMapper).updateEntity(userUpdateDto, existingUser);
        assertThat(actualDto).isEqualTo(expectedDto);
    }

    @Test
    void update_should_throw_resource_not_found_exception_when_user_not_found() {
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userCommandService.update(id, userUpdateDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found: " + id);
        verify(userMapper, never()).updateEntity(any(), any());
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void activate_should_call_updateActiveStatus_with_true() {
        userCommandService.activate(id);
        verify(userRepository).updateActiveStatus(id, true);
    }

    @Test
    void deactivate_should_call_updateActiveStatus_with_false() {
        userCommandService.deactivate(id);
        verify(userRepository).updateActiveStatus(id, false);
    }
}