package com.paymybuddy.finance;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.paymybuddy.finance.dto.ContactDTO;
import com.paymybuddy.finance.model.Account;
import com.paymybuddy.finance.model.Bank;
import com.paymybuddy.finance.model.Person;
import com.paymybuddy.finance.repository.AccountRepository;
import com.paymybuddy.finance.repository.BankRepository;
import com.paymybuddy.finance.repository.PersonRepository;
import com.paymybuddy.finance.repository.TransactionRepository;
import com.paymybuddy.finance.service.AccountService;
import com.paymybuddy.finance.service.BankService;
import com.paymybuddy.finance.service.IAccountService;
import com.paymybuddy.finance.service.IBankService;
import com.paymybuddy.finance.service.IFinanceService;
import com.paymybuddy.finance.service.IPersonService;
import com.paymybuddy.finance.service.ITransactionService;
import com.paymybuddy.finance.service.PersonService;
import com.paymybuddy.finance.service.TransactionService;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@TestMethodOrder(value = org.junit.jupiter.api.MethodOrderer.OrderAnnotation.class)
public class ServiceTest {

    private static final String SECURE_USER = "user@user.com";

    private IPersonService personService;
    private IBankService bankService;
    private IAccountService accountService;
    private ITransactionService transactionService;
    private IFinanceService financeService;

    @Mock
    private PersonRepository personRepository;
    @Mock
    private BankRepository bankRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;

    private Person person;
    private Account account;
    private Bank bank;
    private ContactDTO contactDTO;

    @BeforeAll
    public void beforeAll() {
	person = new Person(SECURE_USER, SECURE_USER);
	person.setId(1L);
	account = new Account();
	bank = new Bank();
	contactDTO = new ContactDTO(null, 1L, 2L);
    }

    @BeforeEach
    public void beforeEach() {

	personService = new PersonService(personRepository);
	bankService = new BankService(bankRepository);
	accountService = new AccountService(accountRepository, personRepository);
	transactionService = new TransactionService(transactionRepository);
    }

    @Test
    public void testPersonServiceSavePerson() {

	when(personRepository.save(any(Person.class))).thenReturn(person);
	personService.savePerson(person);
	verify(personRepository, times(1)).save(any(Person.class));
    }

    @Test
    public void testPersonServiceCreatePerson() {

	when(personRepository.save(any(Person.class))).thenReturn(person);
	personService.createPerson("person", "email");
	verify(personRepository, times(1)).save(any(Person.class));
    }

    @Test
    public void testPersonServiceFindPersonByName() {

	when(personRepository.findByName(any(String.class))).thenReturn(person);
	personService.findPersonByName("person");
	verify(personRepository, times(1)).findByName(any(String.class));
    }

    @Test
    public void testPersonServiceFindFetchWithContactAccountsPersonByName() {

	when(personRepository.findFetchWithContactAccountsByName(any(String.class))).thenReturn(person);
	personService.findFetchWithContactAccountsPersonByName("person");
	verify(personRepository, times(1)).findFetchWithContactAccountsByName(any(String.class));
    }

    @Test
    public void testPersonServiceFindFetchWithAccountsPersonByName() {

	when(personRepository.findFetchWithAccountsByName(any(String.class))).thenReturn(person);
	personService.findFetchWithAccountsPersonByName("person");
	verify(personRepository, times(1)).findFetchWithAccountsByName(any(String.class));
    }

    @Test
    public void testPersonServiceFindFetchWithAccountsTransactionsPersonByName() {

	when(personRepository.findFetchWithAccountsAndTransactionsByName(any(String.class))).thenReturn(person);
	personService.findFetchWithAccountsTransactionsPersonByName("person");
	verify(personRepository, times(1)).findFetchWithAccountsAndTransactionsByName(any(String.class));
    }

    @Test
    public void testPersonServiceFindFetchWithAllPersonByName() {

	when(personRepository.findFetchWithAllByName(any(String.class))).thenReturn(person);
	personService.findFetchWithAllPersonByName("person");
	verify(personRepository, times(1)).findFetchWithAllByName(any(String.class));
    }

    @Test
    public void testPersonServiceFindAllPersons() {

	personService.findAllPersons();
	verify(personRepository, times(1)).findAll();
    }

    @Test
    public void testPersonServiceDeleteAllPersons() {

	personService.deleteAllPersons();
	verify(personRepository, times(1)).deleteAll();
    }

    @Test
    public void testAccountServiceSaveAccount() {

	when(accountRepository.save(any(Account.class))).thenReturn(account);
	accountService.saveAccount(account);
	verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    public void testAccountServiceCreateAccount() {

	when(accountRepository.save(any(Account.class))).thenReturn(account);
	accountService.createAccount(1000.0, person, bank);
	verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    public void testAccountServiceCreateContactAccount() {

	when(accountRepository.findById(any(Long.class))).thenReturn(Optional.of(account));
	accountService.createContactAccount(person, contactDTO);
	verify(accountRepository, times(1)).findById(any(Long.class));
    }

    @Test
    public void testAccountServiceCreateContactAccount2() {

	when(accountRepository.save(any(Account.class))).thenReturn(account);
	accountService.createContactAccount(person, account);
	verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    public void testAccountServiceRemoveContactAccount() {

	when(accountRepository.findById(any(Long.class))).thenReturn(Optional.of(account));
	accountService.removeContactAccount(person, contactDTO);
	verify(accountRepository, times(1)).findById(any(Long.class));
    }

    @Test
    public void testAccountServiceRemoveContactAccount2() {

	when(accountRepository.save(any(Account.class))).thenReturn(account);
	when(personRepository.save(any(Person.class))).thenReturn(person);
	accountService.removeContactAccount(person, account);
	verify(accountRepository, times(1)).save(any(Account.class));
	verify(personRepository, times(1)).save(any(Person.class));
    }

    @Test
    public void testAccountServiceFindAccountByPersonNameAndBankName() {

	when(accountRepository.findByPersonNameAndBankName(any(String.class), any(String.class))).thenReturn(account);
	accountService.findAccountByPersonNameAndBankName("personName", "bankName");
	verify(accountRepository, times(1)).findByPersonNameAndBankName(any(String.class), any(String.class));
    }

    @Test
    public void testAccountServiceFindFetchTransactionsAccountByPersonNameAndBankName() {

	when(accountRepository.findFetchWithTransactionsByPersonNameAndBankName(any(String.class), any(String.class)))
		.thenReturn(account);
	accountService.findFetchTransactionsAccountByPersonNameAndBankName("personName", "bankName");
	verify(accountRepository, times(1)).findFetchWithTransactionsByPersonNameAndBankName(any(String.class),
		any(String.class));
    }

    @Test
    public void testAccountServiceFindFetchWithContactPersonsAccountByPersonNameAndBankName() {

	when(accountRepository.findFetchWithContactPersonsByPersonNameAndBankName(any(String.class), any(String.class)))
		.thenReturn(account);
	accountService.findFetchWithContactPersonsAccountByPersonNameAndBankName("personName", "bankName");
	verify(accountRepository, times(1)).findFetchWithContactPersonsByPersonNameAndBankName(any(String.class),
		any(String.class));
    }

    @Test
    public void testAccountServiceFindAllAccounts() {

	accountService.findAllAccounts();
	verify(accountRepository, times(1)).findAll();
    }

    @Test
    public void testAccountServiceFindAccountById() {

	when(accountRepository.findById(any(Long.class))).thenReturn(Optional.of(account));
	accountService.findAccountById(1L);
	verify(accountRepository, times(1)).findById(any(Long.class));
    }

    @Test
    public void testAccountServiceFindAllAccountsExceptPersonAccounts() {

	when(accountRepository.findAllExceptPerson(any(Long.class))).thenReturn(new ArrayList<Account>());
	accountService.findAllAccountsExceptPersonAccounts(person);
	verify(accountRepository, times(1)).findAllExceptPerson(any(Long.class));
    }

    @Test
    public void testAccountServiceFindFetchTransactionsAccountById() {

	when(accountRepository.findFetchWithTransactionsById(any(Long.class))).thenReturn(account);
	accountService.findFetchTransactionsAccountById(1L);
	verify(accountRepository, times(1)).findFetchWithTransactionsById(any(Long.class));
    }

    @Test
    public void testAccountServiceDeleteAllAccounts() {

	accountService.deleteAllAccounts();
	verify(accountRepository, times(1)).deleteAll();
    }

    public void deleteAllAccounts() {
	accountRepository.deleteAll();
	accountRepository.flush();
    }

}
