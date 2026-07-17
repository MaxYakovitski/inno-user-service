package com.innowise.userservice.repository;

import com.innowise.userservice.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * @author ma_yak
 */

public interface UserRepository extends JpaRepository<User, Long>,
                                        JpaSpecificationExecutor<User> {

    // JPQL, activates or deactivates user
    @Modifying
    @Query("UPDATE User u SET u.active = :active WHERE u.id = :id")
    int updateActiveStatus(@Param("id") Long id, @Param("active") boolean active);

}
