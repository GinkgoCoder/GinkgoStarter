package com.ginkgo.security.jwt.refresh;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtRefreshToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 646151679220412417L;

    private String username;
    private String token;

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     *                    represented by this authentication object.
     */
    public JwtRefreshToken(
            Collection<? extends GrantedAuthority> authorities, String username) {
        super(authorities);
        this.username = username;
        this.setAuthenticated(true);
    }

    public JwtRefreshToken(String username) {
        super(null);
        this.setAuthenticated(false);
        this.username = username;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.username;
    }
}
