package com.ginkgo.security.jwt.authentication;

import com.ginkgo.security.exception.JwtAuthenticationException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

public class JWTLoginAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;
    private PasswordEncoder passwordEncoder;

    public JWTLoginAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtLoginAuthenticationToken jwtToken = (JwtLoginAuthenticationToken) authentication;

        UserDetails userDetails = this.userDetailsService.loadUserByUsername((String) jwtToken.getPrincipal());

        if (!userDetails.isEnabled()) {
            throw new JwtAuthenticationException("User is not valid");
        }
        if (!this.passwordEncoder.matches((String) jwtToken.getCredentials(), userDetails.getPassword())) {
            throw new JwtAuthenticationException("Password is not correct");
        }

        jwtToken = new JwtLoginAuthenticationToken(userDetails.getAuthorities(), (String) jwtToken.getPrincipal(),
                (String) jwtToken.getCredentials());
        jwtToken.setDetails(authentication.getDetails());

        return jwtToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtLoginAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
