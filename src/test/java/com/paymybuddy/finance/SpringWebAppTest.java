package com.paymybuddy.finance;

import static com.paymybuddy.finance.constants.Constants.PAY_MY_BUDDY_BANK;
import static com.paymybuddy.finance.constants.Constants.PAY_MY_BUDDY_GENERIC_USER;
import static com.paymybuddy.finance.constants.Constants.USER_GENERIC_BANK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.paymybuddy.finance.constants.Constants;
import com.paymybuddy.finance.model.Account;
import com.paymybuddy.finance.model.Bank;
import com.paymybuddy.finance.model.Person;
import com.paymybuddy.finance.model.Transaction;
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

    private static final String TEST_BANK = "TEST_BANK";
    private static final int AMOUNT_1000 = 1000;
    private static final int AMOUNT_5000 = 5000;
    private static final int AMOUNT_10000 = 10000;
    private static final String FRIEND_EMAIL = "friend.friend@friend.com";
    private static final String FRIEND = "Friend";
    private static final String USER_EMAIL = "user@user.com";
    private static final String USER = "user";

    // Some Objects are created at initialization
    private static final int OFFSET_BANK = 2;
    private static final int OFFSET_PERSON = 1;
    private static final int OFFSET_ACCOUNT = 1;

    @Autowired
    MockMvc mvc;

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

    @Autowired
    ITransactionService transactionService;

    @BeforeAll
    public void setUp() {
	financeService.initApplication();
    }

    @Test
    @WithMockUser()
    public void shouldReturnDefaultMessage() throws Exception {
	mvc.perform(get("/login")).andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void userLoginTest() throws Exception {
	mvc.perform(formLogin("/login").user(PAY_MY_BUDDY_GENERIC_USER).password(
		"password")).andExpect(authenticated());
    }

    @Test
    @Order(1)
    public void testCreatePersons() {
	// WHEN
	personService.createPerson(USER, USER_EMAIL);
	personService.createPerson(FRIEND, FRIEND_EMAIL);

	// THEN
	assertTrue(personService.findAllPersons().size() == OFFSET_PERSON + 2);
	assertNotNull(personService.findPersonByName(FRIEND));
	assertNotNull(personService.findFetchWithContactAccountsPersonByName(FRIEND));
	assertNotNull(personService.findFetchWithAccountsTransactionsPersonByName(FRIEND));
	assertNotNull(personService.findFetchWithAllPersonByName(FRIEND));
    }

    @Test
    @Order(2)
    public void testCreateBanks() {
	// WHEN
	bankService.createBank(TEST_BANK);

	// THEN
	assertTrue(bankService.findAllBanks().size() == OFFSET_BANK + 1);
	assertNotNull(bankService.findBankByName(TEST_BANK));
    }

    @Test
    @Order(3)
    public void testCreateAccounts() {

	// GIVEN
	Person user = personService.findFetchWithAccountsPersonByName(USER);
	Person friend = personService.findFetchWithAccountsPersonByName(FRIEND);

	Bank bankGeneric = bankService.findWithAccountsBankByName(USER_GENERIC_BANK);
	Bank bankPayMyBuddy = bankService.findWithAccountsBankByName(PAY_MY_BUDDY_BANK);

	accountService.createAccount(10000, user, bankGeneric);
	accountService.createAccount(0, user, bankPayMyBuddy);

	// WHEN
	accountService.createAccount(10000, friend, bankPayMyBuddy);

	// THEN
	assertTrue(accountService.findAllAccounts().size() == OFFSET_ACCOUNT + 3);
	assertNotNull(accountService.findAccountByPersonNameAndBankName(USER,
		PAY_MY_BUDDY_BANK));
	assertNotNull(accountService.findFetchTransactionsAccountByPersonNameAndBankName(USER,
		PAY_MY_BUDDY_BANK));
	assertNotNull(accountService.findFetchWithContactPersonsAccountByPersonNameAndBankName(USER,
		PAY_MY_BUDDY_BANK));

	assertTrue(bankPayMyBuddy.getAccounts().size() == 3);
	assertTrue(user.getAccounts().size() == 2);
	assertTrue(friend.getAccounts().size() == 1);
    }

    @Test
    @Order(4)
    public void testCreateTransactions() {

	// BANK TO BUDDY TRANSACTION

	// GIVEN
	Account accountUserPayMyBuddy = accountService.findFetchTransactionsAccountByPersonNameAndBankName(
		USER,
		PAY_MY_BUDDY_BANK);
	Account accountUserGeneric = accountService.findFetchTransactionsAccountByPersonNameAndBankName(
		USER,
		USER_GENERIC_BANK);

	assertNotNull(accountUserPayMyBuddy);
	assertNotNull(accountUserGeneric);

	assertTrue(accountUserGeneric.getAmount() == AMOUNT_10000);
	assertTrue(accountUserPayMyBuddy.getAmount() == 0);

	// WHEN
	List<Transaction> transactions = financeService.createTransactions(accountUserGeneric,
		accountUserPayMyBuddy, AMOUNT_5000, "virement My Bank -> My Buddy");

	// THEN
	assertTrue(accountUserGeneric.getAmount() == AMOUNT_5000);
	assertTrue(accountUserPayMyBuddy.getAmount() == AMOUNT_5000);

	assertThat(transactions.size() == 1);
	Transaction transactionBankToBuddy = transactions.get(0);
	assertTrue(transactionBankToBuddy.getAmount() == AMOUNT_5000);
	assertTrue(transactionBankToBuddy.getTransactionType() == Transaction.TransactionType.BANK_TO_BUDDY);

	assertTrue(transactionService.findAllTransactions().size() == 1);

	// BUDDY TO BUDDY TRANSACTION

	// GIVEN
	Account accountFriendPayMyBuddy = accountService.findFetchTransactionsAccountByPersonNameAndBankName(FRIEND,
		PAY_MY_BUDDY_BANK);

	assertNotNull(accountUserPayMyBuddy);
	assertNotNull(accountFriendPayMyBuddy);
	assertTrue(accountUserPayMyBuddy.getAmount() == AMOUNT_5000);
	assertTrue(accountFriendPayMyBuddy.getAmount() == AMOUNT_10000);

	// WHEN
	transactions = financeService.createTransactions(accountUserPayMyBuddy,
		accountFriendPayMyBuddy, AMOUNT_1000, "virement My Buddy -> My Buddy");

	// THEN
	Account genericAccountPayMyBuddy = accountService.findFetchTransactionsAccountByPersonNameAndBankName(
		PAY_MY_BUDDY_GENERIC_USER,
		PAY_MY_BUDDY_BANK);

	assertTrue(transactions.size() == 2);
	Transaction transaction = transactions.get(0);
	Transaction transactionCommission = transactions.get(1);

	assertTrue(accountUserPayMyBuddy.getAmount() == AMOUNT_5000 - (1 + Constants.COMMISSION_RATE) * AMOUNT_1000);
	assertTrue(accountFriendPayMyBuddy.getAmount() == AMOUNT_10000 + AMOUNT_1000);
	assertTrue(genericAccountPayMyBuddy.getAmount() == Constants.COMMISSION_RATE * AMOUNT_1000);

	assertTrue(transactionService.findAllTransactions().size() == 3);

	assertTrue(transaction.getTransactionType() == Transaction.TransactionType.BUDDY_TO_BUDDY);
	assertTrue(transaction.getAmount() == AMOUNT_1000);

	assertTrue(transactionCommission.getAmount() == Constants.COMMISSION_RATE * AMOUNT_1000);
	assertTrue(transactionCommission.getTransactionType() == Transaction.TransactionType.COMMISSION);

	Set<Transaction> transactionsTo = genericAccountPayMyBuddy.getTransactionsTo();
	assertNotNull(transactionsTo);
	assertThat(transactionsTo.size()).isNotEqualTo(0);
    }

    @Test
    @Order(5)
    public void testCreateContactAccount() {
	// GIVEN
	Person person = personService.findFetchWithContactAccountsPersonByName(USER);
	Account accountFriendPayMyBuddy = accountService.findFetchWithContactPersonsAccountByPersonNameAndBankName(
		FRIEND,
		PAY_MY_BUDDY_BANK);
	assertNotNull(person);
	assertNotNull(accountFriendPayMyBuddy);

	// WHEN
	accountService.createContactAccount(person, accountFriendPayMyBuddy);

	// THEN
	assertTrue(person.getContactAccounts().size() == 1);
    }

    @Test
    @Order(6)
    public void testRemoveContactAccount() {
	// GIVEN
	Person person = personService.findFetchWithContactAccountsPersonByName(USER);
	Account accountFriendPayMyBuddy = accountService.findFetchWithContactPersonsAccountByPersonNameAndBankName(
		FRIEND,
		PAY_MY_BUDDY_BANK);
	assertNotNull(person);
	assertNotNull(accountFriendPayMyBuddy);
	assertTrue(person.getContactAccounts().size() == 1);

	// WHEN
	accountService.removeContactAccount(person, accountFriendPayMyBuddy);

	// THEN
	assertTrue(person.getContactAccounts().size() == 0);
    }
}
