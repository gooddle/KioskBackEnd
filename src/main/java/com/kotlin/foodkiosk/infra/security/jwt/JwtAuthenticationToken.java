package com.kotlin.foodkiosk.infra.security.jwt;

import com.kotlin.foodkiosk.infra.security.UserPrincipal;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.io.Serializable;

public class JwtAuthenticationToken extends AbstractAuthenticationToken implements Serializable {

    private final UserPrincipal principal;

    public JwtAuthenticationToken(UserPrincipal principal, WebAuthenticationDetails detail) {
        super(principal.getAuthorities());
        this.principal = principal;
        super.setAuthenticated(true);
        super.setDetails(detail);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public UserPrincipal getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }
}
