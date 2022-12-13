package com.paymybuddy.finance.service;

import java.util.List;

import com.paymybuddy.finance.model.Transaction;

public interface ITransactionService {

    List<Transaction> findAllTransactions();
}