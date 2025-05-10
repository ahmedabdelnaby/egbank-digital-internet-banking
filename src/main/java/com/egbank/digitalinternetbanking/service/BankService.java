package com.egbank.digitalinternetbanking.service;

import com.egbank.digitalinternetbanking.model.config.SystemConfig;
import com.egbank.digitalinternetbanking.model.transaction.Transaction;
import com.egbank.digitalinternetbanking.model.account.Account;
import com.egbank.digitalinternetbanking.model.account.CheckingAccount;
import com.egbank.digitalinternetbanking.model.user.Admin;
import com.egbank.digitalinternetbanking.model.user.Customer;
import com.egbank.digitalinternetbanking.model.user.Employee;
import com.egbank.digitalinternetbanking.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Optional;
import java.util.Scanner;

@Service
public class BankService {
    private final UserService userService;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final ConfigService configService;
    private User currentUser;
    private String currentUserRole;
    private final Scanner scanner = new Scanner(System.in);

    @Autowired
    public BankService(UserService userService,
                       AccountService accountService,
                       TransactionService transactionService,
                       ConfigService configService) {
        this.userService = userService;
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.configService = configService;
    }

    public void run() {
        while (true) {
            System.out.println("===== Welcome to EGBank - Digital Internet Banking System =====");
            System.out.println("1. Login\n2. Reset Password (Admin only)\n3. Exit");
            switch (scanner.nextLine()) {
                case "1" -> loginFlow();
                case "2" -> resetPasswordFlow();
                case "3" -> {
                    if (currentUser != null && currentUser.isLoggedIn())
                        userService.logout(currentUser);
                    System.out.println("Goodbye.");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void loginFlow() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        userService.login(username, password).ifPresentOrElse(user -> {
            currentUser = user;

            currentUserRole = getUserRole(user);
            System.out.println("\nWelcome, " + user.getName() + " (" + currentUserRole + ")");

            switch (currentUserRole) {
                case "ADMIN"    -> adminMenu();
                case "EMPLOYEE" -> employeeMenu();
                case "CUSTOMER" -> customerMenu();
            }

            userService.logout(user);
        }, () -> System.out.println("Invalid username or password. Or account is inactive."));
    }

    private void resetPasswordFlow() {
        System.out.print("Admin username: ");
        String adminUser = scanner.nextLine();
        System.out.print("Target username: ");
        String target = scanner.nextLine();
        System.out.print("New password: ");
        String newPass = scanner.nextLine();

        System.out.println(userService.resetPassword(adminUser, target, newPass));
    }

    private String getUserRole(User user) {
        return (user instanceof Admin) ? "ADMIN" :
                (user instanceof Employee) ? "EMPLOYEE" : "CUSTOMER";
    }

    /** ================================================================================================================================== */
    /** =========================================================== ADMIN MENU =========================================================== */
    /** ================================================================================================================================== */
    private void adminMenu() {
        while (true) {
            System.out.println("\n------- Admin Dashboard -------");
            System.out.println("1. View All Accounts");
            System.out.println("2. View All Transactions");
            System.out.println("3. User Management");
            System.out.println("4. Modify Account Status");
            System.out.println("5. Configure System Parameters");
            System.out.println("6. Logout");

            switch (scanner.nextLine()) {
                case "1" -> viewAllAccounts();
                case "2" -> viewAllTransactions();
                case "3" -> userManagementMenu();
                case "4" -> modifyAccountStatusFlow();
                case "5" -> configureSystemParameters();
                case "6" -> { return; }
                default  -> System.out.println("Invalid option.");
            }
        }
    }

    private void viewAllAccounts() {
        accountService.getAllAccounts().forEach(acc ->
                System.out.println(accountService.getAccountSummary(acc)));
    }

    private void viewAllTransactions() {
        transactionService.getAllSystemTransactions()
                .stream()
                .sorted(Comparator.comparing(Transaction::getTransactionDate).reversed())
                .forEach(t -> System.out.printf("Date: %s | Type: %s | Amount: $%.2f | From: %s | To: %s | Status: (%s)%n",
                        t.getTransactionDate(),
                        t.getType(),
                        t.getAmount(),
                        t.getSourceAccount() != null ? t.getSourceAccount().getAccountNumber() : "-",
                        t.getDestinationAccount() != null ? t.getDestinationAccount().getAccountNumber() : "-",
                        t.getStatus()));
    }

    private void userManagementMenu() {
        while (true) {
            System.out.println("\n--- User Management ---");
            System.out.println("1. Create User");
            System.out.println("2. Modify User");
            System.out.println("3. Enable User");
            System.out.println("4. Disable User");
            System.out.println("5. List All Users");
            System.out.println("6. Back");

            switch (scanner.nextLine()) {
                case "1" -> createUserFlow();
                case "2" -> modifyUserFlow();
                case "3" -> switchUserFlow(true);
                case "4" -> switchUserFlow(false);
                case "5" -> userService.getAllUsers().forEach(u ->
                        System.out.printf("ID: %d | Name: %s | Username: %s (%s) | Active: %b%n",
                                u.getId(), u.getName(), u.getUsername(), getUserRole(u), u.isActive()));
                case "6" -> { return; }
                default  -> System.out.println("Invalid choice.");
            }
        }
    }

    private void createUserFlow() {
        System.out.print("New username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Role (1. CUSTOMER or 2. EMPLOYEE): ");

        try {
            String role = "";
            switch (scanner.nextLine()) {
                case "1" -> role = "CUSTOMER";
                case "2" -> role = "EMPLOYEE";
                default -> System.out.println("Invalid role.");
            }
            System.out.println(userService.createUser(
                    currentUser.getUsername(), username, password, name, role));
        } catch (Exception e) {
            System.out.println("Invalid role.");
        }
    }

    private void modifyUserFlow() {
        System.out.print("Username to modify: ");
        String username = scanner.nextLine();
        System.out.print("New name (or leave blank): ");
        String name = scanner.nextLine();
        System.out.print("New password (or leave blank): ");
        String pass = scanner.nextLine();
        System.out.println(userService.updateUser(
                currentUser.getUsername(), username, name, pass));
    }

    private void switchUserFlow(boolean activateUser) {
        System.out.print("Username to disable: ");
        String username = scanner.nextLine();
        System.out.println(userService.switchUser(
                currentUser.getUsername(), username, activateUser));
    }

    private void modifyAccountStatusFlow() {
        System.out.print("Enter account number: ");
        String accNum = scanner.nextLine();
        System.out.print("Enter new status (1. ACTIVE/ 2. INACTIVE/ 3. FROZEN): ");
        String status;
        try {
            status = scanner.nextLine();
            if (!(status.equals("1") || status.equals("2") || status.equals("3"))) {
                System.out.println("Invalid account status.");
                return;
            }

            Account.AccountStatus newStatus = (status.equals("1")) ?
                                    Account.AccountStatus.ACTIVE : (status.equals("2")) ?
                                    Account.AccountStatus.INACTIVE : Account.AccountStatus.FROZEN;
            System.out.println(accountService.updateAccountStatus(accNum, newStatus));
        } catch (Exception e) {
            System.out.println("Invalid status.");
        }
    }

    private void configureSystemParameters() {
        System.out.println("Current Settings:");
        configService.getAllConfigs().forEach(c ->
                System.out.println(c.getConfigKey() + " = " + c.getConfigValue()));

        configService.getAllConfigs();

        System.out.print("Enter parameter key to update (" +
                        String.join("/", configService.getAllConfigs().stream()
                        .map(SystemConfig::getConfigKey)
                        .toList()) + "): ");
        String key = scanner.nextLine();
        System.out.print("Enter new value: ");
        String value = scanner.nextLine();

        String result = configService.updateParameter(key, value);
        System.out.println(result);
    }

    /** ================================================================================================================================== */
    /** ========================================================= EMPLOYEE MENU ========================================================== */
    /** ================================================================================================================================== */
    private void employeeMenu() {
        while (true) {
            System.out.println("\n------- Employee Dashboard -------");
            System.out.println("1. Search Customer by Username");
            System.out.println("2. View Customer Accounts");
            System.out.println("3. Generate Customer Report");
            System.out.println("4. Logout");

            switch (scanner.nextLine()) {
                case "1" -> searchCustomerFlow();
                case "2" -> viewCustomerAccountsFlow();
                case "3" -> generateCustomerReportFlow();
                case "4" -> { return; }
                default  -> System.out.println("Invalid option.");
            }
        }
    }

    private void searchCustomerFlow() {
        System.out.print("Enter username to search: ");
        String username = scanner.nextLine();
        userService.findByUsername(username)
                .ifPresentOrElse(u -> System.out.printf("User: %s | Role: %s | Active: %s%n",
                                                                                u.getName(), getUserRole(u), u.isActive()),
                        () -> System.out.println("User not found."));
    }

    private void viewCustomerAccountsFlow() {
        System.out.print("Enter customer username: ");
        String username = scanner.nextLine();
        userService.findByUsername(username)
                .ifPresentOrElse(user -> {
                    if (!(user instanceof Customer)) {
                        System.out.println("Target user is not a customer.");
                        return;
                    }
                    accountService.viewAccounts(user).forEach(this::printAccount);
                }, () -> System.out.println("User not found."));
    }

    private void generateCustomerReportFlow() {
        System.out.print("Enter customer username: ");
        String username = scanner.nextLine();
        userService.findByUsername(username)
                .ifPresentOrElse(user -> {
                    if (!(user instanceof Customer)) {
                        System.out.println("Target user is not a customer.");
                        return;
                    }
                    accountService.viewAccounts(user).forEach(acc -> {
                        System.out.println(accountService.getAccountSummary(acc));
                        transactionService.getLastTransactions(acc.getId(), 5)
                                .forEach(t -> System.out.printf("  [%s] %s $%.2f (%s)%n",
                                                t.getTransactionDate(), t.getType(), t.getAmount(), t.getStatus()));
                        System.out.println();
                    });
                }, () -> System.out.println("Customer not found."));
    }

    /** ================================================================================================================================== */
    /** ========================================================= CUSTOMER MENU ========================================================== */
    /** ================================================================================================================================== */
    private void customerMenu() {
        while (true) {
            System.out.println("\n------- Customer Dashboard -------");
            System.out.println("\n1. Open New Account\n" +
                                "2. View My Accounts\n" +
                                "3. Deposit\n" +
                                "4. Withdraw\n" +
                                "5. Transfer\n" +
                                "6. View Transactions\n" +
                                "7. View Account Summary\n" +
                                "8. Logout"
            );
            switch (scanner.nextLine()) {
                case "1" -> openAccountFlow();
                case "2" -> accountService.viewAccounts(currentUser).forEach(this::printAccount);
                case "3" -> depositFlow();
                case "4" -> withdrawFlow();
                case "5" -> transferFlow();
                case "6" -> transactionHistoryFlow();
                case "7" -> viewAccountSummaryFlow();
                case "8" -> { return; }
                default  -> System.out.println("Invalid option.");
            }
        }
    }

    private void openAccountFlow() {
        try {
            System.out.print("Enter account type (1. CHECKING/ 2. SAVINGS): ");
            String accountType = scanner.nextLine();

            if (!(accountType.equals("1") || accountType.equals("2"))) {
                System.out.println("Invalid account type.");
                return;
            }
            accountType = (accountType.equals("1")) ? "CHECKING" : "SAVINGS";

            System.out.print("Enter initial deposit amount: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine());

            String msg = accountService.createAccount(currentUser, accountType, amount.doubleValue());
            System.out.println(msg);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid numeric value.");
        }
    }

    private void printAccount(Account account) {
        System.out.printf("Account #%s | Type: %s | Balance: $%.2f | Status: %s\n",
                account.getAccountNumber(),
                ((account instanceof CheckingAccount) ? "CHECKING" : "SAVINGS"),
                account.getBalance(),
                account.getStatus());
    }

    private void depositFlow() {
        try {
            System.out.print("Enter Account Number: ");
            String accNum = scanner.nextLine();
            System.out.print("Enter amount: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine());

            accountService.viewAccounts(currentUser).stream()
                    .filter(a -> a.getAccountNumber().equals(accNum))
                    .filter(a -> a.getStatus() == Account.AccountStatus.ACTIVE)
                    .findFirst()
                    .ifPresentOrElse(
                            acc -> System.out.println(transactionService.deposit(
                                    acc.getId(), amount.doubleValue())),
                            () -> {
                                                transactionService.saveTransaction(Transaction.TransactionType.DEPOSIT,
                                                                                    amount.doubleValue(),
                                                                                    accountService.getAccountByAccountNumber(accNum),
                                                                                    null,
                                                                                    Transaction.TransactionStatus.FAILED);
                                System.out.println("Invalid account or you do not have access to this account or it does not active.");
                            }
                    );
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid amount. Please enter a numeric value greater than zero.");
        }
    }

    private void withdrawFlow() {
        try {
            System.out.print("Enter Account Number: ");
            String accNum = scanner.nextLine();
            System.out.print("Enter amount: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine());

            accountService.viewAccounts(currentUser).stream()
                    .filter(a -> a.getAccountNumber().equals(accNum))
                    .filter(a -> a.getStatus() == Account.AccountStatus.ACTIVE)
                    .findFirst()
                    .ifPresentOrElse(
                            acc -> System.out.println(transactionService.withdraw(
                                    acc.getId(), amount.doubleValue())),
                            () -> {
                                transactionService.saveTransaction(Transaction.TransactionType.WITHDRAWAL,
                                                                    amount.doubleValue(),
                                                                    accountService.getAccountByAccountNumber(accNum),
                                                                    null,
                                                                    Transaction.TransactionStatus.FAILED);
                                System.out.println("Invalid account or you do not have access to this account or it does not active.");
                            }
                    );
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid amount. Please enter a numeric value greater than zero.");
        }
    }

    private void transferFlow() {
        try {
            System.out.print("From Account Number: ");
            String from = scanner.nextLine();
            System.out.print("To Account Number: ");
            String to = scanner.nextLine();
            System.out.print("Amount: ");
            BigDecimal amount = new BigDecimal(scanner.nextLine());

            Optional<Account> fromAcc = accountService.viewAccounts(currentUser).stream()
                                                        .filter(a -> a.getAccountNumber().equals(from)).findFirst()
                                                        .filter(a -> a.getStatus() == Account.AccountStatus.ACTIVE);

            Optional<Account> toAcc = accountService.getAllAccounts().stream()
                                                        .filter(a -> a.getAccountNumber().equals(to)).findFirst()
                                                        .filter(a -> a.getStatus() == Account.AccountStatus.ACTIVE);

            if (fromAcc.isPresent() && toAcc.isPresent()) {
                System.out.println(transactionService.transfer(
                                                    fromAcc.get().getId(),
                                                      toAcc.get().getId(),
                                                     amount.doubleValue())
                );
            } else {
                transactionService.saveTransaction(Transaction.TransactionType.TRANSFER,
                                                    amount.doubleValue(),
                                                    accountService.getAccountByAccountNumber(from),
                                                    accountService.getAccountByAccountNumber(to),
                                                    Transaction.TransactionStatus.FAILED);
                System.out.println("Invalid account or you do not have access to this account or it does not active.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid amount. Please enter a numeric value greater than zero.");
        }
    }

    private void transactionHistoryFlow() {
        System.out.print("Enter Account Number: ");
        String accNum = scanner.nextLine();
        accountService.viewAccounts(currentUser).stream()
                .filter(a -> a.getAccountNumber().equals(accNum))
                .findFirst()
                .ifPresentOrElse(
                        acc -> transactionService.viewTransactions(acc.getId())
                                .stream()
                                .sorted(Comparator.comparing(Transaction::getTransactionDate).reversed())
                                .forEach(t -> System.out.printf("[%s] %s: $%.2f → %s%n",
                                                    t.getTransactionDate(), t.getType(), t.getAmount(), t.getStatus())),
                        () -> System.out.println("Account not found.")
                );
    }

    private void viewAccountSummaryFlow() {
        System.out.print("Enter account number: ");
        String accNum = scanner.nextLine();

        accountService.getAccountByNumberForUser(accNum, currentUser)
                .ifPresentOrElse(acc -> {
                    System.out.println(accountService.getAccountSummary(acc));
                    System.out.println("\nRecent Transactions:");
                    transactionService.getLastTransactions(acc.getId(), 10)
                            .stream()
                            .sorted(Comparator.comparing(Transaction::getTransactionDate).reversed())
                            .forEach(t -> System.out.printf("[%s] %s: $%.2f → %s%n",
                                    t.getTransactionDate(), t.getType(), t.getAmount(), t.getStatus()));
                }, () -> System.out.println("Account not found or access denied."));
    }
}