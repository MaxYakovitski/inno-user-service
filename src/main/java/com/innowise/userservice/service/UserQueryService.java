package com.innowise.userservice.service;

import com.innowise.userservice.dto.user.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author ma_yak
 */

public interface UserQueryService {

    UserResponseDto getById(Long id);
    Page<UserResponseDto> getAll(String name, String surname, Pageable pageable);
}
