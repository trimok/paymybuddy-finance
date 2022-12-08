package com.paymybuddy.finance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.paymybuddy.finance.model.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Person findByName(String name);

    @Query("from Person p left join fetch p.contactAccounts where p.name=:name")
    Person findFetchWithContactAccountsByName(String name);

    @Query("from Person p left join fetch p.accounts a where p.name=:name")
    Person findFetchWithAccountsByName(String name);

    @Query("from Person p left join fetch p.accounts a "
	    + " left join fetch a.transactionsFrom   left join fetch a.transactionsTo where p.name=:name")
    Person findFetchWithAccountsAndTransactionsByName(String name);

    @Query("from Person p left join fetch p.contactAccounts left join fetch p.accounts a "
	    + " left join fetch a.transactionsFrom   left join fetch a.transactionsTo where p.name=:name")
    Person findFetchWithAllByName(String name);
}
