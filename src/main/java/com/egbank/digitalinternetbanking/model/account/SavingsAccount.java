package com.egbank.digitalinternetbanking.model.account;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@DiscriminatorValue("SAVINGS")
@Data
public class SavingsAccount extends Account {
    private int withdrawalsThisMonth = 0;

    @Override
    public String withdraw(double amount) {
        this.setBalance(this.getBalance() - amount);
        withdrawalsThisMonth++;

        return "Withdrawal successful.";
    }

    public String withdraw(double amount, int withdrawalsLimit, double minimumBalance) {
        String msg = "";

        msg = (withdrawalsThisMonth >= withdrawalsLimit) ?
                    "Savings account withdrawal limit exceeded ("+ withdrawalsLimit + " per month)." :
                (((this.getBalance() - amount) < minimumBalance)) ?
                        "Withdrawal would bring balance below minimum required $" + minimumBalance + "." :
                        withdraw(amount);

        this.setBalance(this.getBalance() - amount);
        withdrawalsThisMonth++;

        return msg;
    }
}