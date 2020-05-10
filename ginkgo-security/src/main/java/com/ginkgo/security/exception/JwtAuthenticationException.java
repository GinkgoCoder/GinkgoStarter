package com.ginkgo.security.exception;

import org.springframework.security.core.AuthenticationException;

public class JwtAuthenticationException extends AuthenticationException {
    private static final long serialVersionUID = -5773864787325355423L;

    public JwtAuthenticationException(String msg) {
        super(msg);
    }
}
