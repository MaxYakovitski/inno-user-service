package com.innowise.userservice.service;

import com.innowise.userservice.dto.user.UserResponseDto;
import com.innowise.userservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserQueryService {

    UserResponseDto getById(UUID id);
    Page<UserResponseDto> getAll(String name, String surname, Pageable pageable);

    User getUserWithCardsForUpdate(UUID id);
}
