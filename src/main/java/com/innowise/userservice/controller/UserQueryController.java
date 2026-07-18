package com.innowise.userservice.controller;

import com.innowise.userservice.dto.user.UserResponseDto;
import com.innowise.userservice.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author ma_yak
 */

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserQueryController {

    private final UserQueryService userQueryService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id) {
       return ResponseEntity.ok(userQueryService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String surname,
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(userQueryService.getAll(name, surname, pageable));
    }
}
