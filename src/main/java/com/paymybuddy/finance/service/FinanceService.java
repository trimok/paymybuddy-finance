package com.paymybuddy.finance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paymybuddy.finance.repository.AccountRepository;
import com.paymybuddy.finance.repository.BankRepository;
import com.paymybuddy.finance.repository.PersonRepository;
import com.paymybuddy.finance.repository.TransactionRepository;

@Service
public class FinanceService implements IFinanceService {
    @Autowired
    PersonRepository personRepository;

    @Autowired
    BankRepository bankRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll() {
	transactionRepository.deleteAll();
	accountRepository.deleteAll();
	bankRepository.deleteAll();
	personRepository.deleteAll();
    }
}
