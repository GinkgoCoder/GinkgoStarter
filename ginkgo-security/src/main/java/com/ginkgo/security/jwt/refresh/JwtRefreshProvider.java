package com.ginkgo.security.jwt.refresh;

import com.ginkgo.security.jwt.JwtTokenUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public class JwtRefreshProvider implements AuthenticationProvider {
    UserDetailsService userDetailsService;
    JwtTokenUtils jwtTokenUtils;

    public JwtRefreshProvider(UserDetailsService userDetailsService, JwtTokenUtils jwtTokenUtils) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails userDetails =
                this.userDetailsService.loadUserByUsername((String) authentication.getPrincipal());
        return new JwtRefreshToken(userDetails.getAuthorities(), (String) authentication.getPrincipal());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtRefreshToken.class.isAssignableFrom(authentication);
    }
}
