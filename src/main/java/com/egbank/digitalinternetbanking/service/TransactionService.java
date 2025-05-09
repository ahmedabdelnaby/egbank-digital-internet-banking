package com.egbank.digitalinternetbanking.service;

import com.egbank.digitalinternetbanking.model.account.Account;
import com.egbank.digitalinternetbanking.model.transaction.Transaction;
import com.egbank.digitalinternetbanking.model.account.CheckingAccount;
import com.egbank.digitalinternetbanking.model.account.SavingsAccount;
import com.egbank.digitalinternetbanking.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    private final ConfigService configService;

    @Autowired
    public TransactionService(AccountService accountService,
                              TransactionRepository transactionRepository,
                              ConfigService configService) {
        this.accountService = accountService;
        this.transactionRepository = transactionRepository;
        this.configService = configService;
    }

    public String deposit(Long accId, Double amount) {
        return accountService.findAccountById(accId).map(account -> {
            account.setBalance(account.getBalance() + amount);
            accountService.insertAccount(account);
            saveTransaction(Transaction.TransactionType.DEPOSIT,
                                               amount, account, null,
                                                Transaction.TransactionStatus.COMPLETED);
            return "Deposit successful.";
        }).orElse("Account not found.");
    }

    public String withdraw(Long accountId, Double amount) {
        double minBalance = configService.getSavingsAccMinBalance();
        int withdrawalLimit = configService.getSavingsAccWithdrawalLimit();

        return accountService.findAccountById(accountId).map(account -> {
            String msg = "";
            // withdraw based on the account type
            if (account instanceof SavingsAccount savingsAccount) {
                msg = savingsAccount.withdraw(amount,
                        withdrawalLimit,
                        minBalance);
            } else if (account instanceof CheckingAccount checkingAccount) {
                msg = checkingAccount.withdraw(amount);
            }

            accountService.insertAccount(account);
            saveTransaction(Transaction.TransactionType.WITHDRAWAL, amount, account,
                                                null, Transaction.TransactionStatus.COMPLETED);
            return String.format("%s - Date: %s, Amount: $%.2f, Balance: $%.2f", msg, new Date(), amount, account.getBalance());
        }).orElse("Account not found.");
    }

    public String transfer(Long sourceId, Long destId, Double amount) {
        Optional<Account> fromOpt = accountService.findAccountById(sourceId);
        Optional<Account> toOpt = accountService.findAccountById(destId);

        if (fromOpt.isEmpty() || toOpt.isEmpty()) return "Invalid account(s).";

        Account from = fromOpt.get();
        Account to = toOpt.get();

        if (from.getBalance().compareTo(amount) < 0) return "Insufficient funds.";

        if (from instanceof SavingsAccount savingsAccount &&
                savingsAccount.getWithdrawalsThisMonth() >= configService.getSavingsAccWithdrawalLimit())
            return "Savings withdrawal limit reached.Savings account withdrawal limit exceeded (3 per month).";

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);
        if (from instanceof SavingsAccount savingsAccount)
            savingsAccount.setWithdrawalsThisMonth(savingsAccount.getWithdrawalsThisMonth() + 1);

        accountService.insertAccount(from);
        accountService.insertAccount(to);

        saveTransaction(Transaction.TransactionType.TRANSFER, amount, from, to,
                                                    Transaction.TransactionStatus.COMPLETED);
        return String.format("%s - Date: %s, Amount: $%.2f, Balance: $%.2f", "Transfer successful.", new Date(), amount, from.getBalance());
    }

    public List<Transaction> viewTransactions(Long accountId) {
        return accountService.findAccountById(accountId)
                .map(transactionRepository::findBySourceAccount)
                .orElse(Collections.emptyList());
    }

    private void saveTransaction(Transaction.TransactionType type, Double amount,
                                                    Account from, Account to, Transaction.TransactionStatus status) {
        Transaction transaction = new Transaction();
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setSourceAccount(from);
        transaction.setDestinationAccount(to);
        transaction.setStatus(status);
        transactionRepository.save(transaction);
    }

    public List<Transaction> getLastTransactions(Long accountId, int limit) {
        return accountService.findAccountById(accountId)
                .map(account -> transactionRepository.findBySourceAccount(account).stream()
                        .sorted(Comparator.comparing(Transaction::getTransactionDate).reversed())
                        .limit(limit)
                        .toList())
                .orElse(Collections.emptyList());
    }

    public List<Transaction> getAllSystemTransactions() {
        return transactionRepository.findAll();
    }
}