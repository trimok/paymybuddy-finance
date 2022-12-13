package com.paymybuddy.finance.service;

import static com.paymybuddy.finance.constants.Constants.COMMISSION_RATE;
import static com.paymybuddy.finance.constants.Constants.PAY_MY_BUDDY_BANK;
import static com.paymybuddy.finance.constants.Constants.PAY_MY_BUDDY_GENERIC_USER;
import static com.paymybuddy.finance.constants.Constants.TRANSACTION_COMMISSION_DESCRIPTION;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paymybuddy.finance.model.Account;
import com.paymybuddy.finance.model.Transaction;
import com.paymybuddy.finance.repository.AccountRepository;
import com.paymybuddy.finance.repository.AuthoritiesRepository;
import com.paymybuddy.finance.repository.BankRepository;
import com.paymybuddy.finance.repository.PersonRepository;
import com.paymybuddy.finance.repository.TransactionRepository;

@Service
public class FinanceService implements IFinanceService {

    @Autowired
    AuthoritiesRepository authoritiesRepository;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    BankRepository bankRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll() {
	transactionRepository.deleteAll();
	accountRepository.deleteAll();
	bankRepository.deleteAll();
	authoritiesRepository.deleteAll();
	personRepository.deleteAll();
    }

    public Transaction.TransactionType getTransactionType(boolean buddyFrom, boolean buddyTo) {
	Transaction.TransactionType transactionType = null;

	if (buddyFrom) {
	    if (buddyTo) {
		transactionType = Transaction.TransactionType.BUDDY_TO_BUDDY;
	    } else {
		transactionType = Transaction.TransactionType.BUDDY_TO_BANK;
	    }
	} else {
	    if (buddyTo) {
		transactionType = Transaction.TransactionType.BANK_TO_BUDDY;
	    } else {
		transactionType = Transaction.TransactionType.BANK_TO_BANK;
	    }
	}
	return transactionType;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Transaction createTransaction(Account accountFrom, Account accountTo, double amount, String description,
	    Transaction.TransactionType transactionType) {

	Transaction transaction = new Transaction(amount, description, LocalDate.now(), transactionType);

	accountFrom.changeAmount(-amount);
	accountTo.changeAmount(amount);

	accountRepository.save(accountFrom);
	accountRepository.save(accountTo);

	transaction.setAccountFrom(accountFrom);
	transaction.setAccountTo(accountTo);

	return transactionRepository.save(transaction);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Transaction> createTransactions(Account accountFrom, Account accountTo, double amount,
	    String description) {

	boolean buddyFrom = PAY_MY_BUDDY_BANK.equals(accountFrom.getBank().getName());
	boolean buddyTo = PAY_MY_BUDDY_BANK.equals(accountTo.getBank().getName());

	Transaction.TransactionType transactionType = getTransactionType(buddyFrom, buddyTo);

	// Commission transaction
	Transaction transactionCommission = null;
	if (buddyFrom) {
	    Account genericAccountPayMyBuddy = accountRepository.findByPersonNameAndBankName(PAY_MY_BUDDY_GENERIC_USER,
		    PAY_MY_BUDDY_BANK);

	    transactionCommission = createTransaction(accountFrom, genericAccountPayMyBuddy,
		    COMMISSION_RATE * amount, TRANSACTION_COMMISSION_DESCRIPTION,
		    Transaction.TransactionType.COMMISSION);
	}

	// Standard transaction
	Transaction transaction = createTransaction(accountFrom, accountTo,
		amount, description, transactionType);

	return buddyFrom ? Arrays.asList(transaction, transactionCommission) : Arrays.asList(transaction);
    }
}
