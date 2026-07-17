package com.innowise.userservice.repository;

import com.innowise.userservice.entity.PaymentCard;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * @author ma_yak
 */

public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long>,
                                               JpaSpecificationExecutor<PaymentCard> {

    //named method, gets all cards by user id
    List<PaymentCard> findByUserId(Long id);

    //native SQL just for example because there is a standard named method for this
    @NativeQuery(value = "SELECT * FROM payment_cards WHERE user_id = :userId AND active = true")
    List<PaymentCard> findActiveCardsByUserIdNative(@Param("userId") Long id);

    // JPQL, activates or deactivates card
    @Modifying
    @Query("UPDATE PaymentCard c SET c.active = :active WHERE c.id = :id")
    int updateActivateStatus(@Param("id") Long id, @Param("active") boolean active);
}
