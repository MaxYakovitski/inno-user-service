package com.innowise.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author ma_yak
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "users")
public class User extends AbstractAuditingEntity {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(nullable = false, length = 128)
    private String surname;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Boolean active;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,  orphanRemoval = true,  fetch = FetchType.EAGER)
    private List<PaymentCard> cards = new ArrayList<>();
}
