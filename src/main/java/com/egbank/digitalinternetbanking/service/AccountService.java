package com.egbank.digitalinternetbanking.service;

import com.egbank.digitalinternetbanking.model.account.Account;
import com.egbank.digitalinternetbanking.model.account.CheckingAccount;
import com.egbank.digitalinternetbanking.model.account.SavingsAccount;
import com.egbank.digitalinternetbanking.model.user.User;
import com.egbank.digitalinternetbanking.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Optional<Account> findAccountById(Long accId) {
        return accountRepository.findById(accId);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public String createAccount(User user, String accountType, Double initialDeposit) {
        if (accountType == "SAVINGS"
                && initialDeposit.compareTo(Double.valueOf(100)) < 0) {
            return "Savings account requires a minimum of $100.";
        }

        String accNum = generateAccountNumber();
        Account account;
        account = (accountType == "CHECKING") ? new CheckingAccount() : new SavingsAccount();

        account.setAccountNumber(accNum);
        account.setBalance(initialDeposit);
        account.setOwner(user);
        accountRepository.save(account);
        return accountType + " account created successfully. Account Number: " + accNum;
    }

    public List<Account> viewAccounts(User user) {
        return accountRepository.findByOwner(user);
    }

    private String generateAccountNumber() {
        long count = accountRepository.count() + 10001;
        return String.valueOf(count);
    }

    public String getAccountSummary(Account account) {
        return String.format("Account #%s | Type: %s | Balance: $%.2f | Status: %s",
                account.getAccountNumber(),
                ((account instanceof SavingsAccount) ? "SAVINGS" : "CHECKING"),
                account.getBalance(),
                account.getStatus());
    }

    public Optional<Account> getAccountByNumberForUser(String accNum, User user) {
        return accountRepository.findByOwner(user).stream()
                .filter(a -> a.getAccountNumber().equals(accNum))
                .findFirst();
    }

    public String updateAccountStatus(String accountNumber, Account.AccountStatus status) {
        return accountRepository.findAll().stream()
                .filter(a -> a.getAccountNumber().equals(accountNumber))
                .findFirst()
                .map(a -> {
                    a.setStatus(status);
                    accountRepository.save(a);
                    return "Account status updated.";
                }).orElse("Account not found.");
    }

    public void insertAccount(Account account) {
        accountRepository.save(account);
    }
}