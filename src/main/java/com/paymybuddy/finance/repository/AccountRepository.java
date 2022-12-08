package com.paymybuddy.finance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.paymybuddy.finance.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByPersonNameAndBankName(String personName, String bankName);

    @Query("from Account a left join fetch a.transactionsFrom left join fetch a.transactionsTo where a.person.name=:personName and a.bank.name=:bankName")
    Account findFetchWithTransactionsByPersonNameAndBankName(String personName, String bankName);

    @Query("from Account a left join fetch a.contactPersons where a.person.name=:personName and a.bank.name=:bankName")
    Account findFetchWithContactPersonsByPersonNameAndBankName(String personName, String bankName);
}
