package com.paymybuddy.finance.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paymybuddy.finance.model.Bank;
import com.paymybuddy.finance.repository.BankRepository;

@Service
public class BankService implements IBankService {

    @Autowired
    BankRepository bankRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Bank createBank(String name) {
	Bank bank = new Bank(name);

	return bankRepository.save(bank);
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public Bank findBankByName(String name) {
	return bankRepository.findByName(name);
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public Bank findWithAccountsBankByName(String name) {
	return bankRepository.findFetchWithAccountsByName(name);
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public List<Bank> findAllBanks() {
	return bankRepository.findAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllBanks() {
	bankRepository.deleteAll();
	bankRepository.flush();
    }
}
