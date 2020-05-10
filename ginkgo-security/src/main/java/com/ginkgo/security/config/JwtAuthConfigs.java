package com.ginkgo.security.config;

import com.ginkgo.security.authorization.AuthorizationManager;
import com.ginkgo.security.authorization.AuthorizationProvider;
import com.ginkgo.security.authorization.DefaultAuthorizationManager;
import com.ginkgo.security.authorization.DefaultAuthorizationProvider;
import com.ginkgo.security.jwt.JwtAuthenticationEntryPoint;
import com.ginkgo.security.jwt.JwtDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import java.util.Set;

@Configuration
@Import(JwtSecurityConfig.class)
@EnableWebSecurity
@EntityScan("com.ginkgo.security")
public class JwtAuthConfigs extends WebSecurityConfigurerAdapter {
    @Autowired
    private SecurityProperties properties;

    @Autowired
    private JwtSecurityConfig jwtSecurityConfig;

    @Autowired
    private Set<AuthorizationProvider> authorizationProviders;

    @Bean
    public AuthorizationProvider authorizationProvider() {
        return new DefaultAuthorizationProvider(this.properties);
    }

    @Bean
    public AuthorizationManager authorizationManager() {
        return new DefaultAuthorizationManager(this.authorizationProviders);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable().formLogin().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .accessDeniedHandler(new JwtDeniedHandler())
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint());

        this.authorizationManager().callAllAuthorizationProvider(http);

        http.apply(this.jwtSecurityConfig);
    }
}
