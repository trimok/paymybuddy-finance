package com.paymybuddy.finance.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paymybuddy.finance.model.Account;
import com.paymybuddy.finance.model.Bank;
import com.paymybuddy.finance.model.Person;
import com.paymybuddy.finance.repository.AccountRepository;

@Service
public class AccountService implements IAccountService {
    @Autowired
    AccountRepository accountRepository;

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
    public Account createContactAccount(Person person, Account contactAccount) {
	person.addContactAccount(contactAccount);

	return accountRepository.save(contactAccount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeContactAccount(Person person, Account contactAccount) {
	person.getContactAccounts().remove(contactAccount);
	contactAccount.getContactPersons().remove(person);
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
}
