package com.paymybuddy.finance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import com.paymybuddy.finance.service.IFinanceService;

/**
 * 
 * @author trimok
 *
 */
@SpringBootApplication
public class PayMyBuddyFinanceApplication {

    @Autowired
    IFinanceService financeService;

    public static void main(String[] args) {
	SpringApplication.run(PayMyBuddyFinanceApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
	// financeService.initApplication();
    }
}
