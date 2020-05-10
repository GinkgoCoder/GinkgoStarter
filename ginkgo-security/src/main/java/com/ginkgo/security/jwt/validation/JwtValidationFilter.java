package com.ginkgo.security.jwt.validation;

import com.ginkgo.security.exception.JwtAuthenticationException;
import com.ginkgo.security.jwt.JwtTokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

public class JwtValidationFilter extends OncePerRequestFilter {
    private JwtTokenUtils jwtTokenUtils;
    private AuthenticationFailureHandler authenticationFailureHandler;

    public JwtValidationFilter(JwtTokenUtils jwtTokenUtils, AuthenticationFailureHandler authenticationFailureHandler) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Reset the Authentication, if the session strategy is not set
        // SecurityContextHolder.getContext().setAuthentication(null);

        if (!this.containsAuthorization(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            Authentication authentication = this.jwtTokenUtils.verifyJwtToken(request.getHeader("Authorization"));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            this.authenticationFailureHandler
                    .onAuthenticationFailure(request, response, new JwtAuthenticationException("Token is expired"));
        } catch (JwtAuthenticationException e) {
            this.authenticationFailureHandler
                    .onAuthenticationFailure(request, response, e);
        } catch (RuntimeException e) {
            this.authenticationFailureHandler
                    .onAuthenticationFailure(request, response, new JwtAuthenticationException("Token is not valid"));
        }
    }

    public boolean containsAuthorization(HttpServletRequest request) {
        return !Objects.isNull(request.getHeader("Authorization"));
    }
}
