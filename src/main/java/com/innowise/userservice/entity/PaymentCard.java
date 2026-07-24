package com.innowise.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.UUID;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "payment_cards")
public class PaymentCard extends AbstractAuditingEntity {

    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 32, nullable = false, unique = true)
    private String number;

    @Column(length = 128, nullable = false)
    private String holder;

    @Column(nullable = false)
    private LocalDate expirationDate;

    @Column(nullable = false)
    private Boolean active;

}
