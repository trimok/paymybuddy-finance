package com.paymybuddy.finance;

import static com.paymybuddy.finance.constants.Constants.AUTHORITY_USER;
import static com.paymybuddy.finance.constants.Constants.PAY_MY_BUDDY_BANK;
import static com.paymybuddy.finance.constants.Constants.USER_GENERIC_BANK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
    private static final String TEST_BANK = "test_bank";

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

    private Person personSaved;
    private Person personNotSaved;
    private Account accountSaved;
    private Account accountNotSaved;
    private Account accountFrom;
    private Account accountTo;
    private Bank bankSaved;
    private Transaction transactionSaved;
    private Transaction transactionNotSaved;
    private Transaction transactionBuddyToBank;
    private ContactDTO contactDTO;
    private TransferDTO transferDTO;
    private PayMyBuddyUserDetails userDetails;

    @BeforeAll
    public void beforeAll() {
	personNotSaved = new Person(SECURE_USER, SECURE_USER);
	personSaved = new Person(SECURE_USER, SECURE_USER);
	personSaved.setId(1L);

	bankSaved = new Bank(1L, PAY_MY_BUDDY_BANK);
	Bank bankGeneridSaved = new Bank(1L, USER_GENERIC_BANK);
	accountSaved = new Account(personSaved, bankSaved, 0);
	accountSaved.setId(1L);
	personSaved.addAccount(accountSaved);
	bankSaved.addAccount(accountSaved);

	accountNotSaved = new Account(personSaved, bankSaved, 0);

	accountFrom = new Account(1L);
	accountTo = new Account(2L);
	personSaved.addContactAccount(accountSaved);

	Account accountGeneric = new Account(3L);
	personSaved.addAccount(accountGeneric);
	bankGeneridSaved.addAccount(accountGeneric);
	transactionBuddyToBank = new Transaction(null, accountSaved, accountGeneric, 1000.0, "virement",
		LocalDateTime.now(),
		Transaction.TransactionType.BUDDY_TO_BANK);

	accountFrom.setBank(bankSaved);
	accountTo.setBank(bankSaved);

	transactionSaved = new Transaction(1L, accountSaved, accountSaved, 1000.0, "virement", LocalDateTime.now(),
		Transaction.TransactionType.BUDDY_TO_BUDDY);

	transactionNotSaved = new Transaction(null, accountSaved, accountSaved, 1000.0, "virement", LocalDateTime.now(),
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

	when(personRepository.save(any(Person.class))).thenReturn(personSaved);
	Person personDatabase = personService.savePerson(personNotSaved);
	verify(personRepository, times(1)).save(any(Person.class));
	assertThat(personDatabase).isEqualTo(personSaved);
    }

    @Test
    public void testPersonServiceCreatePerson() {

	when(personRepository.save(any(Person.class))).thenReturn(personSaved);
	Person personDatabase = personService.createPerson(personNotSaved.getName(), personNotSaved.getEmail());
	verify(personRepository, times(1)).save(any(Person.class));
	assertThat(personDatabase).isEqualTo(personSaved);
    }

    @Test
    public void testPersonServiceFindPersonByName() {

	when(personRepository.findByName(any(String.class))).thenReturn(personSaved);
	Person personDatabase = personService.findPersonByName(personNotSaved.getName());
	verify(personRepository, times(1)).findByName(any(String.class));
	assertThat(personDatabase).isEqualTo(personSaved);
    }

    @Test
    public void testPersonServiceFindFetchWithContactAccountsPersonByName() {

	when(personRepository.findFetchWithContactAccountsByName(any(String.class))).thenReturn(personSaved);
	Person personDatabase = personService.findFetchWithContactAccountsPersonByName(personNotSaved.getName());
	verify(personRepository, times(1)).findFetchWithContactAccountsByName(any(String.class));
	assertThat(personDatabase).isEqualTo(personSaved);
    }

    @Test
    public void testPersonServiceFindFetchWithAccountsPersonByName() {

	when(personRepository.findFetchWithAccountsByName(any(String.class))).thenReturn(personSaved);
	Person personDatabase = personService.findFetchWithAccountsPersonByName(personNotSaved.getName());
	verify(personRepository, times(1)).findFetchWithAccountsByName(any(String.class));
	assertThat(personDatabase).isEqualTo(personSaved);
    }

    @Test
    public void testPersonServiceFindFetchWithAccountsTransactionsPersonByName() {

	when(personRepository.findFetchWithAccountsAndTransactionsByName(any(String.class))).thenReturn(personSaved);
	Person personDatabase = personService.findFetchWithAccountsTransactionsPersonByName(personNotSaved.getName());
	verify(personRepository, times(1)).findFetchWithAccountsAndTransactionsByName(any(String.class));
	assertThat(personDatabase).isEqualTo(personSaved);
    }

    @Test
    public void testPersonServiceFindFetchWithAllPersonByName() {

	when(personRepository.findFetchWithAllByName(any(String.class))).thenReturn(personSaved);
	Person personDatabase = personService.findFetchWithAllPersonByName(personNotSaved.getName());
	verify(personRepository, times(1)).findFetchWithAllByName(any(String.class));
	assertThat(personDatabase).isEqualTo(personSaved);
    }

    @Test
    public void testPersonServiceFindAllPersons() {
	when(personRepository.findAll()).thenReturn(Arrays.asList(personSaved));
	List<Person> persons = personService.findAllPersons();
	verify(personRepository, times(1)).findAll();
	assertThat(persons).isEqualTo(Arrays.asList(personSaved));
    }

    @Test
    public void testPersonServiceDeleteAllPersons() {
	personService.deleteAllPersons();
	verify(personRepository, times(1)).deleteAll();
    }

    @Test
    public void testAccountServiceSaveAccount() {
	when(accountRepository.save(any(Account.class))).thenReturn(accountSaved);
	Account accountDatabase = accountService.saveAccount(accountNotSaved);
	verify(accountRepository, times(1)).save(any(Account.class));
	assertThat(accountDatabase).isEqualTo(accountSaved);
    }

    @Test
    public void testAccountServiceCreateAccount() {

	when(accountRepository.save(any(Account.class))).thenReturn(accountSaved);
	Account accountDatabase = accountService.createAccount(1000.0, personSaved, bankSaved);
	verify(accountRepository, times(1)).save(any(Account.class));
	assertThat(accountDatabase).isEqualTo(accountSaved);
    }

    @Test
    public void testAccountServiceCreateContactAccount() {

	when(accountRepository.findById(any(Long.class))).thenReturn(Optional.of(accountSaved));
	when(accountRepository.save(any(Account.class))).thenReturn(accountSaved);
	Account accountDatabase = accountService.createContactAccount(personSaved, contactDTO);
	verify(accountRepository, times(1)).findById(any(Long.class));
	verify(accountRepository, times(1)).save(any(Account.class));
	assertThat(accountDatabase).isEqualTo(accountSaved);
    }

    @Test
    public void testAccountServiceCreateContactAccount2() {

	when(accountRepository.save(any(Account.class))).thenReturn(accountSaved);
	Account accountDatabase = accountService.createContactAccount(personSaved, accountSaved);
	verify(accountRepository, times(1)).save(any(Account.class));
	assertThat(accountDatabase).isEqualTo(accountSaved);
    }

    @Test
    public void testAccountServiceRemoveContactAccount() {

	when(accountRepository.findById(any(Long.class))).thenReturn(Optional.of(accountSaved));
	when(accountRepository.save(any(Account.class))).thenReturn(accountSaved);
	when(personRepository.save(any(Person.class))).thenReturn(personSaved);
	accountService.removeContactAccount(personSaved, contactDTO);
	verify(accountRepository, times(1)).findById(any(Long.class));
	verify(accountRepository, times(1)).save(any(Account.class));
	verify(personRepository, times(1)).save(any(Person.class));
    }

    @Test
    public void testAccountServiceRemoveContactAccount2() {

	when(accountRepository.save(any(Account.class))).thenReturn(accountSaved);
	when(personRepository.save(any(Person.class))).thenReturn(personSaved);
	accountService.removeContactAccount(personSaved, accountSaved);
	verify(accountRepository, times(1)).save(any(Account.class));
	verify(personRepository, times(1)).save(any(Person.class));
    }

    @Test
    public void testAccountServiceFindAccountByPersonNameAndBankName() {

	when(accountRepository.findByPersonNameAndBankName(any(String.class), any(String.class)))
		.thenReturn(accountSaved);
	Account accountDatabase = accountService.findAccountByPersonNameAndBankName(accountSaved.getPerson().getName(),
		accountSaved.getBank().getName());
	verify(accountRepository, times(1)).findByPersonNameAndBankName(any(String.class), any(String.class));
	assertThat(accountDatabase).isEqualTo(accountSaved);
    }

    @Test
    public void testAccountServiceFindFetchTransactionsAccountByPersonNameAndBankName() {

	when(accountRepository.findFetchWithTransactionsByPersonNameAndBankName(any(String.class), any(String.class)))
		.thenReturn(accountSaved);
	Account accountDatabase = accountService.findFetchTransactionsAccountByPersonNameAndBankName(
		accountSaved.getPerson().getName(),
		accountSaved.getBank().getName());
	verify(accountRepository, times(1)).findFetchWithTransactionsByPersonNameAndBankName(any(String.class),
		any(String.class));
	assertThat(accountDatabase).isEqualTo(accountSaved);
    }

    @Test
    public void testAccountServiceFindFetchWithContactPersonsAccountByPersonNameAndBankName() {

	when(accountRepository.findFetchWithContactPersonsByPersonNameAndBankName(any(String.class), any(String.class)))
		.thenReturn(accountSaved);
	Account accountDatabase = accountService.findFetchWithContactPersonsAccountByPersonNameAndBankName(
		accountSaved.getPerson().getName(),
		accountSaved.getBank().getName());
	verify(accountRepository, times(1)).findFetchWithContactPersonsByPersonNameAndBankName(any(String.class),
		any(String.class));
	assertThat(accountDatabase).isEqualTo(accountSaved);
    }

    @Test
    public void testAccountServiceFindAllAccounts() {
	when(accountRepository.findAll()).thenReturn(Arrays.asList(accountSaved));
	List<Account> accounts = accountService.findAllAccounts();
	verify(accountRepository, times(1)).findAll();
	assertThat(accounts).isEqualTo(Arrays.asList(accountSaved));
    }

    @Test
    public void testAccountServiceFindAccountById() {

	when(accountRepository.findById(any(Long.class))).thenReturn(Optional.of(accountSaved));
	Account accountDatabase = accountService.findAccountById(1L);
	verify(accountRepository, times(1)).findById(any(Long.class));
	assertThat(accountDatabase).isEqualTo(accountSaved);
    }

    @Test
    public void testAccountServiceFindAllAccountsExceptPersonAccounts() {

	when(accountRepository.findAllExceptPerson(any(Long.class))).thenReturn(Arrays.asList(accountSaved));
	List<Account> accounts = accountService.findAllAccountsExceptPersonAccounts(personSaved);
	verify(accountRepository, times(1)).findAllExceptPerson(any(Long.class));
	assertThat(accounts).isEqualTo(Arrays.asList(accountSaved));
    }

    @Test
    public void testAccountServiceFindFetchTransactionsAccountById() {

	when(accountRepository.findFetchWithTransactionsById(any(Long.class))).thenReturn(accountSaved);
	Account accountDatabase = accountService.findFetchTransactionsAccountById(1L);
	verify(accountRepository, times(1)).findFetchWithTransactionsById(any(Long.class));
	assertThat(accountDatabase).isEqualTo(accountSaved);
    }

    @Test
    public void testAccountServiceDeleteAllAccounts() {
	accountService.deleteAllAccounts();
	verify(accountRepository, times(1)).deleteAll();
    }

    @Test
    public void testBankServiceCreateBank() {
	when(bankRepository.save(any(Bank.class))).thenReturn(bankSaved);
	Bank bankDatabase = bankService.createBank(bankSaved.getName());
	verify(bankRepository, times(1)).save(any(Bank.class));
	assertThat(bankDatabase).isEqualTo(bankSaved);
    }

    @Test
    public void testBankServiceFindBankByName() {
	when(bankRepository.findByName(any(String.class))).thenReturn(bankSaved);
	Bank bankDatabase = bankService.findBankByName(bankSaved.getName());
	verify(bankRepository, times(1)).findByName(any(String.class));
	assertThat(bankDatabase).isEqualTo(bankSaved);
    }

    @Test
    public void testBankServiceFindWithAccountsBankByName() {
	when(bankRepository.findFetchWithAccountsByName(any(String.class))).thenReturn(bankSaved);
	Bank bankDatabase = bankService.findWithAccountsBankByName(bankSaved.getName());
	verify(bankRepository, times(1)).findFetchWithAccountsByName(any(String.class));
	assertThat(bankDatabase).isEqualTo(bankSaved);
    }

    @Test
    public void testBankServiceFindAllBanks() {
	when(bankRepository.findAll()).thenReturn(Arrays.asList(bankSaved));
	List<Bank> banks = bankService.findAllBanks();
	verify(bankRepository, times(1)).findAll();
	assertThat(banks).isEqualTo(Arrays.asList(bankSaved));
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
	when(transactionRepository.save(any(Transaction.class))).thenReturn(transactionSaved);
	Transaction transactionDatabase = transactionService.saveTransaction(transactionNotSaved);
	verify(transactionRepository, times(1)).save(any(Transaction.class));
	assertThat(transactionDatabase).isEqualTo(transactionSaved);
    }

    @Test
    public void testTransactionServiceFindAllTransactions() {
	when(transactionRepository.findAll()).thenReturn(Arrays.asList(transactionSaved));
	List<Transaction> transactions = transactionService.findAllTransactions();
	verify(transactionRepository, times(1)).findAll();
	assertThat(transactions).isEqualTo(Arrays.asList(transactionSaved));
    }

    @Test
    public void testTransactionServiceDeleteAllTransactions() {
	transactionService.deleteAllTransactions();
	verify(transactionRepository, times(1)).deleteAll();
    }

    @Test
    public void testFinanceServiceDeleteAll() {
	when(mockPersonService.findAllPersons()).thenReturn(Arrays.asList(personSaved));
	doAnswer(i -> {
	    ((Person) i.getArgument(0)).setContactAccounts(new HashSet<Account>());
	    return null;
	}).when(mockAccountService).removeContactAccount(any(Person.class), any(Account.class));

	financeService.deleteAll();
	verify(mockTransactionService, times(1)).deleteAllTransactions();
	verify(mockAccountService, times(1)).removeContactAccount(any(Person.class), any(Account.class));
	verify(mockAccountService, times(1)).deleteAllAccounts();
	verify(mockRoleService, times(1)).deleteAllRoles();
	verify(mockPersonService, times(1)).deleteAllPersons();
	verify(mockPersonService, times(1)).findAllPersons();
	verify(mockBankService, times(1)).deleteAllBanks();
    }

    @Test
    public void testFinanceServiceInitApplication() {
	when(mockAccountService.createAccount(any(Double.class), any(), any()))
		.thenReturn(accountSaved);
	financeService.initApplication();
	verify(mockBankService, times(2)).findBankByName(any(String.class));
	verify(mockBankService, times(2)).createBank(any(String.class));
	verify(mockPersonService, times(1)).findPersonByName(any(String.class));
	verify(userDetailsManager, times(1)).createUser(any(PayMyBuddyUserDetails.class));
	verify(mockAccountService, times(1)).createAccount(any(Double.class), any(), any());
    }

    @Test
    public void testFinanceServiceCreateAtomicTransaction() {
	when(mockTransactionService.saveTransaction(any(Transaction.class))).thenReturn(transactionSaved);
	Transaction transactionDatabase = financeService.createTransaction(transactionNotSaved.getAccountFrom(),
		transactionNotSaved.getAccountTo(), transactionNotSaved.getAmount(),
		transactionNotSaved.getDescription(),
		transactionNotSaved.getTransactionType());
	verify(mockAccountService, times(2)).saveAccount(any(Account.class));
	verify(mockTransactionService, times(1)).saveTransaction(any(Transaction.class));
	assertThat(transactionDatabase.equals(transactionSaved));
    }

    @Test
    public void testFinanceServiceCreateTransactions() {
	when(mockTransactionService.saveTransaction(any(Transaction.class))).thenReturn(transactionSaved);
	when(mockAccountService.findAccountByPersonNameAndBankName(any(String.class), any(String.class)))
		.thenReturn(accountFrom);
	List<Transaction> transactionsDatabase = financeService.createTransactions(transactionNotSaved.getAccountFrom(),
		transactionNotSaved.getAccountTo(), transactionNotSaved.getAmount(),
		transactionNotSaved.getDescription());
	verify(mockAccountService, times(4)).saveAccount(any(Account.class));
	verify(mockTransactionService, times(2)).saveTransaction(any(Transaction.class));
	verify(mockAccountService, times(1)).findAccountByPersonNameAndBankName(any(String.class), any(String.class));
	assertThat(transactionsDatabase.size()).isEqualTo(2);
	assertThat(transactionsDatabase).isEqualTo(Arrays.asList(transactionSaved, transactionSaved));
    }

    @Test
    public void testFinanceServiceCreateTransactionsWithoutTransactionCommission() {
	when(mockTransactionService.saveTransaction(any(Transaction.class))).thenReturn(transactionSaved);
	List<Transaction> transactionsDatabase = financeService.createTransactions(
		transactionBuddyToBank.getAccountFrom(),
		transactionBuddyToBank.getAccountTo(), transactionBuddyToBank.getAmount(),
		transactionBuddyToBank.getDescription());
	verify(mockAccountService, times(2)).saveAccount(any(Account.class));
	verify(mockTransactionService, times(1)).saveTransaction(any(Transaction.class));
	assertThat(transactionsDatabase.size()).isEqualTo(1);
	assertThat(transactionsDatabase).isEqualTo(Arrays.asList(transactionSaved));
    }

    @Test
    public void testFinanceServiceCreateTransactionDTO() {
	when(mockTransactionService.saveTransaction(any(Transaction.class))).thenReturn(transactionSaved);
	when(mockAccountService.findAccountByPersonNameAndBankName(any(String.class), any(String.class)))
		.thenReturn(accountFrom);
	when(mockAccountService.findFetchTransactionsAccountById(any(Long.class)))
		.thenReturn(accountFrom);
	financeService.createTransaction(personSaved, transferDTO);
	verify(mockAccountService, times(2)).findFetchTransactionsAccountById(any(Long.class));
	verify(mockAccountService, times(4)).saveAccount(any(Account.class));
	verify(mockTransactionService, times(2)).saveTransaction(any(Transaction.class));
	verify(mockAccountService, times(1)).findAccountByPersonNameAndBankName(any(String.class), any(String.class));
    }

    @Test
    public void testFinanceServiceValidateCreateTransaction() {
	when(mockAccountService.findAccountById(any(Long.class))).thenReturn(accountFrom, accountTo);
	when(mockAccountService.findAccountById(any(Long.class)))
		.thenReturn(accountFrom);
	List<String> errors = financeService.validateCreateTransaction(personSaved, transferDTO);
	verify(mockAccountService, times(2)).findAccountById(any(Long.class));
	assertThat(errors).isEmpty();
    }

    @Test
    public void testFinanceServiceInitPerson() {
	when(mockPersonService.findFetchWithAllPersonByName(any(String.class)))
		.thenReturn(personSaved);
	Person personDatabase = financeService.initPerson(personSaved);
	verify(mockBankService, times(2)).findWithAccountsBankByName(any(String.class));
	verify(mockAccountService, times(2)).createAccount(any(double.class), any(), any());
	verify(mockAccountService, times(2)).findAccountByPersonNameAndBankName(any(), any());
	assertThat(personDatabase).isEqualTo(personSaved);
    }

    @Test
    public void testFinanceServiceCreateSecurePersonFromUserDetails() {
	when(mockPersonService.findFetchWithAllPersonByName(any(String.class)))
		.thenReturn(personSaved);
	Person personDatabase = financeService.createSecurePerson(userDetails);
	verify(userDetailsManager, times(1)).createUser(any(UserDetails.class));
	verify(mockBankService, times(2)).findWithAccountsBankByName(any(String.class));
	verify(mockAccountService, times(2)).createAccount(any(double.class), any(), any());
	verify(mockAccountService, times(2)).findAccountByPersonNameAndBankName(any(), any());
	assertThat(personDatabase).isEqualTo(personSaved);
    }

    @Test
    public void testFinanceServiceGetTransactionType() {
	Transaction.TransactionType transactionType = financeService.getTransactionType(true, true);
	assertThat(transactionType).isEqualTo(Transaction.TransactionType.BUDDY_TO_BUDDY);
	transactionType = financeService.getTransactionType(true, false);
	assertThat(transactionType).isEqualTo(Transaction.TransactionType.BUDDY_TO_BANK);
	transactionType = financeService.getTransactionType(false, true);
	assertThat(transactionType).isEqualTo(Transaction.TransactionType.BANK_TO_BUDDY);
	transactionType = financeService.getTransactionType(false, false);
	assertThat(transactionType).isEqualTo(Transaction.TransactionType.BANK_TO_BANK);
    }
}
