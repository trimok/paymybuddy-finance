package com.paymybuddy.finance.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.paymybuddy.finance.model.Account;
import com.paymybuddy.finance.model.Transaction;

public interface ITransactionService {

    Transaction createTransaction(Account accountFrom, Account accountTo, float amount, String description);

    List<Transaction> findAllTransactions();

}