package com.innowise.userservice.controller;

import com.innowise.userservice.dto.user.UserResponseDto;
import com.innowise.userservice.security.CurrentUser;
import com.innowise.userservice.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserQueryController {

    private final UserQueryService userQueryService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> getMe(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(userQueryService.getById(CurrentUser.id(jwt)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> getById(@PathVariable UUID id) {
       return ResponseEntity.ok(userQueryService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponseDto>> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname,
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(userQueryService.getAll(name, surname, pageable));
    }
}
