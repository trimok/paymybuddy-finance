package com.paymybuddy.finance.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paymybuddy.finance.model.Transaction;
import com.paymybuddy.finance.repository.AccountRepository;
import com.paymybuddy.finance.repository.TransactionRepository;

@Service
public class TransactionService implements ITransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    AccountRepository accountRepository;

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public Transaction saveTransaction(Transaction transaction) {
	return transactionRepository.save(transaction);
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public List<Transaction> findAllTransactions() {
	return transactionRepository.findAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllTransactions() {
	transactionRepository.deleteAll();
	transactionRepository.flush();
    }

}
