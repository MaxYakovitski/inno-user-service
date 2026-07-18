package com.innowise.userservice.repository.specification;

import com.innowise.userservice.entity.PaymentCard;
import org.springframework.data.jpa.domain.Specification;

/**
 * @author ma_yak
 */

public class PaymentCardSpecification {

    //filter by holder (uses Specifications)

    public static Specification<PaymentCard> hasHolder (String holder) {
        return ((root, query, cb)
                -> cb.like(cb.lower(root.get("holder")), "%" + holder.toLowerCase() + "%"));
    }
}
