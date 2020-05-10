package com.ginkgo.security.jwt.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtLoginAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 4142364785639627441L;

    private String username, password;

    public JwtLoginAuthenticationToken(String username, String password) {
        super(null);
        this.setAuthenticated(false);
        this.username = username;
        this.password = password;
    }

    public JwtLoginAuthenticationToken(Collection<? extends GrantedAuthority> authorities, String username,
                                       String password) {
        super(authorities);
        this.setAuthenticated(true);
        this.username = username;
        this.password = password;
    }

    @Override
    public Object getCredentials() {
        return this.password;
    }

    @Override
    public Object getPrincipal() {
        return this.username;
    }
}
