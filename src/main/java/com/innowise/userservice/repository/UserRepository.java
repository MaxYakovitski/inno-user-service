package com.innowise.userservice.repository;

import com.innowise.userservice.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

/**
 * @author ma_yak
 */

public interface UserRepository extends JpaRepository<User, UUID>,
                                        JpaSpecificationExecutor<User> {

    // JPQL, activates or deactivates user
    @Modifying
    @Query("UPDATE User u SET u.active = :active WHERE u.id = :id")
    void updateActiveStatus(@Param("id") UUID id, @Param("active") boolean active);

}
