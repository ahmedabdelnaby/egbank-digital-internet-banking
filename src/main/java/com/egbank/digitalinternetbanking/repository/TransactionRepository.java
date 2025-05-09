package com.egbank.digitalinternetbanking.repository;

import com.egbank.digitalinternetbanking.model.account.Account;
import com.egbank.digitalinternetbanking.model.transaction.Transaction;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
@Transactional
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySourceAccount(Account sourceAccount);
}