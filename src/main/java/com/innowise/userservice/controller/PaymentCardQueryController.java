package com.innowise.userservice.controller;

import com.innowise.userservice.dto.paymentcard.PaymentCardResponseDto;
import com.innowise.userservice.security.CurrentUser;
import com.innowise.userservice.service.PaymentCardQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment-cards")
@RequiredArgsConstructor
public class PaymentCardQueryController {

    private final PaymentCardQueryService paymentCardQueryService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @cardGuard.isOwner(#id, authentication.name)")
    public ResponseEntity<PaymentCardResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentCardQueryService.getById(id));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PaymentCardResponseDto>> getMyCards(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(paymentCardQueryService.getAllByUserId(CurrentUser.id(jwt)));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentCardResponseDto>> getAllByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(paymentCardQueryService.getAllByUserId(userId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PaymentCardResponseDto>> getAll(
            @RequestParam(required = false) String holder,
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(paymentCardQueryService.getAll(holder, pageable));
    }
}
