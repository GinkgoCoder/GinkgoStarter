package com.ginkgo.security.authorization;

import com.ginkgo.security.config.SecurityProperties;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public class DefaultAuthorizationProvider implements AuthorizationProvider {
    private SecurityProperties properties;

    public DefaultAuthorizationProvider(SecurityProperties properties) {
        this.properties = properties;
    }

    @Override
    public void setAuthorization(HttpSecurity config) throws Exception {
        config.authorizeRequests()
                .antMatchers(this.properties.getJwt().getAuthPath(), this.properties.getJwt().getTokenRefreshPath())
                .permitAll();
    }
}
