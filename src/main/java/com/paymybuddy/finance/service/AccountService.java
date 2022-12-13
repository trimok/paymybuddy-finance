package com.paymybuddy.finance.service;

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

@Service
public class AccountService implements IAccountService {
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PersonRepository personRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Account saveAccount(Account account) {
	return accountRepository.save(account);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Account createAccount(float amount, Person person, Bank bank) {
	Account account = new Account(amount);

	person.addAccount(account);
	bank.addAccount(account);

	return accountRepository.save(account);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Account createContactAccount(Person person, ContactDTO contactDTO) {
	Account contactAccountToAdd = accountRepository.findById(contactDTO.getContactAccountIdToAdd()).orElse(null);

	return createContactAccount(person, contactAccountToAdd);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Account createContactAccount(Person person, Account contactAccount) {
	person.addContactAccount(contactAccount);

	return accountRepository.save(contactAccount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeContactAccount(Person person, ContactDTO contactDTO) {
	Account contactAccountToRemove = accountRepository.findById(contactDTO.getContactAccountIdToRemove())
		.orElse(null);

	removeContactAccount(person, contactAccountToRemove);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeContactAccount(Person person, Account contactAccount) {
	person.getContactAccounts().remove(contactAccount);
	contactAccount.getContactPersons().remove(person);
	personRepository.save(person);
	accountRepository.save(contactAccount);
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public Account findAccountByPersonNameAndBankName(String personName, String bankName) {
	return accountRepository.findByPersonNameAndBankName(personName, bankName);
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public Account findFetchTransactionsAccountByPersonNameAndBankName(String personName, String bankName) {
	return accountRepository.findFetchWithTransactionsByPersonNameAndBankName(personName, bankName);
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public Account findFetchWithContactPersonsAccountByPersonNameAndBankName(String personName, String bankName) {
	return accountRepository.findFetchWithContactPersonsByPersonNameAndBankName(personName, bankName);
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public List<Account> findAllAccounts() {
	return accountRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public Account findAccountById(Long accountId) {
	return accountRepository.findById(accountId).orElse(null);
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public List<Account> findAllAccountsExceptPersonAccounts(Person person) {
	return accountRepository.findAllExceptPerson(person.getId());
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public Account findFetchTransactionsAccountById(long accountId) {
	return accountRepository.findFetchWithTransactionsById(accountId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllAccounts() {
	accountRepository.deleteAll();
	accountRepository.flush();
    }

}
