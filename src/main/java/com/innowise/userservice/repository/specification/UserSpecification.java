package com.innowise.userservice.repository.specification;

import com.innowise.userservice.entity.User;
import org.springframework.data.jpa.domain.Specification;

/**
 * @author ma_yak
 */

public class UserSpecification {

    //filter by first name and surname (uses Specifications)

    public static Specification<User> hasName(String name) {
        return ((root, query, cb) ->
                name == null ? null : cb.like(cb.lower(root.get("name")), "%" + name + "%"));
    }

    public static Specification<User> hasSurname(String surname) {
        return ((root, query, cb) ->
                surname == null ? null : cb.like(cb.lower(root.get("surname")), "%" + surname + "%"));
    }
}
