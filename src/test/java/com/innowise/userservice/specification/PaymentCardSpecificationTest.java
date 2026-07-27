package com.innowise.userservice.specification;

import com.innowise.userservice.entity.PaymentCard;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PaymentCardSpecificationTest {

    @Mock
    private Root<PaymentCard> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    @Test
    void hasHolder_should_return_null_predicate_when_holder_is_blank() {
        Specification<PaymentCard> spec = PaymentCardSpecification.hasHolder(" ");
        Predicate predicate = spec.toPredicate(root, query, cb);
        assertThat(predicate).isNull();
    }
}