package com.egbank.digitalinternetbanking.repository;

import com.egbank.digitalinternetbanking.model.account.Account;
import com.egbank.digitalinternetbanking.model.user.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@Transactional
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByOwner(User owner);

    Account findAccountByAccountNumber(String accountNumber);
}