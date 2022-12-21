package com.paymybuddy.finance;

import static com.paymybuddy.finance.constants.Constants.AUTHORITY_USER;
import static com.paymybuddy.finance.constants.Constants.PAY_MY_BUDDY_BANK;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.context.ActiveProfiles;

import com.paymybuddy.finance.dto.ContactDTO;
import com.paymybuddy.finance.dto.TransferDTO;
import com.paymybuddy.finance.dto.UserLoginDTO;
import com.paymybuddy.finance.model.Account;
import com.paymybuddy.finance.model.Bank;
import com.paymybuddy.finance.model.Person;
import com.paymybuddy.finance.model.Transaction;
import com.paymybuddy.finance.repository.AccountRepository;
import com.paymybuddy.finance.repository.BankRepository;
import com.paymybuddy.finance.repository.PersonRepository;
import com.paymybuddy.finance.repository.RoleRepository;
import com.paymybuddy.finance.repository.TransactionRepository;
import com.paymybuddy.finance.security.PayMyBuddyUserDetails;
import com.paymybuddy.finance.service.AccountService;
import com.paymybuddy.finance.service.BankService;
import com.paymybuddy.finance.service.FinanceService;
import com.paymybuddy.finance.service.IAccountService;
import com.paymybuddy.finance.service.IBankService;
import com.paymybuddy.finance.service.IFinanceService;
import com.paymybuddy.finance.service.IPersonService;
import com.paymybuddy.finance.service.IRoleService;
import com.paymybuddy.finance.service.ITransactionService;
import com.paymybuddy.finance.service.PersonService;
import com.paymybuddy.finance.service.RoleService;
import com.paymybuddy.finance.service.TransactionService;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@TestMethodOrder(value = org.junit.jupiter.api.MethodOrderer.OrderAnnotation.class)
public class ServiceTest {

    private static final String SECURE_USER = "user@user.com";
    private static final String PASSWORD = "password";

    private IPersonService personService;
    private IBankService bankService;
    private IAccountService accountService;
    private ITransactionService transactionService;
    private IFinanceService financeService;
    private IRoleService roleService;

    @Mock
    private PersonRepository personRepository;
    @Mock
    private BankRepository bankRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private RoleRepository roleRepository;

    // Mock For financeService test
    @Mock
    UserDetailsManager userDetailsManager;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    RoleService mockRoleService;
    @Mock
    PersonService mockPersonService;
    @Mock
    BankService mockBankService;
    @Mock
    AccountService mockAccountService;
    @Mock
    TransactionService mockTransactionService;

    private Person person;
    private Account account;
    private Account accountFrom;
    private Account accountTo;
    private Bank bank;
    private Transaction transaction;
    private ContactDTO contactDTO;
    private TransferDTO transferDTO;
    private PayMyBuddyUserDetails userDetails;

    @BeforeAll
    public void beforeAll() {
	person = new Person(SECURE_USER, SECURE_USER);
	person.setId(1L);
	account = new Account();
	accountFrom = new Account();
	accountTo = new Account();
	bank = new Bank(PAY_MY_BUDDY_BANK);
	accountFrom.setBank(bank);
	accountTo.setBank(bank);

	transaction = new Transaction(1L, account, account, 1000.0, "virement", LocalDateTime.now(),
		Transaction.TransactionType.BUDDY_TO_BUDDY);

	contactDTO = new ContactDTO(null, 1L, 2L);
	transferDTO = new TransferDTO(1L, 2L);

	UserLoginDTO userLogin = new UserLoginDTO(SECURE_USER, PASSWORD);
	userDetails = new PayMyBuddyUserDetails(userLogin);
	userDetails.addAuthority(new SimpleGrantedAuthority(AUTHORITY_USER));

    }

    @BeforeEach
    public void beforeEach() {

	personService = new PersonService(personRepository);
	bankService = new BankService(bankRepository);
	accountService = new AccountService(accountRepository, personRepository);
	transactionService = new TransactionService(transactionRepository);
	roleService = new RoleService(roleRepository);

	financeService = new FinanceService(userDetailsManager, passwordEncoder,
		mockRoleService, mockPersonService, mockBankService,
		mockAccountService, mockTransactionService);
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

    @Test
    public void testBankServiceCreateBank() {
	when(bankRepository.save(any(Bank.class))).thenReturn(bank);
	bankService.createBank("bankName");
	verify(bankRepository, times(1)).save(any(Bank.class));
    }

    @Test
    public void testBankServiceFindBankByName() {
	when(bankRepository.findByName(any(String.class))).thenReturn(bank);
	bankService.findBankByName("bankName");
	verify(bankRepository, times(1)).findByName(any(String.class));
    }

    @Test
    public void testBankServiceFindWithAccountsBankByName() {
	when(bankRepository.findFetchWithAccountsByName(any(String.class))).thenReturn(bank);
	bankService.findWithAccountsBankByName("bankName");
	verify(bankRepository, times(1)).findFetchWithAccountsByName(any(String.class));
    }

    @Test
    public void testBankServiceFindAllBanks() {
	when(bankRepository.findAll()).thenReturn(new ArrayList<Bank>());
	bankService.findAllBanks();
	verify(bankRepository, times(1)).findAll();
    }

    @Test
    public void testBankServiceDeleteAllBanks() {
	bankService.deleteAllBanks();
	verify(bankRepository, times(1)).deleteAll();
    }

    @Test
    public void testRoleServiceDeleteAllRoles() {
	roleService.deleteAllRoles();
	verify(roleRepository, times(1)).deleteAll();
    }

    @Test
    public void testTransactionServiceSaveTransaction() {
	when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
	transactionService.saveTransaction(transaction);
	verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    public void testTransactionServiceFindAllTransactions() {
	transactionService.findAllTransactions();
	verify(transactionRepository, times(1)).findAll();
    }

    @Test
    public void testTransactionServiceDeleteAllTransactions() {
	transactionService.deleteAllTransactions();
	verify(transactionRepository, times(1)).deleteAll();
    }

    @Test
    public void testFinanceServiceDeleteAll() {
	financeService.deleteAll();
	verify(mockTransactionService, times(1)).deleteAllTransactions();
	verify(mockAccountService, times(1)).deleteAllAccounts();
	verify(mockRoleService, times(1)).deleteAllRoles();
	verify(mockPersonService, times(1)).deleteAllPersons();
	verify(mockBankService, times(1)).deleteAllBanks();
    }

    @Test
    public void testFinanceServiceInitApplication() {
	when(mockAccountService.createAccount(any(Double.class), any(), any()))
		.thenReturn(account);
	financeService.initApplication();
	verify(mockBankService, times(2)).findBankByName(any(String.class));
	verify(mockBankService, times(2)).createBank(any(String.class));
	verify(mockPersonService, times(1)).findPersonByName(any(String.class));
	verify(userDetailsManager, times(1)).createUser(any(PayMyBuddyUserDetails.class));
	verify(mockAccountService, times(1)).createAccount(any(Double.class), any(), any());
    }

    @Test
    public void testFinanceServiceCreateAtomicTransaction() {
	financeService.createTransaction(accountFrom, accountTo, 1000.0, "virement",
		Transaction.TransactionType.BUDDY_TO_BUDDY);
	verify(mockAccountService, times(2)).saveAccount(any(Account.class));
	verify(mockTransactionService, times(1)).saveTransaction(any(Transaction.class));
    }

    @Test
    public void testFinanceServiceCreateTransactions() {
	when(mockAccountService.findAccountByPersonNameAndBankName(any(String.class), any(String.class)))
		.thenReturn(accountFrom);
	financeService.createTransactions(accountFrom, accountTo, 1000.0, "virement");
	verify(mockAccountService, times(1)).findAccountByPersonNameAndBankName(any(String.class), any(String.class));
    }

    @Test
    public void testFinanceServiceCreateTransactionDTO() {
	when(mockAccountService.findAccountByPersonNameAndBankName(any(String.class), any(String.class)))
		.thenReturn(accountFrom);
	when(mockAccountService.findFetchTransactionsAccountById(any(Long.class)))
		.thenReturn(accountFrom);
	financeService.createTransaction(person, transferDTO);
	verify(mockAccountService, times(2)).findFetchTransactionsAccountById(any(Long.class));
    }

    @Test
    public void testFinanceServiceValidateCreateTransaction() {
	when(mockAccountService.findAccountById(any(Long.class)))
		.thenReturn(accountFrom);
	financeService.validateCreateTransaction(person, transferDTO);
	verify(mockAccountService, times(2)).findAccountById(any(Long.class));
    }

    @Test
    public void testFinanceServiceInitPerson() {
	when(mockPersonService.findFetchWithAllPersonByName(any(String.class)))
		.thenReturn(person);
	financeService.initPerson(person);
	verify(mockBankService, times(2)).findWithAccountsBankByName(any(String.class));
	verify(mockAccountService, times(2)).createAccount(any(double.class), any(), any());
	verify(mockAccountService, times(2)).findAccountByPersonNameAndBankName(any(), any());
    }

    @Test
    public void testFinanceServiceCreateSecurePersonFromUserDetails() {
	financeService.createSecurePerson(userDetails);
	verify(userDetailsManager, times(1)).createUser(any(UserDetails.class));
    }

}
