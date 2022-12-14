package com.paymybuddy.finance.service;

import java.util.List;

import com.paymybuddy.finance.dto.ContactDTO;
import com.paymybuddy.finance.model.Account;
import com.paymybuddy.finance.model.Bank;
import com.paymybuddy.finance.model.Person;

public interface IAccountService {

    Account createAccount(float amount, Person person, Bank bank);

    Account saveAccount(Account account);

    Account createContactAccount(Person person, Account contactAccount);

    List<String> validateCreateContactAccount(Person person, ContactDTO contactDTO);

    Account createContactAccount(Person person, ContactDTO contactDTO);

    Account findAccountById(Long accountId);

    Account findFetchTransactionsAccountById(long accountId);

    List<Account> findAllAccounts();

    List<Account> findAllAccountsExceptPersonAccounts(Person person);

    Account findAccountByPersonNameAndBankName(String personName, String bankName);

    Account findFetchTransactionsAccountByPersonNameAndBankName(String personName, String bankName);

    Account findFetchWithContactPersonsAccountByPersonNameAndBankName(String personName, String bankName);

    void removeContactAccount(Person person, Account contactAccount);

    List<String> validateRemoveContactAccount(Person person, ContactDTO contactDTO);

    void removeContactAccount(Person person, ContactDTO contactDTO);

    void deleteAllAccounts();

}