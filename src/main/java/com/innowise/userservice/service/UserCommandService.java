package com.innowise.userservice.service;

import com.innowise.userservice.dto.user.UserCreateDto;
import com.innowise.userservice.dto.user.UserResponseDto;
import com.innowise.userservice.dto.user.UserUpdateDto;

import java.util.UUID;

public interface UserCommandService {

    UserResponseDto create(UserCreateDto dto);
    UserResponseDto update(UUID id, UserUpdateDto dto);

    void activate(UUID id);
    void deactivate(UUID id);
}
