package com.ginkgo.security.authorization;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface AuthorizationProvider {
    public void setAuthorization(HttpSecurity config) throws Exception;
}
