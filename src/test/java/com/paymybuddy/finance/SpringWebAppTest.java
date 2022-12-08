package com.paymybuddy.finance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.paymybuddy.finance.constants.Constants;
import com.paymybuddy.finance.model.Account;
import com.paymybuddy.finance.model.Bank;
import com.paymybuddy.finance.model.Person;
import com.paymybuddy.finance.service.IAccountService;
import com.paymybuddy.finance.service.IBankService;
import com.paymybuddy.finance.service.IFinanceService;
import com.paymybuddy.finance.service.IPersonService;
import com.paymybuddy.finance.service.ITransactionService;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(value = org.junit.jupiter.api.MethodOrderer.OrderAnnotation.class)
public class SpringWebAppTest {

    private static final int AMOUNT_9000 = 9000;
    private static final int AMOUNT_1000 = 1000;
    private static final int AMOUNT_10000 = 10000;
    private static final String PAY_MY_BUDDY_BANK = "PayMyBuddy";
    private static final String BNP_BANK = "BNP";
    private static final String FRIEND_EMAIL = "friend.friend@friend.com";
    private static final String FRIEND = "Friend";
    private static final String USER_EMAIL = "gogol.googelisant@gmail.com";
    private static final String USER = "Gogol Googelisant";

    @Autowired
    MockMvc mvc;

    @Autowired
    IFinanceService financeService;

    @Autowired
    IPersonService personService;

    @Autowired
    IBankService bankService;

    @Autowired
    IAccountService accountService;

    @Autowired
    ITransactionService transactionService;

    @BeforeAll
    public void setUp() {
	financeService.deleteAll();
    }

    @Test
    public void shouldReturnDefaultMessage() throws Exception {
	mvc.perform(get("/login")).andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void userLoginTest() throws Exception {
	mvc.perform(formLogin("/login").user("person@person.mail").password(
		"person123")).andExpect(authenticated());
    }

    @Test
    @Order(1)
    public void testCreatePersons() {
	personService.createPerson(USER, USER_EMAIL);
	personService.createPerson(FRIEND, FRIEND_EMAIL);

	assertThat(personService.findAllPersons().size() == 2);
	assertNotNull(personService.findPersonByName(FRIEND));
	assertNotNull(personService.findFetchWithContactAccountsPersonByName(FRIEND));
	assertNotNull(personService.findFetchWithAccountsTransactionsPersonByName(FRIEND));
	assertNotNull(personService.findFetchWithAllPersonByName(FRIEND));
    }

    @Test
    @Order(2)
    public void testCreateBanks() {
	bankService.createBank(BNP_BANK);
	bankService.createBank(PAY_MY_BUDDY_BANK);

	assertThat(bankService.findAllBanks().size() == 2);
	assertNotNull(bankService.findBankByName(BNP_BANK));
    }

    @Test
    @Order(3)
    public void testCreateAccounts() {

	Person user = personService.findFetchWithAccountsPersonByName(USER);
	Person friend = personService.findFetchWithAccountsPersonByName(FRIEND);

	Bank bankBnp = bankService.findWithAccountsBankByName(BNP_BANK);
	Bank bankPayMyBuddy = bankService.findWithAccountsBankByName(PAY_MY_BUDDY_BANK);

	accountService.createAccount(10000, user, bankBnp);
	accountService.createAccount(0, user, bankPayMyBuddy);
	accountService.createAccount(10000, friend, bankPayMyBuddy);

	assertThat(accountService.findAllAccounts().size() == 2);
	assertNotNull(accountService.findAccountByPersonNameAndBankName(USER,
		PAY_MY_BUDDY_BANK));
	assertNotNull(accountService.findFetchTransactionsAccountByPersonNameAndBankName(USER,
		PAY_MY_BUDDY_BANK));
	assertNotNull(accountService.findFetchWithContactPersonsAccountByPersonNameAndBankName(USER,
		PAY_MY_BUDDY_BANK));

	assertThat(bankPayMyBuddy.getAccounts().size() == 2);
	assertThat(user.getAccounts().size() == 2);
	assertThat(friend.getAccounts().size() == 1);
    }

    @Test
    @Order(4)
    public void testCreateTransaction() {
	Account accountPayMyBuddy = accountService.findFetchTransactionsAccountByPersonNameAndBankName(
		USER,
		PAY_MY_BUDDY_BANK);
	Account accountFriendPayMyBuddy = accountService.findFetchTransactionsAccountByPersonNameAndBankName(FRIEND,
		PAY_MY_BUDDY_BANK);
	assertNotNull(accountPayMyBuddy);
	assertNotNull(accountFriendPayMyBuddy);
	assertThat(accountPayMyBuddy.getAmount() == AMOUNT_10000);
	assertThat(accountFriendPayMyBuddy.getAmount() == 10000);

	transactionService.createTransaction(accountPayMyBuddy,
		accountFriendPayMyBuddy, AMOUNT_1000, "virement");

	assertThat(transactionService.findAllTransactions().size() == 1);
	assertThat(accountPayMyBuddy.getAmount() == AMOUNT_9000);
	assertThat(accountFriendPayMyBuddy.getAmount() == AMOUNT_10000 - (1 + Constants.COMMISSION_RATE) * AMOUNT_1000);
    }

    @Test
    @Order(5)
    public void testCreateContactAccount() {
	Person person = personService.findFetchWithContactAccountsPersonByName(USER);
	Account accountFriendPayMyBuddy = accountService.findFetchWithContactPersonsAccountByPersonNameAndBankName(
		FRIEND,
		PAY_MY_BUDDY_BANK);
	assertNotNull(person);
	assertNotNull(accountFriendPayMyBuddy);

	accountService.createContactAccount(person, accountFriendPayMyBuddy);

	assertThat(person.getContactAccounts().size() == 1);
    }

    @Test
    @Order(6)
    public void testRemoveContactAccount() {
	Person person = personService.findFetchWithContactAccountsPersonByName(USER);
	Account accountFriendPayMyBuddy = accountService.findFetchWithContactPersonsAccountByPersonNameAndBankName(
		FRIEND,
		PAY_MY_BUDDY_BANK);
	assertNotNull(person);
	assertNotNull(accountFriendPayMyBuddy);
	assertThat(person.getContactAccounts().size() == 1);

	accountService.removeContactAccount(person, accountFriendPayMyBuddy);

	assertThat(person.getContactAccounts().size() == 0);
    }
}
