package com.ginkgo.security.config;

import com.ginkgo.security.repository.AccountRepository;
import com.ginkgo.security.service.GinkgoUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableJpaRepositories("com.ginkgo.security.repository")
@EnableConfigurationProperties(SecurityProperties.class)
public class BasicConfiguration {
    @Autowired
    private AccountRepository accountRepository;

    @Bean
    @ConditionalOnMissingBean(UserDetailsService.class)
    public GinkgoUserDetailsService userDetailsService() {
        return new GinkgoUserDetailsService(this.accountRepository);
    }

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
