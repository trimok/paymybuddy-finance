package com.paymybuddy.finance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.paymybuddy.finance.model.Person;

/**
 * @author trimok
 *
 */
public interface PersonRepository extends JpaRepository<Person, Long> {
    /**
     * Find person by name
     * 
     * @param name : the name of the person
     * @return : the person
     */
    Person findByName(String name);

    /**
     * Find person by name, with contact accounts
     * 
     * @param name : the name of the person
     * @return : the person
     */
    @Query("from Person p left join fetch p.contactAccounts where p.name=:name")
    Person findFetchWithContactAccountsByName(String name);

    /**
     * Find person by name, with accounts
     * 
     * @param name : the name of the person
     * @return : the person
     */
    @Query("from Person p left join fetch p.accounts a where p.name=:name")
    Person findFetchWithAccountsByName(String name);

    /**
     * Find person by name, with accounts and transactions
     * 
     * @param name : the name of the person
     * @return : the person
     */
    @Query("from Person p left join fetch p.accounts a "
	    + " left join fetch a.transactionsFrom   left join fetch a.transactionsTo where p.name=:name")
    Person findFetchWithAccountsAndTransactionsByName(String name);

    /**
     * Find person by name, with contact accounts, accounts and transactions
     * 
     * @param name : the name of the person
     * @return : the person
     */
    @Query("from Person p left join fetch p.contactAccounts left join fetch p.accounts a "
	    + " left join fetch a.transactionsFrom   left join fetch a.transactionsTo where p.name=:name")
    Person findFetchWithAllByName(String name);
}
