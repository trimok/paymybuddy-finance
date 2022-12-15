package com.paymybuddy.finance.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author trimok
 *
 */
@Getter
@Setter
/**
 * 
 */
@NoArgsConstructor
/**
 * @param id               : id
 * @param amount           : amount
 * @param person           : person
 * @param bank             : bank
 * @param transactionsFrom : set of transactions from
 * @param transactionsTo   : set of transactions to
 * @param contactPersons   : set of contact Persons
 */
@AllArgsConstructor
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "person_id", "bank_id" }) })
public class Account {
    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * amount
     */
    private double amount;

    /**
     * the person owner of the account
     */
    @ManyToOne
    private Person person;

    /**
     * the bank where the account is
     */
    @ManyToOne
    private Bank bank;

    /**
     * the set of transactions from (the account)
     */
    @OneToMany(mappedBy = "accountFrom", orphanRemoval = true)
    @Cascade({ CascadeType.ALL })
    private Set<Transaction> transactionsFrom = new HashSet<>();

    /**
     * the set of transactions to (the account)
     */
    @OneToMany(mappedBy = "accountTo", orphanRemoval = true)
    @Cascade({ CascadeType.ALL })
    private Set<Transaction> transactionsTo = new HashSet<>();

    /**
     * The list of persons who have this account as contact account
     */
    @ManyToMany(mappedBy = "contactAccounts")
    @Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
    private List<Person> contactPersons = new ArrayList<>();

    /**
     * Adding a transaction from
     * 
     * @param transaction : the transaction
     */
    public void addTransactionFrom(Transaction transaction) {
	transactionsFrom.add(transaction);
	transaction.setAccountFrom(this);
    }

    /**
     * Adding a transaction to
     * 
     * @param transaction : the transaction
     */
    public void addTransactionTo(Transaction transaction) {
	transactionsTo.add(transaction);
	transaction.setAccountTo(this);
    }

    /**
     * Constructor
     * 
     * @param person : the person
     * @param bank   : the bank
     * @param amount : the amount
     */
    public Account(Person person, Bank bank, float amount) {
	super();
	this.amount = amount;
	this.person = person;
	this.bank = bank;
    }

    /**
     * Construcot
     * 
     * @param amount : the amount
     */
    public Account(float amount) {
	super();
	this.amount = amount;
    }

    /**
     * Change the amount of the account
     * 
     * @param changeAmount : the modification of the amount
     */
    public void changeAmount(double changeAmount) {
	amount += changeAmount;
    }

    /**
     * equals
     */
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

    /**
     * hashCode
     */
    @Override
    public int hashCode() {
	return getClass().hashCode();
    }
}
