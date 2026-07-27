package com.innowise.userservice.specification;

import com.innowise.userservice.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSpecification {

    //filter by first name and surname (uses Specifications)

    public static Specification<User> hasName(String name) {
        return ((root, query, cb) ->
                StringUtils.hasText(name)
                ? cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%")
                : null);
    }

    public static Specification<User> hasSurname(String surname) {
        return ((root, query, cb) ->
                StringUtils.hasText(surname)
                ? cb.like(cb.lower(root.get("surname")), "%" + surname.toLowerCase() + "%")
                : null);
    }
}
