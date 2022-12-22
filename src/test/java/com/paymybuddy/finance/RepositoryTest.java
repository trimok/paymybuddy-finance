package com.paymybuddy.finance;

import static com.paymybuddy.finance.constants.Constants.PAY_MY_BUDDY_BANK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.paymybuddy.finance.model.Account;
import com.paymybuddy.finance.model.Bank;
import com.paymybuddy.finance.model.Person;
import com.paymybuddy.finance.model.Role;
import com.paymybuddy.finance.model.Transaction;
import com.paymybuddy.finance.model.Transaction.TransactionType;
import com.paymybuddy.finance.repository.AccountRepository;
import com.paymybuddy.finance.repository.BankRepository;
import com.paymybuddy.finance.repository.PersonRepository;
import com.paymybuddy.finance.repository.RoleRepository;
import com.paymybuddy.finance.repository.TransactionRepository;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@TestMethodOrder(value = org.junit.jupiter.api.MethodOrderer.MethodName.class)
public class RepositoryTest {

    private static final String SECURE_USER = "user@user.com";
    private static final String CONTACT_USER = "contact@contact.com";
    private static final String TEST_USER = "test@test.com";
    private static final String TEST_BANK = "testBank";
    private static int NB_PERSON_ACCOUNT;
    private static int NB_PERSON_CONTACT_ACCOUNT;
    private static int NB_TRANSACTION;
    private static int NB_CONTACT_PERSON;
    private static int NB_ACCOUNT_WITHOUT_PERSON;
    private static int NB_BANK_ACCOUNT;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BankRepository bankRepository;

    @Autowired
    private AccountRepository accountRepository;

    private Bank bank;
    private Account account;
    private Account contactAccount;
    private Person person;

    private Bank testBank;
    private Person testPerson;

    @BeforeAll
    public void beforeAll() {
	bank = new Bank(PAY_MY_BUDDY_BANK);
	bankRepository.save(bank);

	testBank = new Bank(TEST_BANK);
	bankRepository.save(testBank);

	person = new Person(SECURE_USER);
	personRepository.save(person);

	testPerson = new Person(TEST_USER);
	personRepository.save(testPerson);

	Person contactPerson = new Person(CONTACT_USER);
	personRepository.save(contactPerson);

	account = new Account(person, bank, 0);
	accountRepository.save(account);
	NB_PERSON_ACCOUNT = 1;

	contactAccount = new Account(contactPerson, bank, 0);
	accountRepository.save(contactAccount);
	NB_PERSON_CONTACT_ACCOUNT = 1;
	NB_CONTACT_PERSON = 1;
	NB_ACCOUNT_WITHOUT_PERSON = 1;
	NB_BANK_ACCOUNT = 2;

	person.addContactAccount(contactAccount);
	personRepository.save(person);

	Transaction transaction = new Transaction(null, account, contactAccount, 1000.0, "virement",
		LocalDateTime.now(), TransactionType.BUDDY_TO_BUDDY);
	transactionRepository.save(transaction);
	NB_TRANSACTION = 1;
    }

    @BeforeEach
    public void beforeEach() {

    }

    @AfterEach
    public void afterEach() {
    }

    @Test
    void testPersonRepositorySaveDeleteFindAll() {
	int nbPersons = personRepository.findAll().size();
	Person person = new Person("personName");
	person = personRepository.save(person);
	assertThat(personRepository.findAll().size()).isEqualTo(nbPersons + 1);
	personRepository.delete(person);
	assertThat(personRepository.findAll().size()).isEqualTo(nbPersons);
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void testPersonRepositoryFindByName() {
	Person personDatabase = personRepository.findByName(person.getName());

	assertNotNull(personDatabase);

	assertThatThrownBy(() -> personDatabase.getContactAccounts().size())
		.isInstanceOf(LazyInitializationException.class);
	assertThatThrownBy(() -> personDatabase.getAccounts().size())
		.isInstanceOf(LazyInitializationException.class);
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void testPersonRepositoryFindFetchWithContactAccountsByName() {
	Person personDatabase = personRepository.findFetchWithContactAccountsByName(person.getName());

	assertNotNull(personDatabase);
	assertNotNull(personDatabase.getContactAccounts());
	assertThat(personDatabase.getContactAccounts().size()).isEqualTo(NB_PERSON_CONTACT_ACCOUNT);

	assertThatThrownBy(() -> personDatabase.getAccounts().size())
		.isInstanceOf(LazyInitializationException.class);
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void testPersonRepositoryFindFetchWithAccountsByName() {
	Person personDatabase = personRepository.findFetchWithAccountsByName(person.getName());

	assertNotNull(personDatabase);
	assertNotNull(personDatabase.getAccounts());
	assertThat(personDatabase.getAccounts().size()).isEqualTo(NB_PERSON_ACCOUNT);

	assertThatThrownBy(() -> personDatabase.getContactAccounts().size())
		.isInstanceOf(LazyInitializationException.class);
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void testPersonRepositoryFindFetchWithAccountsAndTransactionsByName() {
	Person personDatabase = personRepository.findFetchWithAccountsAndTransactionsByName(person.getName());

	assertNotNull(personDatabase);

	Set<Account> accounts = personDatabase.getAccounts();
	assertNotNull(accounts);
	assertThat(accounts.size()).isEqualTo(NB_PERSON_ACCOUNT);
	Account account = accounts.iterator().next();
	assertNotNull(account);

	Set<Transaction> transactions = account.getTransactionsFrom();
	assertNotNull(transactions);
	assertThat(transactions.size()).isEqualTo(NB_TRANSACTION);

	assertThatThrownBy(() -> personDatabase.getContactAccounts().size())
		.isInstanceOf(LazyInitializationException.class);
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void testPersonRepositoryFindFetchWithAllByName() {
	Person personDatabase = personRepository.findFetchWithAllByName(person.getName());

	assertNotNull(personDatabase);

	assertNotNull(personDatabase.getContactAccounts());
	assertThat(personDatabase.getContactAccounts().size()).isEqualTo(NB_PERSON_CONTACT_ACCOUNT);

	Set<Account> accounts = personDatabase.getAccounts();
	assertNotNull(accounts);
	assertThat(accounts.size()).isEqualTo(NB_PERSON_ACCOUNT);
	Account account = accounts.iterator().next();
	assertNotNull(account);

	Set<Transaction> transactions = account.getTransactionsFrom();
	assertNotNull(transactions);
	assertThat(transactions.size()).isEqualTo(NB_TRANSACTION);
    }

    @Test
    void testAccountRepositorySaveDeleteFindAll() {
	int nbAccounts = accountRepository.findAll().size();
	Account account = new Account(testPerson, testBank, 0);
	account = accountRepository.save(account);
	assertThat(accountRepository.findAll().size()).isEqualTo(nbAccounts + 1);
	accountRepository.delete(account);
	assertThat(accountRepository.findAll().size()).isEqualTo(nbAccounts);
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void testAccountRepositoryFindByPersonNameAndBankName() {
	Account accountDatabase = accountRepository.findByPersonNameAndBankName(CONTACT_USER, PAY_MY_BUDDY_BANK);

	assertNotNull(accountDatabase);

	assertThatThrownBy(() -> accountDatabase.getTransactionsFrom().size())
		.isInstanceOf(LazyInitializationException.class);
	assertThatThrownBy(() -> accountDatabase.getTransactionsTo().size())
		.isInstanceOf(LazyInitializationException.class);
	assertThatThrownBy(() -> accountDatabase.getContactPersons().size())
		.isInstanceOf(LazyInitializationException.class);
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void testAccountRepositoryFindFetchWithContactPersonsByPersonNameAndBankName() {
	Account accountDatabase = accountRepository.findFetchWithContactPersonsByPersonNameAndBankName(CONTACT_USER,
		PAY_MY_BUDDY_BANK);

	assertNotNull(accountDatabase);

	assertNotNull(accountDatabase.getContactPersons());
	assertThat(accountDatabase.getContactPersons());
	assertThat(accountDatabase.getContactPersons().size()).isEqualTo(NB_CONTACT_PERSON);

	assertThatThrownBy(() -> accountDatabase.getTransactionsFrom().size())
		.isInstanceOf(LazyInitializationException.class);
	assertThatThrownBy(() -> accountDatabase.getTransactionsTo().size())
		.isInstanceOf(LazyInitializationException.class);
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void testAccountRepositoryFindAllExceptPerson() {
	List<Account> accounts = accountRepository.findAllExceptPerson(person.getId());

	assertNotNull(accounts);
	assertThat(accounts.size()).isEqualTo(NB_ACCOUNT_WITHOUT_PERSON);

	for (Account account : accounts) {
	    assertNotNull(account);

	    assertThatThrownBy(() -> account.getTransactionsFrom().size())
		    .isInstanceOf(LazyInitializationException.class);
	    assertThatThrownBy(() -> account.getTransactionsTo().size())
		    .isInstanceOf(LazyInitializationException.class);
	    assertThatThrownBy(() -> account.getContactPersons().size())
		    .isInstanceOf(LazyInitializationException.class);
	}

    }

    @Test
    void testBankRepositorySaveDeleteFindAll() {
	int nbBanks = bankRepository.findAll().size();
	Bank bank = new Bank("bankName");
	bank = bankRepository.save(bank);
	assertThat(bankRepository.findAll().size()).isEqualTo(nbBanks + 1);
	bankRepository.delete(bank);
	assertThat(bankRepository.findAll().size()).isEqualTo(nbBanks);
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void testBankRepositoryFindByName() {
	Bank bankDatabase = bankRepository.findByName(bank.getName());

	assertNotNull(bankDatabase);

	assertThatThrownBy(() -> bankDatabase.getAccounts().size())
		.isInstanceOf(LazyInitializationException.class);
    }

    @Test
    @Transactional(propagation = Propagation.NEVER)
    void testBankRepositoryFindFetchWithAccountsByName() {
	Bank bankDatabase = bankRepository.findFetchWithAccountsByName(bank.getName());

	assertNotNull(bankDatabase);

	assertNotNull(bankDatabase.getAccounts());
	assertThat(bankDatabase.getAccounts().size()).isEqualTo(NB_BANK_ACCOUNT);

    }

    @Test
    void testTransactionRepositorySaveDeleteFindAll() {
	int nbTransactions = transactionRepository.findAll().size();
	Transaction transaction = new Transaction(null, account, contactAccount, 1000.0, "virement",
		LocalDateTime.now(), TransactionType.BUDDY_TO_BUDDY);
	transaction = transactionRepository.save(transaction);
	assertThat(transactionRepository.findAll().size()).isEqualTo(nbTransactions + 1);
	transactionRepository.delete(transaction);
	assertThat(transactionRepository.findAll().size()).isEqualTo(nbTransactions);
    }

    @Test
    void testRoleRepositorySaveDeleteFindAll() {
	int nbRoles = roleRepository.findAll().size();
	Role role = new Role(null, "personName", "AUTHORITY_USER", person);
	role = roleRepository.save(role);
	assertThat(roleRepository.findAll().size()).isEqualTo(nbRoles + 1);
	roleRepository.delete(role);
	assertThat(roleRepository.findAll().size()).isEqualTo(nbRoles);
    }
}
