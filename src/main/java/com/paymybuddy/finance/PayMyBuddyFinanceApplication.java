package com.paymybuddy.finance;

import static com.paymybuddy.finance.constants.Constants.PAY_MY_BUDDY_BANK;
import static com.paymybuddy.finance.constants.Constants.PAY_MY_BUDDY_GENERIC_USER;
import static com.paymybuddy.finance.constants.Constants.PAY_MY_BUDDY_GENERIC_USER_PASSWORD_ENCODED;
import static com.paymybuddy.finance.constants.Constants.USER_GENERIC_BANK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.provisioning.UserDetailsManager;

import com.paymybuddy.finance.dto.UserLogin;
import com.paymybuddy.finance.model.Bank;
import com.paymybuddy.finance.model.Person;
import com.paymybuddy.finance.security.SecureUser;
import com.paymybuddy.finance.service.IAccountService;
import com.paymybuddy.finance.service.IBankService;
import com.paymybuddy.finance.service.IFinanceService;
import com.paymybuddy.finance.service.IPersonService;

/**
 * 
 * @author trimok
 *
 */
@SpringBootApplication
public class PayMyBuddyFinanceApplication {

    @Autowired
    UserDetailsManager userDetailsManager;

    @Autowired
    IFinanceService financeService;

    @Autowired
    IPersonService personService;

    @Autowired
    IBankService bankService;

    @Autowired
    IAccountService accountService;

    public static void main(String[] args) {
	SpringApplication.run(PayMyBuddyFinanceApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {

	financeService.deleteAll();

	Bank payMyBuddyBank = bankService.createBank(PAY_MY_BUDDY_BANK);
	bankService.createBank(USER_GENERIC_BANK);

	userDetailsManager.createUser(
		new SecureUser(new UserLogin(PAY_MY_BUDDY_GENERIC_USER, PAY_MY_BUDDY_GENERIC_USER_PASSWORD_ENCODED)));
	Person payMyBuddyGenericUser = personService.findFetchWithAccountsPersonByName(PAY_MY_BUDDY_GENERIC_USER);

	accountService.createAccount(0, payMyBuddyGenericUser, payMyBuddyBank);
    }
}
