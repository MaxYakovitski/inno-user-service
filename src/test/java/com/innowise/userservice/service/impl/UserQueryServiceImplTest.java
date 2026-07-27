package com.innowise.userservice.service.impl;

import com.innowise.userservice.dto.user.UserResponseDto;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.ResourceNotFoundException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.repository.UserRepository;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserQueryServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserQueryServiceImpl userQueryService;

    UUID id = UUID.randomUUID();
    Instant now = Instant.now();

    User user = User.builder()
           .id(id)
           .name("Maxim")
           .surname("M")
           .birthDate(LocalDate.of(1995, Month.JANUARY, 1))
           .email("m@test.com")
           .active(true)
            .cards(List.of())
            .createdAt(now)
            .updatedAt(now)
           .build();

    UserResponseDto expectedDto = new UserResponseDto(
           user.getId(),
           user.getName(),
           user.getSurname(),
           user.getBirthDate(),
           user.getEmail(),
           user.getActive(),
            List.of(),
           user.getCreatedAt(),
           user.getUpdatedAt());


    @Test
    void getById_should_return_user_response_dto_when_user_exists() {
        when(userRepository.findByIdWithPaymentCards(id)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(expectedDto);

        UserResponseDto result = userQueryService.getById(id);
        assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    void getById_should_throw_resource_not_found_exception_when_user_not_found() {
        when(userRepository.findByIdWithPaymentCards(id)).thenReturn(Optional.empty());
        verify(userMapper, never()).toDto(any());
        assertThatThrownBy(() -> userQueryService.getById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found: " + id);
    }

    Page<User> page = new PageImpl<>(List.of(user));

    @SuppressWarnings("unchecked")
    @Test
    void getAll_should_return_page_of_users_when_users_exists() {
        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
        when(userMapper.toDto(user)).thenReturn(expectedDto);
        Page<UserResponseDto> result = userQueryService.getAll(null, null, Pageable.unpaged());
        assertThat(result.getContent()).containsExactly(expectedDto);
    }

    @SuppressWarnings("unchecked")
    @Test
    void getAll_should_return_empty_page_when_users_not_found() {
        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(Page.empty());
        Page<UserResponseDto> result = userQueryService.getAll(null, null, Pageable.unpaged());
        assertThat(result.getContent()).isEmpty();
    }

}