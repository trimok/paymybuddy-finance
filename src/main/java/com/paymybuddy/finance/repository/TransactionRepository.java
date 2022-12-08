package com.paymybuddy.finance.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.paymybuddy.finance.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}
