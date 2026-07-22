package com.innowise.userservice.controller;

import com.innowise.userservice.dto.paymentcard.PaymentCardResponseDto;
import com.innowise.userservice.service.PaymentCardQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * @author ma_yak
 */

@RestController
@RequestMapping("/api/payment-cards")
@RequiredArgsConstructor
public class PaymentCardQueryController {

    private final PaymentCardQueryService paymentCardQueryService;

    @GetMapping("/{id}")
    public ResponseEntity<PaymentCardResponseDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentCardQueryService.getById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentCardResponseDto>> getAllByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(paymentCardQueryService.getAllByUserId(userId));
    }

    @GetMapping
    public ResponseEntity<Page<PaymentCardResponseDto>> getAll(
            @RequestParam(required = false) String holder,
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(paymentCardQueryService.getAll(holder, pageable));
    }
}
