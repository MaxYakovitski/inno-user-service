package com.innowise.userservice.service;

import com.innowise.userservice.dto.user.UserCreateDto;
import com.innowise.userservice.dto.user.UserResponseDto;
import com.innowise.userservice.dto.user.UserUpdateDto;

/**
 * @author ma_yak
 */

public interface UserCommandService {

    UserResponseDto create(UserCreateDto dto);
    UserResponseDto update(Long id, UserUpdateDto dto);

    void activate(Long id);
    void deactivate(Long id);
}
