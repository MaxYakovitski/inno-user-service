package com.innowise.userservice.controller;

import com.innowise.userservice.dto.user.UserCreateDto;
import com.innowise.userservice.dto.user.UserResponseDto;
import com.innowise.userservice.dto.user.UserUpdateDto;
import com.innowise.userservice.security.CurrentUser;
import com.innowise.userservice.service.UserCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserCommandController {

    private final UserCommandService userCommandService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> create (@Valid @RequestBody UserCreateDto dto) {
        UserResponseDto created = userCommandService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> updateMe(@Valid @RequestBody UserUpdateDto dto,
                                                    @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(userCommandService.update(CurrentUser.id(jwt), dto));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> update(@PathVariable UUID id, @Valid @RequestBody UserUpdateDto dto) {
        UserResponseDto updated = userCommandService.update(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activate(@PathVariable UUID id) {
        userCommandService.activate(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        userCommandService.deactivate(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userCommandService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
