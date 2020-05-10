package com.ginkgo.security.repository;

import com.ginkgo.security.domain.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Long> {
    public Account getAccountByUsername(String username);
}
