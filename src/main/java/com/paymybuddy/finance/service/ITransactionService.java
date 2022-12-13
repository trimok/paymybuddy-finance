package com.paymybuddy.finance.service;

import java.util.List;

import com.paymybuddy.finance.model.Transaction;

public interface ITransactionService {

    Transaction saveTransaction(Transaction transaction);

    List<Transaction> findAllTransactions();

    void deleteAllTransactions();
}