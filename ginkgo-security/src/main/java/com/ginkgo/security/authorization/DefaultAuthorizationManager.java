package com.ginkgo.security.authorization;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import java.util.Set;

public class DefaultAuthorizationManager implements AuthorizationManager {
    Set<AuthorizationProvider> authorizationProviders;

    public DefaultAuthorizationManager(
            Set<AuthorizationProvider> authorizationProviders) {
        this.authorizationProviders = authorizationProviders;
    }

    @Override
    public void callAllAuthorizationProvider(HttpSecurity config) throws Exception {
        for (AuthorizationProvider authorizationProvider : this.authorizationProviders) {
            authorizationProvider.setAuthorization(config);
        }
        config.authorizeRequests().anyRequest().authenticated();
    }
}
