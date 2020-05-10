package com.ginkgo.security.config;

import com.ginkgo.security.jwt.JwtTokenUtils;
import com.ginkgo.security.jwt.authentication.JWTLoginAuthenticationProvider;
import com.ginkgo.security.jwt.authentication.JwtLoginAuthenticationFilter;
import com.ginkgo.security.jwt.handler.JwtFailureHandler;
import com.ginkgo.security.jwt.handler.JwtSuccessfulHandler;
import com.ginkgo.security.jwt.refresh.JwtRefreshFilter;
import com.ginkgo.security.jwt.refresh.JwtRefreshProvider;
import com.ginkgo.security.jwt.validation.JwtValidationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@Import(BasicConfiguration.class)
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private UserDetailsService userDetailsService;
    private PasswordEncoder passwordEncoder;
    private SecurityProperties properties;


    @Autowired
    public JwtSecurityConfig(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder,
                             SecurityProperties properties) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.properties = properties;
    }

    @Bean
    public JwtTokenUtils jwtTokenUtils() {
        return new JwtTokenUtils(this.properties);
    }

    @Bean
    @ConditionalOnMissingBean(JwtFailureHandler.class)
    public JwtFailureHandler jwtFailureHandler() {
        return new JwtFailureHandler();
    }

    @Bean
    @ConditionalOnMissingBean(JwtSuccessfulHandler.class)
    public JwtSuccessfulHandler jwtSuccessfulHandler() {
        return new JwtSuccessfulHandler(this.jwtTokenUtils(), this.properties);
    }

    @Override
    public void configure(HttpSecurity http) {
        //Authentication Filter Provider Setting
        JwtLoginAuthenticationFilter jwtLoginAuthenticationFilter =
                new JwtLoginAuthenticationFilter(new AntPathRequestMatcher(this.properties.getJwt().getAuthPath()));
        jwtLoginAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        jwtLoginAuthenticationFilter.setPasswordEncoder(this.passwordEncoder);
        jwtLoginAuthenticationFilter.setAuthenticationFailureHandler(this.jwtFailureHandler());
        jwtLoginAuthenticationFilter.setAuthenticationSuccessHandler(this.jwtSuccessfulHandler());
        JWTLoginAuthenticationProvider jwtLoginAuthenticationProvider =
                new JWTLoginAuthenticationProvider(this.userDetailsService, this.passwordEncoder);

        //Refresh Authentication Filter Setting
        JwtRefreshFilter jwtRefreshFilter =
                new JwtRefreshFilter(new AntPathRequestMatcher(this.properties.getJwt().getTokenRefreshPath()),
                        this.jwtTokenUtils());
        jwtRefreshFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        jwtRefreshFilter.setAuthenticationFailureHandler(this.jwtFailureHandler());
        jwtRefreshFilter.setAuthenticationSuccessHandler(this.jwtSuccessfulHandler());
        JwtRefreshProvider jwtRefreshProvider = new JwtRefreshProvider(this.userDetailsService, this.jwtTokenUtils());

        JwtValidationFilter jwtValidationFilter =
                new JwtValidationFilter(this.jwtTokenUtils(), this.jwtFailureHandler());

        http.authenticationProvider(jwtLoginAuthenticationProvider);
        http.authenticationProvider(jwtRefreshProvider);
        http.addFilterBefore(jwtRefreshFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtLoginAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtValidationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
