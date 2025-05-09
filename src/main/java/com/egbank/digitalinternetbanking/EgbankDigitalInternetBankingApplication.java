package com.egbank.digitalinternetbanking;

import com.egbank.digitalinternetbanking.service.BankService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class EgbankDigitalInternetBankingApplication {

	public static void main (String[]args){
		ConfigurableApplicationContext context = SpringApplication.run(EgbankDigitalInternetBankingApplication.class, args);
		BankService bank = context.getBean(BankService.class);
		bank.run();
	}
}