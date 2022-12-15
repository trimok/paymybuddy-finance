package com.paymybuddy.finance.service;

import java.util.List;

import com.paymybuddy.finance.dto.ContactDTO;
import com.paymybuddy.finance.model.Account;
import com.paymybuddy.finance.model.Bank;
import com.paymybuddy.finance.model.Person;

/**
 * @author trimok
 *
 */
public interface IAccountService {

    /**
     * Creating an account
     * 
     * @param amount : amount
     * @param person : person
     * @param bank   : bank
     * @return : the account
     */
    Account createAccount(float amount, Person person, Bank bank);

    /**
     * Creating an account
     * 
     * @param account : the account object
     * @return : the created account
     */
    Account saveAccount(Account account);

    /**
     * Creating a contact account
     * 
     * @param person         : the person
     * @param contactAccount : the contact account
     * @return : the contact account created
     */
    Account createContactAccount(Person person, Account contactAccount);

    /**
     * Validation before the creation of a contact account
     * 
     * @param person     : the person
     * @param contactDTO : the contactDTO
     * @return : a list of errors
     */
    List<String> validateCreateContactAccount(Person person, ContactDTO contactDTO);

    /**
     * @param person     : the person
     * @param contactDTO : the contactDTO
     * @return : the account
     */
    Account createContactAccount(Person person, ContactDTO contactDTO);

    /**
     * findAccountById
     * 
     * @param accountId : accountId
     * @return : the account
     */
    Account findAccountById(Long accountId);

    /**
     * findFetchTransactionsAccountById
     * 
     * @param accountId : accountId
     * @return : the account
     */
    Account findFetchTransactionsAccountById(long accountId);

    /**
     * findAllAccounts
     * 
     * @return : the list of all the accounts
     */
    List<Account> findAllAccounts();

    /**
     * findAllAccountsExceptPersonAccounts Return all the accounts who could be used
     * as contact accounts for a person
     * 
     * @param person : the person
     * @return : list of accounts
     */
    List<Account> findAllAccountsExceptPersonAccounts(Person person);

    /**
     * findAccountByPersonNameAndBankName
     * 
     * @param personName : personName
     * @param bankName   : bankName
     * @return : account
     */
    Account findAccountByPersonNameAndBankName(String personName, String bankName);

    /**
     * findFetchTransactionsAccountByPersonNameAndBankName
     * 
     * @param personName : personName
     * @param bankName   : bankName
     * @return : account
     */
    Account findFetchTransactionsAccountByPersonNameAndBankName(String personName, String bankName);

    /**
     * findFetchWithContactPersonsAccountByPersonNameAndBankName
     * 
     * @param personName : personName
     * @param bankName   : bankName
     * @return : account
     */
    Account findFetchWithContactPersonsAccountByPersonNameAndBankName(String personName, String bankName);

    /**
     * Removing a contact account
     * 
     * @param person         : the person
     * @param contactAccount : the contact to be removed
     */
    void removeContactAccount(Person person, Account contactAccount);

    /**
     * Validation before the removing of a contact account
     * 
     * @param person     : the person
     * @param contactDTO : contactDTO
     * @return : a list of errors
     */
    List<String> validateRemoveContactAccount(Person person, ContactDTO contactDTO);

    /**
     * Removing a contact account
     * 
     * @param person     : the person
     * @param contactDTO : the contactDTO
     */
    void removeContactAccount(Person person, ContactDTO contactDTO);

    /**
     * Deleting all accounts
     */
    void deleteAllAccounts();

}