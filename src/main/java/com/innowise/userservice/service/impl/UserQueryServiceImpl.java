package com.innowise.userservice.service.impl;

import com.innowise.userservice.dto.user.UserResponseDto;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.ResourceNotFoundException;
import com.innowise.userservice.mapper.UserMapper;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.specification.UserSpecification;
import com.innowise.userservice.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "users", key = "#id")
    public UserResponseDto getById(UUID id) {
        User user = userRepository.findByIdWithPaymentCards(id)
                .orElseThrow(() -> ResourceNotFoundException.user(id));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDto> getAll(String name, String surname, Pageable pageable) {
        Specification <User> specification = Specification
                .where(UserSpecification.hasName(name)
                .and(UserSpecification.hasSurname(surname)));
        return userRepository.findAll(specification, pageable).map(userMapper::toDto);
    }

    @Override
    public User getUserWithCardsForUpdate(UUID id) {
        return userRepository.findByIdWithPaymentCardsForUpdate(id)
                .orElseThrow(() -> ResourceNotFoundException.user(id));
    }

}
