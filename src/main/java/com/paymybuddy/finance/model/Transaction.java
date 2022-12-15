package com.paymybuddy.finance.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author trimok
 * 
 *         Transaction class
 *
 */
@Getter
@Setter
/**
 * @AllArgsConstructor
 * 
 * @param id              : id
 * @param accountFrom     : accountFrom
 * @param accountTo       : accountTo
 * @param amount          : amount
 * @param description     : description
 * @param transactionDate : transactionDate
 * @param transactionType : transactionType
 */
@AllArgsConstructor
/**
 * @NoArgsConstructor
 */
@NoArgsConstructor
@Entity

public class Transaction implements Comparable<Transaction> {

    /**
     * @author trimok
     * 
     *         Transaction type
     *
     */
    public static enum TransactionType {
	BANK_TO_BUDDY, BUDDY_TO_BUDDY, BUDDY_TO_BANK, BANK_TO_BANK, COMMISSION
    };

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * origin account
     */
    @ManyToOne
    private Account accountFrom;

    /**
     * destination account
     */
    @ManyToOne
    private Account accountTo;

    /**
     * amount
     */
    private double amount;
    /**
     * description
     */
    private String description;

    /**
     * transaction date
     */
    @Column(name = "TRANSACTION_DATE")
    private LocalDateTime transactionDate;

    /**
     * transaction type
     */
    @Column(name = "TRANSACTION_TYPE")
    private TransactionType transactionType = TransactionType.BUDDY_TO_BUDDY;

    /**
     * Constructor
     * 
     * @param amount          : amount
     * @param description     : description
     * @param transactionDate : transactionDate
     */
    public Transaction(double amount, String description, LocalDateTime transactionDate) {
	super();
	this.amount = amount;
	this.description = description;
	this.transactionDate = transactionDate;
    }

    /**
     * @param amount          : amount
     * @param description     : description
     * @param transactionDate : transactionDate
     * @param transactionType : transactionType
     */
    public Transaction(double amount, String description, LocalDateTime transactionDate,
	    TransactionType transactionType) {
	super();
	this.amount = amount;
	this.description = description;
	this.transactionDate = transactionDate;
	this.transactionType = transactionType;
    }

    /**
     * equals
     */
    @Override
    public boolean equals(Object o) {
	if (this == o)
	    return true;

	if (!(o instanceof Transaction))
	    return false;

	Transaction other = (Transaction) o;

	return id != null &&
		id.equals(other.getId());
    }

    /**
     * hashCode
     */
    @Override
    public int hashCode() {
	return getClass().hashCode();
    }

    /**
     * compareTo
     */
    @Override
    public int compareTo(Transaction transaction) {
	return this.transactionDate.isAfter(transaction.getTransactionDate()) ? -1 : 1;
    }
}
