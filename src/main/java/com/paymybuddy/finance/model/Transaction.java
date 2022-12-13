package com.paymybuddy.finance.model;

import java.time.LocalDate;

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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class Transaction implements Comparable {

    public static enum TransactionType {
	BANK_TO_BUDDY, BUDDY_TO_BUDDY, BUDDY_TO_BANK, BANK_TO_BANK, COMMISSION
    };

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Account accountFrom;

    @ManyToOne
    private Account accountTo;

    private double amount;
    private String description;

    @Column(name = "TRANSACTION_DATE")
    private LocalDate transactionDate;

    @Column(name = "TRANSACTION_TYPE")
    private TransactionType transactionType = TransactionType.BUDDY_TO_BUDDY;

    public Transaction(double amount, String description, LocalDate transactionDate) {
	super();
	this.amount = amount;
	this.description = description;
	this.transactionDate = transactionDate;
    }

    public Transaction(double amount, String description, LocalDate transactionDate, TransactionType transactionType) {
	super();
	this.amount = amount;
	this.description = description;
	this.transactionDate = transactionDate;
	this.transactionType = transactionType;
    }

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

    @Override
    public int hashCode() {
	return getClass().hashCode();
    }

    @Override
    public int compareTo(Object o) {
	return this.transactionDate.isAfter(((Transaction) o).getTransactionDate()) ? -1 : 1;
    }
}
