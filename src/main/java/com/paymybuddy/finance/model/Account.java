package com.paymybuddy.finance.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double amount;

    @ManyToOne
    private Person person;

    @ManyToOne
    private Bank bank;

    @OneToMany(mappedBy = "accountFrom")
    private Set<Transaction> transactionsFrom = new HashSet<>();

    @OneToMany(mappedBy = "accountTo")
    private Set<Transaction> transactionsTo = new HashSet<>();

    @ManyToMany(mappedBy = "contactAccounts")
    private List<Person> contactPersons = new ArrayList<>();

    public void addTransactionFrom(Transaction transaction) {
	transactionsFrom.add(transaction);
	transaction.setAccountFrom(this);
    }

    public void addTransactionTo(Transaction transaction) {
	transactionsTo.add(transaction);
	transaction.setAccountTo(this);
    }

    public Account(Person person, Bank bank, float amount) {
	super();
	this.amount = amount;
	this.person = person;
	this.bank = bank;
    }

    public Account(float amount) {
	super();
	this.amount = amount;
    }

    public void changeAmount(double changeAmount) {
	amount += changeAmount;
    }

    @Override
    public boolean equals(Object o) {
	if (this == o)
	    return true;

	if (!(o instanceof Account))
	    return false;

	Account other = (Account) o;

	return id != null &&
		id.equals(other.getId());
    }

    @Override
    public int hashCode() {
	return getClass().hashCode();
    }
}
