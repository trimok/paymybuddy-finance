package com.paymybuddy.finance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.paymybuddy.finance.model.Bank;

/**
 * @author trimok
 *
 */
public interface BankRepository extends JpaRepository<Bank, Long> {
    /**
     * Find bank by bame
     * 
     * @param name : the name of the bank
     * @return : the bank
     */
    Bank findByName(String name);

    /**
     * Find bank by name with accounts
     * 
     * @param name : the name of the bank
     * @return : the bank
     */
    @Query("from Bank b left join fetch b.accounts where b.name=:name")
    Bank findFetchWithAccountsByName(String name);
}
