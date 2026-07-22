package com.innowise.userservice.service.impl;

import com.innowise.userservice.dto.user.UserCreateDto;
import com.innowise.userservice.dto.user.UserResponseDto;
import com.innowise.userservice.dto.user.UserUpdateDto;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.ResourceNotFoundException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.UserCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author ma_yak
 */

@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto create(UserCreateDto dto) {
        User user = userMapper.toEntity(dto);
        user.setActive(true);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public UserResponseDto update(UUID id, UserUpdateDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        userMapper.updateEntity(dto, user);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void activate(UUID id) {
        userRepository.updateActiveStatus(id, true);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void deactivate(UUID id) {
        userRepository.updateActiveStatus(id, false);
    }
}
