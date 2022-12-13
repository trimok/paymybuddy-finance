package com.paymybuddy.finance.service;

import java.util.List;

import com.paymybuddy.finance.model.Account;
import com.paymybuddy.finance.model.Transaction;
import com.paymybuddy.finance.model.Transaction.TransactionType;

public interface IFinanceService {

    void deleteAll();

    List<Transaction> createTransactions(Account accountFrom, Account accountTo, double amount, String description);

    Transaction createTransaction(Account accountFrom, Account accountTo, double amount, String description,
	    TransactionType transactionType);

}