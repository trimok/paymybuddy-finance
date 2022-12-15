package com.paymybuddy.finance.service;

import java.util.List;

import com.paymybuddy.finance.model.Transaction;

/**
 * @author trimok
 *
 */
public interface ITransactionService {

    /**
     * Creating a transaction in the database
     * 
     * @param transaction : the transaction to be created
     * @return : the created transaction
     */
    Transaction saveTransaction(Transaction transaction);

    /**
     * Getting all transactions
     * 
     * @return : the list of all the transactions
     */
    List<Transaction> findAllTransactions();

    /**
     * deleteAllTransactions
     */
    void deleteAllTransactions();
}