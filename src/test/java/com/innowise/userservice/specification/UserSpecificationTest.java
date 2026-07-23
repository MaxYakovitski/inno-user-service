package com.innowise.userservice.specification;

import com.innowise.userservice.entity.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author ma_yak
 */

@ExtendWith(MockitoExtension.class)
class UserSpecificationTest {

    @Mock
    private Root<User> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    @Test
    void hasName_should_return_null_predicate_when_name_is_blank() {
        Specification<User> spec = UserSpecification.hasName(" ");
        Predicate predicate = spec.toPredicate(root, query, cb);
        assertThat(predicate).isNull();
    }

    @Test
    void hasSurname_should_return_null_predicate_when_surname_is_blank() {
        Specification<User> spec = UserSpecification.hasSurname(" ");
        Predicate predicate = spec.toPredicate(root, query, cb);
        assertThat(predicate).isNull();
    }

}