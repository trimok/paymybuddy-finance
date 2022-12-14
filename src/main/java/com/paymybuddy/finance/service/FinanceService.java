package com.paymybuddy.finance.service;

import static com.paymybuddy.finance.constants.Constants.AMOUNT_BEGIN;
import static com.paymybuddy.finance.constants.Constants.COMMISSION_RATE;
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
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paymybuddy.finance.dto.TransferDTO;
import com.paymybuddy.finance.dto.UserLoginDTO;
import com.paymybuddy.finance.model.Account;
import com.paymybuddy.finance.model.Bank;
import com.paymybuddy.finance.model.Person;
import com.paymybuddy.finance.model.Transaction;
import com.paymybuddy.finance.security.SecureUser;

@Service
public class FinanceService implements IFinanceService {
    private static final String ERROR_ORIGIN_ACCOUNT_AMOUNT_NOT_SUFFICIENT = "originAccountAmountNotSufficient";

    private static final String ERROR_TRANSACTION_MUST_BE_FROM_BUDDY_ACCOUNT = "transactionMustBeFromBuddyAccount";

    private static final String ERROR_ACCOUNTS_MUST_BE_DIFFERENT = "accountsMustBeDifferent";

    private static final String ERROR_SELECT_ACCOUNT_TO = "selectAccountTo";

    private static final String ERROR_SELECT_ACCOUNT_FROM = "selectAccountFrom";

    @Autowired
    UserDetailsManager userDetailsManager;

    @Autowired
    IRoleService roleService;

    @Autowired
    IPersonService personService;

    @Autowired
    IBankService bankService;

    @Autowired
    IAccountService accountService;

    @Autowired
    ITransactionService transactionService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll() {
	List<Person> persons = personService.findAllPersons();
	for (Person person : persons) {
	    for (Account account : person.getContactAccounts()) {
		accountService.removeContactAccount(person, account);
	    }
	}
	transactionService.deleteAllTransactions();
	accountService.deleteAllAccounts();
	bankService.deleteAllBanks();
	roleService.deleteAllRoles();
	personService.deleteAllPersons();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initApplication() {
	deleteAll();

	Bank payMyBuddyBank = bankService.findBankByName(PAY_MY_BUDDY_BANK);
	if (payMyBuddyBank == null) {
	    payMyBuddyBank = bankService.createBank(PAY_MY_BUDDY_BANK);
	}

	Bank userGenricBank = bankService.findBankByName(USER_GENERIC_BANK);
	if (userGenricBank == null) {
	    userGenricBank = bankService.createBank(USER_GENERIC_BANK);
	}

	Person payMyBuddyGenericUser = personService.findPersonByName(PAY_MY_BUDDY_GENERIC_USER);
	if (payMyBuddyGenericUser == null) {
	    userDetailsManager.createUser(
		    new SecureUser(
			    new UserLoginDTO(PAY_MY_BUDDY_GENERIC_USER, PAY_MY_BUDDY_GENERIC_USER_PASSWORD_ENCODED)));
	    payMyBuddyGenericUser = personService.findFetchWithAccountsPersonByName(PAY_MY_BUDDY_GENERIC_USER);
	    accountService.createAccount(0, payMyBuddyGenericUser, payMyBuddyBank);
	}
    }

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Transaction> createTransactions(Account accountFrom, Account accountTo, double amount,
	    String description) {

	boolean buddyFrom = PAY_MY_BUDDY_BANK.equals(accountFrom.getBank().getName());
	boolean buddyTo = PAY_MY_BUDDY_BANK.equals(accountTo.getBank().getName());

	Transaction.TransactionType transactionType = getTransactionType(buddyFrom, buddyTo);

	// Commission transaction
	Transaction transactionCommission = null;
	if (buddyFrom) {
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

	return buddyFrom ? Arrays.asList(transaction, transactionCommission) : Arrays.asList(transaction);
    }

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createTransaction(Person person, TransferDTO transferDTO) {
	Account accountFrom = accountService.findFetchTransactionsAccountById(transferDTO.getAccountFromId());
	Account accountTo = accountService.findFetchTransactionsAccountById(transferDTO.getAccountToId());
	createTransactions(accountFrom, accountTo, transferDTO.getAmount(), transferDTO.getDescription());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Person initPerson(Person person) {
	Person personDatabase = personService.findPersonByName(person.getName());
	if (personDatabase == null) {
	    personDatabase = personService.savePerson(person);
	}

	Bank bankUserGeneric = bankService.findBankByName(USER_GENERIC_BANK);
	Bank bankPayMyBuddy = bankService.findBankByName(PAY_MY_BUDDY_BANK);

	Account accountGeneric = accountService.findAccountByPersonNameAndBankName(personDatabase.getName(),
		USER_GENERIC_BANK);
	if (accountGeneric == null) {
	    accountService.createAccount(AMOUNT_BEGIN, personDatabase, bankUserGeneric);
	}

	Account accountPayMyBuddy = accountService.findAccountByPersonNameAndBankName(personDatabase.getName(),
		PAY_MY_BUDDY_BANK);
	if (accountPayMyBuddy == null) {
	    accountService.createAccount(0, personDatabase, bankPayMyBuddy);
	}

	return personDatabase;
    }
}
