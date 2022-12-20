package com.paymybuddy.finance.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paymybuddy.finance.model.Bank;
import com.paymybuddy.finance.repository.BankRepository;

/**
 * @author trimok
 *
 */
@Service
public class BankService implements IBankService {

    /**
     * bankRepository
     */
    @Autowired
    BankRepository bankRepository;

    @Autowired
    public BankService(BankRepository bankRepository) {
	this.bankRepository = bankRepository;
    }

    /**
     * Creating a bank
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Bank createBank(String name) {
	Bank bank = new Bank(name);

	return bankRepository.save(bank);
    }

    /**
     * findBankByName
     */
    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public Bank findBankByName(String name) {
	return bankRepository.findByName(name);
    }

    /**
     * findWithAccountsBankByName
     */
    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public Bank findWithAccountsBankByName(String name) {
	return bankRepository.findFetchWithAccountsByName(name);
    }

    /**
     * findAllBanks
     */
    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public List<Bank> findAllBanks() {
	return bankRepository.findAll();
    }

    /**
     * deleteAllBanks
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllBanks() {
	bankRepository.deleteAll();
	bankRepository.flush();
    }
}
