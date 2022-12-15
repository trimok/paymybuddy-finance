package com.paymybuddy.finance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paymybuddy.finance.model.Transaction;

/**
 * @author trimok
 *
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}
