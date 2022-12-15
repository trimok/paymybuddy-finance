package com.paymybuddy.finance.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paymybuddy.finance.dto.ContactDTO;
import com.paymybuddy.finance.model.Account;
import com.paymybuddy.finance.model.Bank;
import com.paymybuddy.finance.model.Person;
import com.paymybuddy.finance.repository.AccountRepository;
import com.paymybuddy.finance.repository.PersonRepository;

/**
 * @author trimok
 *
 */
@Service
public class AccountService implements IAccountService {
    /**
     * ERROR_SELECT_ACCOUNT_TO_REMOVE
     */
    private static final String ERROR_SELECT_ACCOUNT_TO_REMOVE = "selectAccountToRemove";

    /**
     * ERROR_ACCOUNT_ALREADY_EXISTS
     */
    private static final String ERROR_ACCOUNT_ALREADY_EXISTS = "accountAlreadyExists";

    /**
     * ERROR_SELECT_ACCOUNT_TO_ADD
     */
    private static final String ERROR_SELECT_ACCOUNT_TO_ADD = "selectAccountToAdd";

    /**
     * accountRepository
     */
    @Autowired
    AccountRepository accountRepository;

    /**
     * personRepository
     */
    @Autowired
    PersonRepository personRepository;

    /**
     * Saving the account
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Account saveAccount(Account account) {
	return accountRepository.save(account);
    }

    /**
     * Creating an account
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Account createAccount(float amount, Person person, Bank bank) {
	Account account = new Account(amount);

	person.addAccount(account);
	bank.addAccount(account);

	return accountRepository.save(account);
    }

    /**
     * Creating a contact account
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Account createContactAccount(Person person, ContactDTO contactDTO) {
	Account contactAccountToAdd = accountRepository.findById(contactDTO.getContactAccountIdToAdd()).orElse(null);

	return createContactAccount(person, contactAccountToAdd);
    }

    /**
     * Validating before the creation of a contact account
     */
    @Override
    public List<String> validateCreateContactAccount(Person person, ContactDTO contactDTO) {
	List<String> errors = new ArrayList<>();

	if (contactDTO.getContactAccountIdToAdd() == null) {
	    errors.add(ERROR_SELECT_ACCOUNT_TO_ADD);
	} else {
	    Account accountToAdd = findAccountById(contactDTO.getContactAccountIdToAdd());
	    if (person.getContactAccounts().contains(accountToAdd)) {
		errors.add(ERROR_ACCOUNT_ALREADY_EXISTS);
	    }
	}

	return errors;
    }

    /**
     * Creating a contact account
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Account createContactAccount(Person person, Account contactAccount) {
	person.addContactAccount(contactAccount);

	return accountRepository.save(contactAccount);
    }

    /**
     * Validating before the creation for a contact account
     */
    @Override
    public List<String> validateRemoveContactAccount(Person person, ContactDTO contactDTO) {
	List<String> errors = new ArrayList<>();

	if (contactDTO.getContactAccountIdToRemove() == null) {
	    errors.add(ERROR_SELECT_ACCOUNT_TO_REMOVE);
	}

	return errors;
    }

    /**
     * Removing a contact account
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeContactAccount(Person person, ContactDTO contactDTO) {
	Account contactAccountToRemove = accountRepository.findById(contactDTO.getContactAccountIdToRemove())
		.orElse(null);

	removeContactAccount(person, contactAccountToRemove);
    }

    /**
     * Removing a contact account
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeContactAccount(Person person, Account contactAccount) {
	person.getContactAccounts().remove(contactAccount);
	contactAccount.getContactPersons().remove(person);
	personRepository.save(person);
	accountRepository.save(contactAccount);
    }

    /**
     * findAccountByPersonNameAndBankName
     */
    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public Account findAccountByPersonNameAndBankName(String personName, String bankName) {
	return accountRepository.findByPersonNameAndBankName(personName, bankName);
    }

    /**
     * findFetchTransactionsAccountByPersonNameAndBankName
     */
    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public Account findFetchTransactionsAccountByPersonNameAndBankName(String personName, String bankName) {
	return accountRepository.findFetchWithTransactionsByPersonNameAndBankName(personName, bankName);
    }

    /**
     * findFetchWithContactPersonsAccountByPersonNameAndBankName
     */
    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public Account findFetchWithContactPersonsAccountByPersonNameAndBankName(String personName, String bankName) {
	return accountRepository.findFetchWithContactPersonsByPersonNameAndBankName(personName, bankName);
    }

    /**
     * findAllAccounts
     */
    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public List<Account> findAllAccounts() {
	return accountRepository.findAll();
    }

    /**
     * findAccountById
     */
    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public Account findAccountById(Long accountId) {
	return accountRepository.findById(accountId).orElse(null);
    }

    /**
     * findAllAccountsExceptPersonAccounts
     */
    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public List<Account> findAllAccountsExceptPersonAccounts(Person person) {
	return accountRepository.findAllExceptPerson(person.getId());
    }

    /**
     * findFetchTransactionsAccountById
     */
    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public Account findFetchTransactionsAccountById(long accountId) {
	return accountRepository.findFetchWithTransactionsById(accountId);
    }

    /**
     * deleteAllAccounts
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllAccounts() {
	accountRepository.deleteAll();
	accountRepository.flush();
    }

}
