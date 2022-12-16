package com.paymybuddy.finance.service;

import java.util.List;

import com.paymybuddy.finance.dto.TransferDTO;
import com.paymybuddy.finance.model.Account;
import com.paymybuddy.finance.model.Person;
import com.paymybuddy.finance.model.Transaction;
import com.paymybuddy.finance.model.Transaction.TransactionType;
import com.paymybuddy.finance.security.SecureUser;

/**
 * @author trimok
 *
 */
public interface IFinanceService {

    /**
     * deleteAll
     */
    void deleteAll();

    /**
     * Validation before the creation of a transaction
     * 
     * @param person      : person
     * @param transferDTO : transferDTO
     * @return : a list of errors
     */
    List<String> validateCreateTransaction(Person person, TransferDTO transferDTO);

    /**
     * Functional creation of transaction (DTO version)
     * 
     * @param person      : person
     * @param transferDTO : transferDTO
     */
    void createTransaction(Person person, TransferDTO transferDTO);

    /**
     * Functional creation of transaction
     * 
     * @param accountFrom : accountFrom
     * @param accountTo   :accountTo
     * @param amount      : amount
     * @param description : description
     * @return : a list of transactions (1 or 2 if commission)
     */
    List<Transaction> createTransactions(Account accountFrom, Account accountTo, double amount, String description);

    /**
     * Atomic creation of a transaction
     * 
     * @param accountFrom     : accountFrom
     * @param accountTo       :accountTo
     * @param amount          : amount
     * @param description     : description
     * @param transactionType : transaction type
     * @return : a transaction
     */
    Transaction createTransaction(Account accountFrom, Account accountTo, double amount, String description,
	    TransactionType transactionType);

    /**
     * Initialization of the application
     */
    void initApplication();

    /**
     * Initialization for a Person
     * 
     * @param person : a person
     * @return : the person
     */
    Person initPerson(Person person);

    /**
     * Creation of a secure Person (Person + password + Role)
     * 
     * @param secureUser : secureUser
     * @return : a Person
     */
    Person createSecurePerson(SecureUser secureUser);
}