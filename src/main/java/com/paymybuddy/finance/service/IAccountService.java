package com.paymybuddy.finance.service;

import java.util.List;

import com.paymybuddy.finance.model.Account;
import com.paymybuddy.finance.model.Bank;
import com.paymybuddy.finance.model.Person;

public interface IAccountService {

    Account createAccount(float amount, Person person, Bank bank);

    Account createContactAccount(Person person, Account contactAccount);

    List<Account> findAllAccounts();

    Account findAccountByPersonNameAndBankName(String personName, String bankName);

    Account findFetchTransactionsAccountByPersonNameAndBankName(String personName, String bankName);

    Account findFetchWithContactPersonsAccountByPersonNameAndBankName(String personName, String bankName);

    void removeContactAccount(Person person, Account contactAccount);
}