package com.innowise.userservice.service.impl;

import com.innowise.userservice.dto.user.UserCreateDto;
import com.innowise.userservice.dto.user.UserResponseDto;
import com.innowise.userservice.dto.user.UserUpdateDto;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.EmailAlreadyExistsException;
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
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserCommandServiceImpl userCommandService;

    UUID id = UUID.randomUUID();
    Instant now = Instant.now();

    UserCreateDto userCreateDto = new UserCreateDto(
            "Maxim",
            "Maximov",
            LocalDate.of(1995, Month.JANUARY,1),
            "m@test.com");

    User savedUser = User.builder()
            .id(id)
            .name("Maxim")
            .surname("Maximov")
            .birthDate(LocalDate.of(1995, Month.JANUARY, 1))
            .email("m@test.com")
            .active(true)
            .createdAt(now)
            .updatedAt(now)
            .build();

    UserResponseDto expected = new UserResponseDto(
            id,
            "Maxim",
            "Maximov",
            LocalDate.of(1995, Month.JANUARY, 1),
            "m@test.com",
            true,
            List.of(),
            now,
            now);

    UserUpdateDto userUpdateDto = new UserUpdateDto(null, "Ivanov", null, null);

    @Test
    void create_should_return_userResponseDto() {
        User userFromDto = User.builder().name("Maxim").surname("Maximov").build();
        when(userMapper.toEntity(userCreateDto)).thenReturn(userFromDto);
        when(userRepository.save(userFromDto)).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(expected);

        UserResponseDto actualDto = userCommandService.create(userCreateDto);
        assertThat(actualDto).isEqualTo(expected);
    }

    @Test
    void create_should_throw_email_already_exist_exception_when_email_already_exist() {
        when(userRepository.existsByEmail(userCreateDto.email())).thenReturn(true);
        assertThatThrownBy(() -> userCommandService.create(userCreateDto))
                .isInstanceOf(EmailAlreadyExistsException.class);
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_should_return_userResponseDto() {
        when(userRepository.findById(id)).thenReturn(Optional.of(savedUser));
        when(userMapper.toDto(savedUser)).thenReturn(expected);

        UserResponseDto actualDto = userCommandService.update(id, userUpdateDto);
        assertThat(actualDto).isEqualTo(expected);
        verify(userMapper).updateEntity(userUpdateDto, savedUser);
    }

    @Test
    void update_should_throw_resource_not_found_exception_when_user_not_found() {
        when(userRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userCommandService.update(id, userUpdateDto))
                .isInstanceOf(ResourceNotFoundException.class);
        verify(userMapper, never()).updateEntity(any(), any());
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void update_should_not_check_email_uniqueness_when_email_not_changed() {
        when(userRepository.findById(id)).thenReturn(Optional.of(savedUser));
        when(userMapper.toDto(savedUser)).thenReturn(expected);

        UserUpdateDto dtoWithSameEmail = new UserUpdateDto(null, "Ivanov", null, savedUser.getEmail());
        userCommandService.update(id, dtoWithSameEmail);
        verify(userRepository, never()).existsByEmail(any());
    }

    @Test
    void update_should_proceed_when_email_is_available() {
        when(userRepository.findById(id)).thenReturn(Optional.of(savedUser));
        when(userRepository.existsByEmail("available@test.com")).thenReturn(false);
        when(userMapper.toDto(savedUser)).thenReturn(expected);

        UserUpdateDto dtoWithAvailableEmail = new UserUpdateDto(null, null, null, "available@test.com");
        UserResponseDto result = userCommandService.update(id, dtoWithAvailableEmail);
        assertThat(result).isEqualTo(expected);
        verify(userMapper).updateEntity(dtoWithAvailableEmail, savedUser);
    }

    @Test
    void update_should_throw_email_already_exist_exception_when_email_already_exist() {
        when(userRepository.findById(id)).thenReturn(Optional.of(savedUser));
        when(userRepository.existsByEmail("new@test.com")).thenReturn(true);

        UserUpdateDto dtoWithNewEmail = new UserUpdateDto(null, null, null, "new@test.com");
        assertThatThrownBy(() -> userCommandService.update(id, dtoWithNewEmail))
                .isInstanceOf(EmailAlreadyExistsException.class);
        verify(userMapper, never()).updateEntity(any(), any());
    }

    @Test
    void activate_should_call_updateActiveStatus_with_true() {
        when(userRepository.updateActiveStatus(id, true)).thenReturn(1);
        userCommandService.activate(id);
        verify(userRepository).updateActiveStatus(id, true);
    }

    @Test
    void activate_should_return_resource_not_found_exception_when_user_not_found() {
        when(userRepository.updateActiveStatus(id, true)).thenReturn(0);
        assertThatThrownBy(() -> userCommandService.activate(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deactivate_should_call_updateActiveStatus_with_false() {
        when(userRepository.updateActiveStatus(id, false)).thenReturn(1);
        userCommandService.deactivate(id);
        verify(userRepository).updateActiveStatus(id, false);
    }

    @Test
    void deactivate_should_return_resource_not_found_exception_when_user_not_found() {
        when(userRepository.updateActiveStatus(id, false)).thenReturn(0);
        assertThatThrownBy(() -> userCommandService.deactivate(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}