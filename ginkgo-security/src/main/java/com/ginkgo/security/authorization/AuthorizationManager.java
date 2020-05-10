package com.ginkgo.security.authorization;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface AuthorizationManager {
    public void callAllAuthorizationProvider(HttpSecurity config) throws Exception;
}
