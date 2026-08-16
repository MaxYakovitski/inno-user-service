package com.innowise.userservice.controller;

import com.innowise.userservice.dto.paymentcard.PaymentCardCreateDto;
import com.innowise.userservice.dto.paymentcard.PaymentCardResponseDto;
import com.innowise.userservice.dto.paymentcard.PaymentCardUpdateDto;
import com.innowise.userservice.security.CurrentUser;
import com.innowise.userservice.service.PaymentCardCommandService;
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
@RequestMapping("/api/v1/payment-cards")
@RequiredArgsConstructor
public class PaymentCardCommandController {

    private final PaymentCardCommandService paymentCardCommandService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PaymentCardResponseDto> create(@Valid @RequestBody PaymentCardCreateDto dto,
                                                         @AuthenticationPrincipal Jwt jwt) {
        PaymentCardResponseDto created = paymentCardCommandService.create(CurrentUser.id(jwt), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentCardResponseDto> createForUser(@PathVariable UUID userId,
                                                                @Valid @RequestBody PaymentCardCreateDto dto) {
        var created = paymentCardCommandService.create(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @cardGuard.isOwner(#id, authentication.name)")
    public ResponseEntity<PaymentCardResponseDto> update(@PathVariable UUID id,
                                                         @Valid @RequestBody PaymentCardUpdateDto dto) {
        PaymentCardResponseDto updated = paymentCardCommandService.update(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN') or @cardGuard.isOwner(#id, authentication.name)")
    public ResponseEntity<Void> activate(@PathVariable UUID id) {
        paymentCardCommandService.activate(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN') or @cardGuard.isOwner(#id, authentication.name)")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        paymentCardCommandService.deactivate(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
