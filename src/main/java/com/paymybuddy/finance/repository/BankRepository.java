package com.paymybuddy.finance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.paymybuddy.finance.model.Bank;

public interface BankRepository extends JpaRepository<Bank, Long> {
    Bank findByName(String name);

    @Query("from Bank b left join fetch b.accounts where b.name=:name")
    Bank findFetchWithAccountsByName(String name);
}
