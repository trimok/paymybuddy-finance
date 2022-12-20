package com.paymybuddy.finance;

import static com.paymybuddy.finance.constants.Constants.AMOUNT_BEGIN;
import static com.paymybuddy.finance.constants.Constants.AUTHORITY_USER;
import static com.paymybuddy.finance.constants.Constants.COMMISSION_RATE;
import static com.paymybuddy.finance.constants.Constants.ERROR_ACCOUNTS_MUST_BE_DIFFERENT;
import static com.paymybuddy.finance.constants.Constants.ERROR_ORIGIN_ACCOUNT_AMOUNT_NOT_SUFFICIENT;
import static com.paymybuddy.finance.constants.Constants.ERROR_TRANSACTION_MUST_BE_FROM_BUDDY_ACCOUNT;
import static com.paymybuddy.finance.constants.Constants.PAY_MY_BUDDY_BANK;
import static com.paymybuddy.finance.constants.Constants.PAY_MY_BUDDY_GENERIC_USER;
import static com.paymybuddy.finance.constants.Constants.USER_GENERIC_BANK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.paymybuddy.finance.dto.ContactDTO;
import com.paymybuddy.finance.dto.TransferDTO;
import com.paymybuddy.finance.model.Account;
import com.paymybuddy.finance.model.Person;
import com.paymybuddy.finance.model.Transaction;
import com.paymybuddy.finance.service.IAccountService;
import com.paymybuddy.finance.service.IBankService;
import com.paymybuddy.finance.service.IFinanceService;
import com.paymybuddy.finance.service.IPersonService;
import com.paymybuddy.finance.service.ITransactionService;

@ContextConfiguration
@WithMockUser(username = "user@user.com", authorities = { AUTHORITY_USER })
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@TestMethodOrder(value = org.junit.jupiter.api.MethodOrderer.OrderAnnotation.class)
public class IntegrationTest {

    private static final String SECURE_USER = "user@user.com";
    private static final String SECURE_FRIEND = "friend@friend.com";

    private static final String PASSWORD = "password";
    private static final double AMOUNT_TRANSACTION = 1000;

    @Autowired
    private MockMvc mockMvc;

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

    private Person user = null;
    private Person friend = null;
    private Account accountBuddyUser = null;
    private Account accountBankUser = null;
    private Account accountBuddyFriend = null;
    private Account genericAccountPayMyBuddy = null;

    private TransferDTO transferDTO = null;
    private ContactDTO contactDTO = null;

    private Map<String, Object> sessionAttrs = null;

    @BeforeEach
    public void beforeEach() {
	financeService.deleteAll();
	financeService.initApplication();

	user = financeService.createAuthorityUserPerson(SECURE_USER, PASSWORD);
	friend = financeService.createAuthorityUserPerson(SECURE_FRIEND, PASSWORD);

	accountBankUser = accountService.findAccountByPersonNameAndBankName(SECURE_USER, USER_GENERIC_BANK);
	accountBuddyUser = accountService.findAccountByPersonNameAndBankName(SECURE_USER, PAY_MY_BUDDY_BANK);
	accountBuddyFriend = accountService.findAccountByPersonNameAndBankName(SECURE_FRIEND,
		PAY_MY_BUDDY_BANK);
	genericAccountPayMyBuddy = accountService.findAccountByPersonNameAndBankName(
		PAY_MY_BUDDY_GENERIC_USER,
		PAY_MY_BUDDY_BANK);

	sessionAttrs = new HashMap<String, Object>();
	sessionAttrs.put("person", user);
    }

    @AfterEach
    public void afterEach() {
	financeService.deleteAll();
    }

    @Test
    public void testTransactionBuddyToBank() throws Exception {

	accountBuddyUser.changeAmount(2 * AMOUNT_TRANSACTION);
	accountService.saveAccount(accountBuddyUser);

	transferDTO = new TransferDTO(accountBuddyUser.getId(), accountBankUser.getId(), "virement",
		AMOUNT_TRANSACTION);

	mockMvc.perform(
		MockMvcRequestBuilders.post("/transfer").sessionAttrs(sessionAttrs).flashAttr("transferDTO",
			transferDTO))
		.andExpect(status().isOk());

	accountBuddyUser = accountService.findFetchTransactionsAccountByPersonNameAndBankName(SECURE_USER,
		PAY_MY_BUDDY_BANK);
	accountBankUser = accountService.findFetchTransactionsAccountByPersonNameAndBankName(SECURE_USER,
		USER_GENERIC_BANK);

	assertThat(accountBankUser.getAmount()).isEqualTo(AMOUNT_BEGIN + AMOUNT_TRANSACTION);
	assertThat(accountBuddyUser.getAmount()).isEqualTo(AMOUNT_TRANSACTION);

	assertThat(transactionService.findAllTransactions().size()).isEqualTo(1);
	assertThat(accountBuddyUser.getTransactionsFrom().size()).isEqualTo(1);
	assertThat(accountBankUser.getTransactionsTo().size()).isEqualTo(1);
	Transaction transaction = accountBuddyUser.getTransactionsFrom().iterator().next();

	assertThat(transaction.getAmount()).isEqualTo(AMOUNT_TRANSACTION);
	assertThat(transaction.getAccountFrom().getId()).isEqualTo(accountBuddyUser.getId());
	assertThat(transaction.getAccountTo().getId()).isEqualTo(accountBankUser.getId());
	assertThat(transaction.getTransactionType()).isEqualTo(Transaction.TransactionType.BUDDY_TO_BANK);
    }

    @Test
    public void testTransactionBankToBuddy() throws Exception {

	transferDTO = new TransferDTO(accountBankUser.getId(), accountBuddyUser.getId(), "virement",
		AMOUNT_TRANSACTION);

	mockMvc.perform(
		MockMvcRequestBuilders.post("/transfer").sessionAttrs(sessionAttrs).flashAttr("transferDTO",
			transferDTO))
		.andExpect(status().isOk());

	accountBuddyUser = accountService.findFetchTransactionsAccountByPersonNameAndBankName(SECURE_USER,
		PAY_MY_BUDDY_BANK);
	accountBankUser = accountService.findFetchTransactionsAccountByPersonNameAndBankName(SECURE_USER,
		USER_GENERIC_BANK);

	assertThat(accountBankUser.getAmount()).isEqualTo(AMOUNT_BEGIN - AMOUNT_TRANSACTION);
	assertThat(accountBuddyUser.getAmount()).isEqualTo(AMOUNT_TRANSACTION);

	assertThat(transactionService.findAllTransactions().size()).isEqualTo(1);
	assertThat(accountBankUser.getTransactionsFrom().size()).isEqualTo(1);
	assertThat(accountBuddyUser.getTransactionsTo().size()).isEqualTo(1);
	Transaction transaction = accountBankUser.getTransactionsFrom().iterator().next();

	assertThat(transaction.getAmount()).isEqualTo(AMOUNT_TRANSACTION);
	assertThat(transaction.getAccountFrom().getId()).isEqualTo(accountBankUser.getId());
	assertThat(transaction.getAccountTo().getId()).isEqualTo(accountBuddyUser.getId());
	assertThat(transaction.getTransactionType()).isEqualTo(Transaction.TransactionType.BANK_TO_BUDDY);
    }

    @Test
    public void testTransactionBuddyToBuddy() throws Exception {

	accountBuddyUser.changeAmount(2 * AMOUNT_TRANSACTION);
	accountService.saveAccount(accountBuddyUser);

	transferDTO = new TransferDTO(accountBuddyUser.getId(), accountBuddyFriend.getId(), "virement",
		AMOUNT_TRANSACTION);

	mockMvc.perform(
		MockMvcRequestBuilders.post("/transfer").sessionAttrs(sessionAttrs).flashAttr("transferDTO",
			transferDTO))
		.andExpect(status().isOk());

	accountBuddyUser = accountService.findFetchTransactionsAccountByPersonNameAndBankName(SECURE_USER,
		PAY_MY_BUDDY_BANK);
	accountBuddyFriend = accountService.findFetchTransactionsAccountByPersonNameAndBankName(SECURE_FRIEND,
		PAY_MY_BUDDY_BANK);
	genericAccountPayMyBuddy = accountService.findFetchTransactionsAccountByPersonNameAndBankName(
		PAY_MY_BUDDY_GENERIC_USER,
		PAY_MY_BUDDY_BANK);

	assertThat(accountBuddyFriend.getAmount()).isEqualTo(AMOUNT_TRANSACTION);
	assertThat(accountBuddyUser.getAmount()).isEqualTo((1 - COMMISSION_RATE) * AMOUNT_TRANSACTION);
	assertThat(genericAccountPayMyBuddy.getAmount()).isEqualTo(COMMISSION_RATE * AMOUNT_TRANSACTION);

	assertThat(transactionService.findAllTransactions().size()).isEqualTo(2);
	assertThat(accountBuddyFriend.getTransactionsTo().size()).isEqualTo(1);
	assertThat(accountBuddyUser.getTransactionsFrom().size()).isEqualTo(2);
	Transaction transaction = accountBuddyFriend.getTransactionsTo().iterator().next();

	Transaction transactionCommission = null;
	while ((transactionCommission = (Transaction) accountBuddyUser.getTransactionsFrom().iterator()
		.next()) != null) {
	    if (transactionCommission.getTransactionType() == Transaction.TransactionType.COMMISSION) {
		break;
	    }
	}
	assertNotNull(transactionCommission);
	assertThat(transactionCommission.getAmount()).isEqualTo(COMMISSION_RATE * AMOUNT_TRANSACTION);
	assertThat(transactionCommission.getAccountFrom().getId()).isEqualTo(accountBuddyUser.getId());
	assertThat(transactionCommission.getAccountTo().getId()).isEqualTo(genericAccountPayMyBuddy.getId());
	assertThat(transactionCommission.getTransactionType()).isEqualTo(Transaction.TransactionType.COMMISSION);

	assertThat(transaction.getAmount()).isEqualTo(AMOUNT_TRANSACTION);
	assertThat(transaction.getAccountFrom().getId()).isEqualTo(accountBuddyUser.getId());
	assertThat(transaction.getAccountTo().getId()).isEqualTo(accountBuddyFriend.getId());
	assertThat(transaction.getTransactionType()).isEqualTo(Transaction.TransactionType.BUDDY_TO_BUDDY);
    }

    @Test
    public void testTransactionToBuddyAccountMustBeFromBuddyAccount() throws Exception {

	transferDTO = new TransferDTO(accountBankUser.getId(), accountBuddyFriend.getId(), "virement",
		AMOUNT_TRANSACTION);

	mockMvc.perform(
		MockMvcRequestBuilders.post("/transfer").sessionAttrs(sessionAttrs).flashAttr("transferDTO",
			transferDTO))
		.andExpect(model().attributeExists(ERROR_TRANSACTION_MUST_BE_FROM_BUDDY_ACCOUNT))
		.andExpect(status().isOk());
    }

    @Test
    public void testTransactionBuddyToBuddyAccountsMustBeDifferent() throws Exception {

	accountBuddyUser.changeAmount(2 * AMOUNT_TRANSACTION);
	accountService.saveAccount(accountBuddyUser);

	transferDTO = new TransferDTO(accountBuddyUser.getId(), accountBuddyUser.getId(), "virement",
		AMOUNT_TRANSACTION);

	mockMvc.perform(
		MockMvcRequestBuilders.post("/transfer").sessionAttrs(sessionAttrs).flashAttr("transferDTO",
			transferDTO))
		.andExpect(model().attributeExists(ERROR_ACCOUNTS_MUST_BE_DIFFERENT))
		.andExpect(status().isOk());
    }

    @Test
    public void testTransactionBuddyToBuddyInsufficientAmount() throws Exception {

	transferDTO = new TransferDTO(accountBuddyUser.getId(), accountBuddyFriend.getId(), "virement",
		AMOUNT_TRANSACTION);

	mockMvc.perform(
		MockMvcRequestBuilders.post("/transfer").sessionAttrs(sessionAttrs).flashAttr("transferDTO",
			transferDTO))
		.andExpect(model().attributeExists(ERROR_ORIGIN_ACCOUNT_AMOUNT_NOT_SUFFICIENT))
		.andExpect(status().isOk());

    }

    @Test
    public void testContactAccountAdd() throws Exception {
	Person user = personService.findFetchWithAllPersonByName(SECURE_USER);
	sessionAttrs.put("person", user);
	contactDTO = new ContactDTO(null, accountBuddyFriend.getId(), null);

	mockMvc.perform(
		MockMvcRequestBuilders.post("/addContactAccount").sessionAttrs(sessionAttrs).flashAttr("contactDTO",
			contactDTO))
		.andExpect(status().isOk());

	user = personService.findFetchWithAllPersonByName(SECURE_USER);
	assertThat(user.getContactAccounts().size()).isEqualTo(1);
	assertThat(user.getContactAccounts().iterator().next().getId()).isEqualTo(accountBuddyFriend.getId());
    }

    @Test
    public void testContactAccountRemove() throws Exception {

	Person user = personService.findFetchWithAllPersonByName(SECURE_USER);
	accountBuddyFriend = accountService.findFetchWithContactPersonsAccountByPersonNameAndBankName(SECURE_FRIEND,
		PAY_MY_BUDDY_BANK);
	accountService.createContactAccount(user, accountBuddyFriend);
	user = personService.findFetchWithAllPersonByName(SECURE_USER);
	assertThat(user.getContactAccounts().size()).isEqualTo(1);

	sessionAttrs.put("person", user);
	contactDTO = new ContactDTO(null, null, accountBuddyFriend.getId());

	mockMvc.perform(
		MockMvcRequestBuilders.post("/removeContactAccount").sessionAttrs(sessionAttrs).flashAttr("contactDTO",
			contactDTO))
		.andExpect(status().isOk());

	user = personService.findFetchWithAllPersonByName(SECURE_USER);
	assertThat(user.getContactAccounts().size()).isEqualTo(0);
    }
}
