package com.innowise.userservice.repository;

import com.innowise.userservice.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;


public interface UserRepository extends JpaRepository<User, UUID>,
                                        JpaSpecificationExecutor<User> {

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.cards WHERE u.id = :id")
    Optional<User> findByIdWithPaymentCards(@Param("id") UUID id);

    // JPQL, activates or deactivates user
    @Modifying
    @Query("UPDATE User u SET u.active = :active WHERE u.id = :id")
    int updateActiveStatus(@Param("id") UUID id, @Param("active") boolean active);

    boolean existsByEmail(String email);
}
