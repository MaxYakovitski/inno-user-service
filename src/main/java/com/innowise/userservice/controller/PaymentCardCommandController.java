package com.innowise.userservice.controller;

import com.innowise.userservice.dto.paymentcard.PaymentCardCreateDto;
import com.innowise.userservice.dto.paymentcard.PaymentCardResponseDto;
import com.innowise.userservice.dto.paymentcard.PaymentCardUpdateDto;
import com.innowise.userservice.service.PaymentCardCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author ma_yak
 */

@RestController
@RequestMapping("/api/payment_cards")
@RequiredArgsConstructor
public class PaymentCardCommandController {

    private final PaymentCardCommandService paymentCardCommandService;

    @PostMapping
    public ResponseEntity<PaymentCardResponseDto> create(@Valid @RequestBody PaymentCardCreateDto dto) {
        PaymentCardResponseDto created = paymentCardCommandService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PaymentCardResponseDto> update(@PathVariable Long id, @Valid @RequestBody PaymentCardUpdateDto dto) {
        PaymentCardResponseDto updated = paymentCardCommandService.update(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        paymentCardCommandService.activate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        paymentCardCommandService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

}
