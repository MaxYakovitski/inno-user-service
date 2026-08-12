package com.innowise.userservice.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CurrentUser {

    public static UUID id(Jwt jwt) {
        try {
            return UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException _) {
            throw new BadCredentialsException("Token subject has not valid user id");
        }
    }
}
