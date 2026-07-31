package com.innowise.userservice.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;


class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig();

    @Test
    void jwtConverter_mapsRoleClaimToSpringAuthority() {
        UUID userId = UUID.randomUUID();
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject(userId.toString())
                .claim("role", "ADMIN")
                .build();

        var authentication = securityConfig.jwtAuthenticationConverter().convert(jwt);

        assertThat(AuthorityUtils.authorityListToSet(authentication.getAuthorities()))
                .filteredOn(authority -> authority.startsWith("ROLE_"))
                .containsExactly("ROLE_ADMIN");
        assertThat(authentication.getName()).isEqualTo(userId.toString());
    }

    @Test
    void jwtConverter_givesNoRoleWhenClaimIsMissing() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .subject("auth-service")
                .build();

        var authentication = securityConfig.jwtAuthenticationConverter().convert(jwt);

        assertThat(AuthorityUtils.authorityListToSet(authentication.getAuthorities()))
                .filteredOn(authority -> authority.startsWith("ROLE_"))
                .isEmpty();
    }

}