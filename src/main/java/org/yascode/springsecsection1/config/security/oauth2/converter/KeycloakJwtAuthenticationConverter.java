package org.yascode.springsecsection1.config.security.oauth2.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final String REALM_ACCESS = "realm_access";
    private static final String RESOURCE_ACCESS = "resource_access";
    private static final String ROLES = "roles";
    private static final String PERMISSION_PREFIX = "PERM_";
    private static final String ROLE_PREFIX = "ROLE_";

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Set<GrantedAuthority> authorities = new LinkedHashSet<>();

        extractRealmRoles(jwt, authorities);
        extractClientRoles(jwt, authorities);

        return authorities;
    }

    private void extractRealmRoles(
            Jwt jwt,
            Set<GrantedAuthority> authorities) {

        Map<String, Object> realmAccess = jwt.getClaimAsMap(REALM_ACCESS);

        if (realmAccess == null) {
            return;
        }

        addRoles(realmAccess.get(ROLES), authorities);
    }

    private void extractClientRoles(
            Jwt jwt,
            Set<GrantedAuthority> authorities) {

        Map<String, Object> resourceAccess = jwt.getClaimAsMap(RESOURCE_ACCESS);

        if (resourceAccess == null) {
            return;
        }

        for (Object clientValue : resourceAccess.values()) {
            if (!(clientValue instanceof Map<?, ?> clientClaims)) {
                continue;
            }

            addRoles(clientClaims.get(ROLES), authorities);
        }
    }

    private void addRoles(
            Object rolesValue,
            Set<GrantedAuthority> authorities) {

        if (!(rolesValue instanceof Collection<?> roles)) {
            return;
        }

        for (Object roleValue : roles) {
            if (!(roleValue instanceof String role)
                    || role.isBlank()) {
                continue;
            }

            authorities.add(
                    new SimpleGrantedAuthority(toAuthority(role))
            );
        }
    }

    private String toAuthority(String role) {
        if (role.startsWith(PERMISSION_PREFIX)) {
            return role;
        }

        if (role.startsWith(ROLE_PREFIX)) {
            return role;
        }

        return ROLE_PREFIX + role;
    }
}
