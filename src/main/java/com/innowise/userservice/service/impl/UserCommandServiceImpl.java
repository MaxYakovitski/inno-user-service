package com.innowise.userservice.service.impl;

import com.innowise.userservice.dto.user.UserCreateDto;
import com.innowise.userservice.dto.user.UserResponseDto;
import com.innowise.userservice.dto.user.UserUpdateDto;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.EmailAlreadyExistsException;
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
        boolean emailAlreadyExist = userRepository.existsByEmail(dto.email());
        if (emailAlreadyExist) {
           throw  EmailAlreadyExistsException.emailAlreadyExists(dto.email());
        }
        User user = userMapper.toEntity(dto);
        user.setActive(true);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public UserResponseDto update(UUID id, UserUpdateDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.user(id));
        if (dto.email() != null
                && !dto.email().equals(user.getEmail())
                && userRepository.existsByEmail(dto.email())) {
            throw  EmailAlreadyExistsException.emailAlreadyExists(dto.email());
        }
        userMapper.updateEntity(dto, user);
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void activate(UUID id) {
        updateActivateStatus(id, true);
    }

    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void deactivate(UUID id) {
        updateActivateStatus(id, false);
    }

    private void updateActivateStatus(UUID id, boolean active) {
        int updated = userRepository.updateActiveStatus(id, active);
        if (updated == 0) {
            throw  ResourceNotFoundException.user(id);
        }
    }
}
