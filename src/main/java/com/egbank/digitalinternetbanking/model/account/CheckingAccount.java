package com.egbank.digitalinternetbanking.model.account;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@DiscriminatorValue("CHECKING")
@Data
public class CheckingAccount extends Account {

    @Override
    public String withdraw(double amount) {
        if (amount <= 0) {
            return "Withdrawal amount must be positive.";
        }

        if (this.getBalance() < amount) {
            return "Insufficient funds for withdrawal. Current balance: $" + this.getBalance();
        }
        this.setBalance(this.getBalance() - amount);

        return "Withdrawal successful.";
    }
}