package com.paymybuddy.finance.service;

import static com.paymybuddy.finance.constants.Constants.AMOUNT_BEGIN;
import static com.paymybuddy.finance.constants.Constants.AUTHORITY_USER;
import static com.paymybuddy.finance.constants.Constants.COMMISSION_RATE;
import static com.paymybuddy.finance.constants.Constants.ERROR_ACCOUNTS_MUST_BE_DIFFERENT;
import static com.paymybuddy.finance.constants.Constants.ERROR_ORIGIN_ACCOUNT_AMOUNT_NOT_SUFFICIENT;
import static com.paymybuddy.finance.constants.Constants.ERROR_PASSWORDS_DONT_MATCH;
import static com.paymybuddy.finance.constants.Constants.ERROR_SELECT_ACCOUNT_FROM;
import static com.paymybuddy.finance.constants.Constants.ERROR_SELECT_ACCOUNT_TO;
import static com.paymybuddy.finance.constants.Constants.ERROR_TRANSACTION_MUST_BE_FROM_BUDDY_ACCOUNT;
import static com.paymybuddy.finance.constants.Constants.ERROR_USER_ALREADY_REGISTERED;
import static com.paymybuddy.finance.constants.Constants.PAY_MY_BUDDY_BANK;
import static com.paymybuddy.finance.constants.Constants.PAY_MY_BUDDY_GENERIC_USER;
import static com.paymybuddy.finance.constants.Constants.PAY_MY_BUDDY_GENERIC_USER_PASSWORD_ENCODED;
import static com.paymybuddy.finance.constants.Constants.TRANSACTION_COMMISSION_DESCRIPTION;
import static com.paymybuddy.finance.constants.Constants.USER_GENERIC_BANK;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paymybuddy.finance.dto.TransferDTO;
import com.paymybuddy.finance.dto.UserLoginDTO;
import com.paymybuddy.finance.model.Account;
import com.paymybuddy.finance.model.Bank;
import com.paymybuddy.finance.model.Person;
import com.paymybuddy.finance.model.Transaction;
import com.paymybuddy.finance.security.PayMyBuddyUserDetails;

@Service
public class FinanceService implements IFinanceService {

    @Autowired
    public FinanceService(UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder,
	    IRoleService roleService, IPersonService personService, IBankService bankService,
	    IAccountService accountService, ITransactionService transactionService) {
	super();
	this.userDetailsManager = userDetailsManager;
	this.passwordEncoder = passwordEncoder;
	this.roleService = roleService;
	this.personService = personService;
	this.bankService = bankService;
	this.accountService = accountService;
	this.transactionService = transactionService;
    }

    /**
     * userDetailsManager
     */
    @Autowired
    UserDetailsManager userDetailsManager;

    /**
     * passwordEncoder
     */
    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * roleService
     */
    @Autowired
    IRoleService roleService;

    /**
     * personService
     */
    @Autowired
    IPersonService personService;

    /**
     * bankService
     */
    @Autowired
    IBankService bankService;

    /**
     * accountService
     */
    @Autowired
    IAccountService accountService;

    /**
     * transactionService
     */
    @Autowired
    ITransactionService transactionService;

    /**
     * Truncate the tables
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll() {
	List<Person> persons = personService.findAllPersons();
	for (Person person : persons) {
	    while (!person.getContactAccounts().isEmpty()) {
		accountService.removeContactAccount(person, person.getContactAccounts().iterator().next());
	    }
	}
	transactionService.deleteAllTransactions();
	accountService.deleteAllAccounts();
	bankService.deleteAllBanks();
	roleService.deleteAllRoles();
	personService.deleteAllPersons();
    }

    /**
     * Initializations of the application
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @EventListener(ApplicationReadyEvent.class)
    public void initApplication() {

	// Buddy bank creation
	Bank payMyBuddyBank = bankService.findBankByName(PAY_MY_BUDDY_BANK);
	if (payMyBuddyBank == null) {
	    payMyBuddyBank = bankService.createBank(PAY_MY_BUDDY_BANK);
	}

	// User Generic Bank creation
	Bank userGenricBank = bankService.findBankByName(USER_GENERIC_BANK);
	if (userGenricBank == null) {
	    userGenricBank = bankService.createBank(USER_GENERIC_BANK);
	}

	// Generic Buddy User creation
	Person payMyBuddyGenericUser = personService.findPersonByName(PAY_MY_BUDDY_GENERIC_USER);
	if (payMyBuddyGenericUser == null) {
	    userDetailsManager.createUser(
		    new PayMyBuddyUserDetails(
			    new UserLoginDTO(PAY_MY_BUDDY_GENERIC_USER, PAY_MY_BUDDY_GENERIC_USER_PASSWORD_ENCODED,
				    PAY_MY_BUDDY_GENERIC_USER),
			    Arrays.asList(new SimpleGrantedAuthority(AUTHORITY_USER))));
	    payMyBuddyGenericUser = personService.findFetchWithAccountsPersonByName(PAY_MY_BUDDY_GENERIC_USER);
	    accountService.createAccount(0, payMyBuddyGenericUser, payMyBuddyBank);
	}
    }

    /**
     * Getting the type of a transaction
     * 
     * @param buddyFrom : indicating the type of bank of origin account
     * @param buddyTo:  indicating the type of bank of destionation account
     * @return : the transaction type
     */
    @Override
    public Transaction.TransactionType getTransactionType(boolean buddyFrom, boolean buddyTo) {
	Transaction.TransactionType transactionType = null;

	if (buddyFrom) {
	    if (buddyTo) {
		transactionType = Transaction.TransactionType.BUDDY_TO_BUDDY;
	    } else {
		transactionType = Transaction.TransactionType.BUDDY_TO_BANK;
	    }
	} else {
	    if (buddyTo) {
		transactionType = Transaction.TransactionType.BANK_TO_BUDDY;
	    } else {
		transactionType = Transaction.TransactionType.BANK_TO_BANK;
	    }
	}
	return transactionType;
    }

    /**
     * Creating a atomic transaction
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Transaction createTransaction(Account accountFrom, Account accountTo, double amount, String description,
	    Transaction.TransactionType transactionType) {

	Transaction transaction = new Transaction(amount, description, LocalDateTime.now(), transactionType);

	accountFrom.changeAmount(-amount);
	accountTo.changeAmount(amount);

	accountService.saveAccount(accountFrom);
	accountService.saveAccount(accountTo);

	transaction.setAccountFrom(accountFrom);
	transaction.setAccountTo(accountTo);

	return transactionService.saveTransaction(transaction);
    }

    /**
     * Creating a functional set of transactions (commission + standard)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Transaction> createTransactions(Account accountFrom, Account accountTo, double amount,
	    String description) {

	boolean buddyFrom = PAY_MY_BUDDY_BANK.equals(accountFrom.getBank().getName());
	boolean buddyTo = PAY_MY_BUDDY_BANK.equals(accountTo.getBank().getName());
	boolean transactionCommissionContext = buddyFrom && buddyTo;

	Transaction.TransactionType transactionType = getTransactionType(buddyFrom, buddyTo);

	// Commission transaction
	Transaction transactionCommission = null;
	if (transactionCommissionContext) {
	    Account genericAccountPayMyBuddy = accountService.findAccountByPersonNameAndBankName(
		    PAY_MY_BUDDY_GENERIC_USER,
		    PAY_MY_BUDDY_BANK);

	    transactionCommission = createTransaction(accountFrom, genericAccountPayMyBuddy,
		    COMMISSION_RATE * amount, TRANSACTION_COMMISSION_DESCRIPTION,
		    Transaction.TransactionType.COMMISSION);
	}

	// Standard transaction
	Transaction transaction = createTransaction(accountFrom, accountTo,
		amount, description, transactionType);

	return transactionCommissionContext ? Arrays.asList(transaction, transactionCommission)
		: Arrays.asList(transaction);
    }

    /**
     * Creating a functional set of transactions (commission + standard), DTO
     * version
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createTransaction(Person person, TransferDTO transferDTO) {
	Account accountFrom = accountService.findFetchTransactionsAccountById(transferDTO.getAccountFromId());
	Account accountTo = accountService.findFetchTransactionsAccountById(transferDTO.getAccountToId());
	createTransactions(accountFrom, accountTo, transferDTO.getAmount(), transferDTO.getDescription());
    }

    /**
     * Validation before the creation of the transaction
     */
    @Override
    public List<String> validateCreateTransaction(Person person, TransferDTO transferDTO) {
	List<String> errors = new ArrayList<>();

	if (transferDTO.getAccountFromId() == null) {
	    errors.add(ERROR_SELECT_ACCOUNT_FROM);
	} else if (transferDTO.getAccountToId() == null) {
	    errors.add(ERROR_SELECT_ACCOUNT_TO);
	} else if (transferDTO.getAccountToId() == transferDTO.getAccountFromId()) {
	    errors.add(ERROR_ACCOUNTS_MUST_BE_DIFFERENT);
	} else {
	    Account accountFrom = accountService.findAccountById(transferDTO.getAccountFromId());
	    Account accountTo = accountService.findAccountById(transferDTO.getAccountToId());
	    if (!accountFrom.getBank().getName().equals(PAY_MY_BUDDY_BANK)
		    &&
		    accountTo.getPerson().getId() != person.getId()) {
		errors.add(ERROR_TRANSACTION_MUST_BE_FROM_BUDDY_ACCOUNT);
	    } else if (transferDTO.getAmount() > accountFrom.getAmount()) {
		errors.add(ERROR_ORIGIN_ACCOUNT_AMOUNT_NOT_SUFFICIENT);
	    }
	}

	return errors;
    }

    /**
     * For a registered user, this function is called during the registering
     * Oauth2/OpenId, the function is called after authentification
     * 
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Person initPerson(Person person) {

	// The person is supposed to exist in the database
	Person personDatabase = personService.findFetchWithAllPersonByName(person.getName());

	if (personDatabase != null) {
	    Bank bankUserGeneric = bankService.findWithAccountsBankByName(USER_GENERIC_BANK);
	    Bank bankPayMyBuddy = bankService.findWithAccountsBankByName(PAY_MY_BUDDY_BANK);

	    // Account for generic user bank : creation
	    Account accountGeneric = accountService.findAccountByPersonNameAndBankName(personDatabase.getName(),
		    USER_GENERIC_BANK);
	    if (accountGeneric == null) {
		accountService.createAccount(AMOUNT_BEGIN, personDatabase, bankUserGeneric);
	    }

	    // Account for buddy bank : creation
	    Account accountPayMyBuddy = accountService.findAccountByPersonNameAndBankName(personDatabase.getName(),
		    PAY_MY_BUDDY_BANK);
	    if (accountPayMyBuddy == null) {
		accountService.createAccount(0, personDatabase, bankPayMyBuddy);
	    }
	}
	return personDatabase;
    }

    /**
     * Creation of a secure person (Person + accounts + Role)
     * 
     * Called during registering, for registered users
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Person createSecurePerson(PayMyBuddyUserDetails userDetails) {

	// Creating secure Person (table Person + table Role + accounts) if not already
	// created
	Person personDatabase = personService.findPersonByName(userDetails.getUsername());
	if (personDatabase == null) {
	    userDetails.getUserLogin().setPassword(passwordEncoder.encode(userDetails.getPassword()));
	    userDetailsManager.createUser(userDetails);
	    // Person initialization (creating accounts)
	    return initPerson(new Person(userDetails.getUsername()));
	} else {
	    return personDatabase;
	}
    }

    @Override
    public List<String> validateCreateAuthorityUserPerson(String username, String password, String confirmPassword) {
	List<String> errors = new ArrayList<>();

	if (!password.equals(confirmPassword)) {
	    errors.add(ERROR_PASSWORDS_DONT_MATCH);
	} else if (userDetailsManager.userExists(username)) {
	    errors.add(ERROR_USER_ALREADY_REGISTERED);
	}
	return errors;
    }

    /**
     * Utilitary method to create a secure person (Person + accounts +
     * AUTHORITY_USER)
     * 
     */
    @Override
    public Person createAuthorityUserPerson(String name, String password) {
	UserLoginDTO userLogin = new UserLoginDTO(name, password, name);
	PayMyBuddyUserDetails userDetails = new PayMyBuddyUserDetails(userLogin);
	userDetails.addAuthority(new SimpleGrantedAuthority(AUTHORITY_USER));
	return createSecurePerson(userDetails);
    }

    /**
     * Utilitary method to create a secure person (Person + accounts + authority)
     * 
     */
    @Override
    public Person createAuthorityPerson(String name, String password, String authority) {
	UserLoginDTO userLogin = new UserLoginDTO(name, password);
	PayMyBuddyUserDetails userDetails = new PayMyBuddyUserDetails(userLogin);
	userDetails.addAuthority(new SimpleGrantedAuthority(authority));
	return createSecurePerson(userDetails);
    }
}
