package com.paymybuddy.finance.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paymybuddy.finance.constants.Constants;
import com.paymybuddy.finance.model.Account;
import com.paymybuddy.finance.model.Transaction;
import com.paymybuddy.finance.repository.TransactionRepository;

@Service
public class TransactionService implements ITransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Transaction createTransaction(Account accountFrom, Account accountTo, float amount, String description) {
	Transaction transaction = new Transaction(amount, description, LocalDate.now());

	accountFrom.addTransactionFrom(transaction);
	accountTo.addTransactionTo(transaction);

	accountFrom.changeAmount(-(1 + Constants.COMMISSION_RATE) * amount);
	accountTo.changeAmount(amount);

	return transactionRepository.save(transaction);
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public List<Transaction> findAllTransactions() {
	return transactionRepository.findAll();
    }
}
