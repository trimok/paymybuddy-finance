package com.paymybuddy.finance.service;

import java.util.List;

import com.paymybuddy.finance.model.Bank;

/**
 * @author trimok
 *
 */
public interface IBankService {

    /**
     * Creating a bank
     * 
     * @param name : the name of the bank
     * @return : the bank
     */
    Bank createBank(String name);

    /**
     * findAllBanks
     * 
     * @return : all the banks
     */
    List<Bank> findAllBanks();

    /**
     * findBankByName
     * 
     * @param name : the bank bame
     * @return : the bank
     */
    Bank findBankByName(String name);

    /**
     * findWithAccountsBankByName
     * 
     * @param name : the bank name
     * @return : the bank
     */
    Bank findWithAccountsBankByName(String name);

    /**
     * deleteAllBanks
     */
    void deleteAllBanks();
}