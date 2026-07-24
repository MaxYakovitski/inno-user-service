package com.innowise.userservice.specification;

import com.innowise.userservice.entity.PaymentCard;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentCardSpecification {

    //filter by holder (uses Specifications)

    public static Specification<PaymentCard> hasHolder (String holder) {
        return ((root, query, cb)
                -> StringUtils.hasText(holder)
        ? cb.like(cb.lower(root.get("holder")), "%" + holder.toLowerCase() + "%")
        : null);
    }
}
