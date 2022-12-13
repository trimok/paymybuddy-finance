package com.paymybuddy.finance.repository;

import static com.paymybuddy.finance.constants.Constants.PAY_MY_BUDDY_BANK;
import static com.paymybuddy.finance.constants.Constants.PAY_MY_BUDDY_GENERIC_USER;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.paymybuddy.finance.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByPersonNameAndBankName(String personName, String bankName);

    @Query("from Account a left join fetch a.transactionsFrom left join fetch a.transactionsTo where a.person.name=:personName and a.bank.name=:bankName")
    Account findFetchWithTransactionsByPersonNameAndBankName(String personName, String bankName);

    @Query("from Account a left join fetch a.transactionsFrom left join fetch a.transactionsTo where a.id=:accountId")
    Account findFetchWithTransactionsById(Long accountId);

    @Query("from Account a left join fetch a.contactPersons where a.person.name=:personName and a.bank.name=:bankName")
    Account findFetchWithContactPersonsByPersonNameAndBankName(String personName, String bankName);

    @Query("from Account a left join a.person p left join a.bank b where p.id <> :personId " + " and bank.name = '"
	    + PAY_MY_BUDDY_BANK + "'" + " and p.name <> '" + PAY_MY_BUDDY_GENERIC_USER + "'")
    List<Account> findAllExceptPerson(Long personId);
}
