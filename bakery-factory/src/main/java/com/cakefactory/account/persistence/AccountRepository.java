package com.cakefactory.account.persistence;

import org.springframework.data.repository.CrudRepository;

interface AccountRepository extends CrudRepository<AccountEntity, String> {
    AccountEntity findByEmail(String email);
}
