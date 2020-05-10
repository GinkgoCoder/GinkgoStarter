package com.ginkgo.security.jwt.refresh;

import com.ginkgo.security.exception.JwtAuthenticationException;
import com.ginkgo.security.jwt.JwtTokenUtils;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtRefreshFilter extends AbstractAuthenticationProcessingFilter {

    JwtTokenUtils jwtTokenUtils;

    public JwtRefreshFilter(
            RequestMatcher requiresAuthenticationRequestMatcher, JwtTokenUtils jwtTokenUtils) {
        super(requiresAuthenticationRequestMatcher);
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        try {
            if (!this.jwtTokenUtils.isRefreshToken(request.getHeader("Authorization"))) {
                throw new JwtAuthenticationException("This token is not a refresh token");
            }
            Claims claims =
                    this.jwtTokenUtils.getJwtParser().parseClaimsJws(request.getHeader("Authorization")).getBody();
            JwtRefreshToken token = new JwtRefreshToken((String) claims.getSubject());
            token.setDetails(request);
            return this.getAuthenticationManager().authenticate(token);
        } catch (JwtAuthenticationException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new JwtAuthenticationException("Refresh Token is not valid");
        }
    }
}
