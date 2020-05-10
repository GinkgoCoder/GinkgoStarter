package com.ginkgo.security.service;

import com.ginkgo.security.domain.Account;
import com.ginkgo.security.repository.AccountRepository;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Objects;

public class GinkgoUserDetailsService implements UserDetailsService {

    private AccountRepository accountRepository;

    public GinkgoUserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = this.accountRepository.getAccountByUsername(username);

        if (Objects.isNull(account)) {
            throw new UsernameNotFoundException("User does not exist");
        }

        return User.builder().username(account.getUsername()).password(account.getPassword()).authorities(
                AuthorityUtils.commaSeparatedStringToAuthorityList(account.getRoles())).disabled(!account.getIsValid())
                .build();
    }
}
