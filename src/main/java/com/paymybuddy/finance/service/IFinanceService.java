package com.paymybuddy.finance.service;

import java.util.List;

import com.paymybuddy.finance.dto.TransferDTO;
import com.paymybuddy.finance.model.Account;
import com.paymybuddy.finance.model.Person;
import com.paymybuddy.finance.model.Transaction;
import com.paymybuddy.finance.model.Transaction.TransactionType;

public interface IFinanceService {

    void deleteAll();

    void createTransaction(Person person, TransferDTO transferDTO);

    List<Transaction> createTransactions(Account accountFrom, Account accountTo, double amount, String description);

    Transaction createTransaction(Account accountFrom, Account accountTo, double amount, String description,
	    TransactionType transactionType);

    void initApplication();

    Person initPerson(Person person);
}