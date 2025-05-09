
------------------------------------------------------------------
----------------------------- Schema -----------------------------
------------------------------------------------------------------
DROP DATABASE eg_banking_system;
CREATE DATABASE eg_banking_system;
USE eg_banking_system;

------------------------------------------------------------------
----------------------------- Tables -----------------------------
------------------------------------------------------------------
CREATE TABLE SYSTEM_CONFIGS (
    CONFIG_KEY VARCHAR(50) PRIMARY KEY,
    CONFIG_VALUE VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS USERS (
    USER_ID BIGINT PRIMARY KEY AUTO_INCREMENT,
    USERNAME VARCHAR(50) UNIQUE NOT NULL,
    PASSWORD VARCHAR(100) NOT NULL,
    NAME VARCHAR(100),
    EMAIL VARCHAR(255),
    USER_TYPE VARCHAR(20) NOT NULL,
    SECURITY_CLEARANCE VARCHAR(255),
    ADMIN_PRIVILEGES VARCHAR(255),
    EMPLOYEE_ID VARCHAR(255),
    POSITION VARCHAR(255),
    ADDRESS VARCHAR(255),
    ACTIVE BOOLEAN DEFAULT TRUE,
    LOGGED_IN BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS ACCOUNTS (
    ACCOUNT_ID BIGINT PRIMARY KEY AUTO_INCREMENT,
    ACCOUNT_NUMBER VARCHAR(255) UNIQUE NOT NULL,
    BALANCE DECIMAL(15, 2) DEFAULT 0.00,
    STATUS VARCHAR(20) DEFAULT 'ACTIVE',
    OWNER_ID BIGINT,
    ACCOUNT_TYPE VARCHAR(20) NOT NULL,
    WITHDRAWALS_THIS_MONTH INT DEFAULT 0,

    FOREIGN KEY (OWNER_ID) REFERENCES USERS(USER_ID)
);

CREATE TABLE IF NOT EXISTS TRANSACTIONS (
    ID BIGINT PRIMARY KEY AUTO_INCREMENT,
    TRANSACTION_ID VARCHAR(255) UNIQUE NOT NULL,
    TRANSACTION_DATE DATETIME DEFAULT CURRENT_TIMESTAMP,
    TYPE VARCHAR(20) NOT NULL,
    AMOUNT DECIMAL(15, 2) NOT NULL,
    SOURCE_ACCOUNT_ID BIGINT,
    DESTINATION_ACCOUNT_ID BIGINT,
    STATUS VARCHAR(20) DEFAULT 'COMPLETED',

    FOREIGN KEY (SOURCE_ACCOUNT_ID) REFERENCES ACCOUNTS(ACCOUNT_ID),
    FOREIGN KEY (DESTINATION_ACCOUNT_ID) REFERENCES ACCOUNTS(ACCOUNT_ID)
);

------------------------------------------------------------------
------------------------------ Data ------------------------------
------------------------------------------------------------------
-- system_config test data
INSERT INTO SYSTEM_CONFIGS (CONFIG_KEY, CONFIG_VALUE) VALUES
('SAVINGS_INTEREST_RATE', '2.0'),
('SAVINGS_WITHDRAWAL_LIMIT', '3'),
('SAVINGS_MINIMUM_BALANCE', '100.0');


-- users test data
INSERT INTO `users` (`user_id`, `username`, `password`, `name`, `email`, `user_type`, `security_clearance`, `admin_privileges`, `employee_id`, `position`, `address`, `active`, `logged_in`)
VALUES (1, 'Ahmed123', 'bank456', 'Ahmed AbdElnaby', 'abc@abc.com', 'CUSTOMER', NULL, NULL, NULL, NULL, 'Alexandria', 1, 1);

INSERT INTO `users` (`user_id`, `username`, `password`, `name`, `email`, `user_type`, `security_clearance`, `admin_privileges`, `employee_id`, `position`, `address`, `active`, `logged_in`)
VALUES (2, 'Employee001', 'emp789', 'Eg Bank Employee', 'abc@abc.com', 'EMPLOYEE', NULL, NULL, 'emp10001', 'cashier', NULL, 1, 0);

INSERT INTO `users` (`user_id`, `username`, `password`, `name`, `email`, `user_type`, `security_clearance`, `admin_privileges`, `employee_id`, `position`, `address`, `active`, `logged_in`)
VALUES (3, 'AmrAdmin', 'admin123', 'Amr Elwdad', 'abc@abc.com', 'ADMIN', NULL, NULL, NULL, NULL, NULL, 1, 0);


-- accounts test data
-- INSERT INTO `accounts` (`account_id`, `account_number`, `balance`, `status`, `owner_id`, `account_type`, `withdrawals_this_month`, `interest_rate`)
-- VALUES (1, '10001', 50000, 'ACTIVE', 1, 'CHECKING', 0, NULL);
--
-- INSERT INTO `accounts` (`account_id`, `account_number`, `balance`, `status`, `owner_id`, `account_type`, `withdrawals_this_month`, `interest_rate`)
-- VALUES (2, '10002', 100000, 'ACTIVE', 1, 'SAVINGS', 3, 0.02);
--
-- INSERT INTO `accounts` (`account_id`, `account_number`, `balance`, `status`, `owner_id`, `account_type`, `withdrawals_this_month`, `interest_rate`)
-- VALUES (3, '10003', 150000, 'ACTIVE', 1, 'CHECKING', 0, NULL);


-- transactions test data
-- INSERT INTO `transactions` (`id`, `amount`, `status`, `transaction_date`, `transaction_id`, `type`, `destination_account_id`, `source_account_id`)
-- VALUES (1, 50, 'COMPLETED', '2025-05-08 11:10:34.303491', '3e5326a2-f5da-4124-92ee-0d3230d416fe', 'WITHDRAWAL', NULL, 2);
--
-- INSERT INTO `transactions` (`id`, `amount`, `status`, `transaction_date`, `transaction_id`, `type`, `destination_account_id`, `source_account_id`)
-- VALUES (2, 50, 'COMPLETED', '2025-05-08 11:10:50.972340', '8b699b6e-f0cd-41c8-bdc9-818365eed5ea', 'WITHDRAWAL', NULL, 2);
--
-- INSERT INTO `transactions` (`id`, `amount`, `status`, `transaction_date`, `transaction_id`, `type`, `destination_account_id`, `source_account_id`)
-- VALUES (3, 100, 'COMPLETED', '2025-05-08 11:11:33.871894', '103815b3-921c-418c-ac30-aae6be1b8960', 'DEPOSIT', NULL, 1);
--
-- INSERT INTO `transactions` (`id`, `amount`, `status`, `transaction_date`, `transaction_id`, `type`, `destination_account_id`, `source_account_id`)
-- VALUES (4, 1000, 'COMPLETED', '2025-05-08 11:11:43.597086', '775a4239-9176-42d0-b1b2-9640dab03a94', 'DEPOSIT', NULL, 1);
--
-- INSERT INTO `transactions` (`id`, `amount`, `status`, `transaction_date`, `transaction_id`, `type`, `destination_account_id`, `source_account_id`)
-- VALUES (5, 500, 'COMPLETED', '2025-05-08 11:12:54.381699', '80a88894-9318-47dc-bf00-7c3a2420d92e', 'TRANSFER', 2, 1);


-- -- Deposit into checking
-- INSERT INTO transactions (type,  amount,  status,  source_account_id)
-- VALUES ('DEPOSIT',  200.00,  'SUCCESS',  1);
--
-- -- Withdrawal from savings
-- INSERT INTO transactions (type,  amount,  status,  source_account_id)
-- VALUES ('WITHDRAWAL',  100.00,  'SUCCESS',  2);
--
-- -- Transfer from savings to checking
-- INSERT INTO transactions (type,  amount,  status,  source_account_id,  destination_account_id)
-- VALUES ('TRANSFER',  150.00,  'SUCCESS',  2,  1);