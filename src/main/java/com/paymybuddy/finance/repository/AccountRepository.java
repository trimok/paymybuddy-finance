package com.paymybuddy.finance.repository;

import static com.paymybuddy.finance.constants.Constants.PAY_MY_BUDDY_BANK;
import static com.paymybuddy.finance.constants.Constants.PAY_MY_BUDDY_GENERIC_USER;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.paymybuddy.finance.model.Account;

/**
 * @author trimok
 *
 */
public interface AccountRepository extends JpaRepository<Account, Long> {
    /**
     * Find by person name and bank name
     * 
     * @param personName : personName
     * @param bankName   : bankName
     * @return : the account
     */
    Account findByPersonNameAndBankName(String personName, String bankName);

    /**
     * Find by person name and bank name, with transactions
     * 
     * @param personName : personName
     * @param bankName   : bankName
     * @return : the account
     */
    @Query("from Account a left join fetch a.transactionsFrom left join fetch a.transactionsTo where a.person.name=:personName and a.bank.name=:bankName")
    Account findFetchWithTransactionsByPersonNameAndBankName(String personName, String bankName);

    /**
     * Find by account id, with transactions
     * 
     * @param accountId
     * @return : the account
     */
    @Query("from Account a left join fetch a.transactionsFrom left join fetch a.transactionsTo where a.id=:accountId")
    Account findFetchWithTransactionsById(Long accountId);

    /**
     * Find by person name and bank name, with contact persons
     * 
     * @param personName : personName
     * @param bankName   : bankName
     * @return : the account
     */
    @Query("from Account a left join fetch a.contactPersons where a.person.name=:personName and a.bank.name=:bankName")
    Account findFetchWithContactPersonsByPersonNameAndBankName(String personName, String bankName);

    /**
     * Find all the accounts which may be used as contact accounts
     * 
     * @param personId : person id
     * @return : accounts lists
     */
    @Query("from Account a left join a.bank b left join a.person p   where  p.id <> :personId "
	    + " and bank.name = '"
	    + PAY_MY_BUDDY_BANK + "'" + " and p.name <> '" + PAY_MY_BUDDY_GENERIC_USER + "'")
    List<Account> findAllExceptPerson(Long personId);
}
