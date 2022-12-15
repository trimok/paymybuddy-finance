package com.paymybuddy.finance.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paymybuddy.finance.model.Transaction;
import com.paymybuddy.finance.repository.AccountRepository;
import com.paymybuddy.finance.repository.TransactionRepository;

/**
 * @author trimok
 *
 */
@Service
public class TransactionService implements ITransactionService {

    /**
     * transactionRepository
     */
    @Autowired
    TransactionRepository transactionRepository;

    /**
     * accountRepository
     */
    @Autowired
    AccountRepository accountRepository;

    /**
     * Saving a transaction
     */
    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public Transaction saveTransaction(Transaction transaction) {
	return transactionRepository.save(transaction);
    }

    /**
     * findAllTransactions
     */
    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public List<Transaction> findAllTransactions() {
	return transactionRepository.findAll();
    }

    /**
     * deleteAllTransactions
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllTransactions() {
	transactionRepository.deleteAll();
	transactionRepository.flush();
    }

}
