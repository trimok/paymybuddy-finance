package com.paymybuddy.finance.service;

import java.util.List;

import com.paymybuddy.finance.model.Bank;

public interface IBankService {

    Bank createBank(String name);

    List<Bank> findAllBanks();

    Bank findBankByName(String name);

    Bank findWithAccountsBankByName(String name);
}