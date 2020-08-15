package com.cakefactory.account.persistence;

import com.cakefactory.account.Account;
import com.cakefactory.account.AccountService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
class JpaAccountService implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    JpaAccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void register(String email, String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password can't be null or empty");
        }
        AccountEntity accountEntity = new AccountEntity();
        accountEntity.setEmail(email);
        accountEntity.setPassword(passwordEncoder.encode(password));

        this.accountRepository.save(accountEntity);
    }

    @Override
    public Account find(String email) {
        AccountEntity accountEntity = this.accountRepository.findByEmail(email);
        return new Account(accountEntity.getEmail(), accountEntity.getPassword());
    }

    @Override
    public boolean exists(String email) {
        return this.accountRepository.findByEmail(email) != null;
    }
}
