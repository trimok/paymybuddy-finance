package com.paymybuddy.finance.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.NaturalId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
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
 * @AllArgsConstructor
 * 
 * @param id              : id
 * @param name            : name
 * @param email           : email
 * @param password        : password
 * @param enabled         : enabled
 * @param roles           : the roles (authorities)
 * @param accounts        : the accounts
 * @param contactAccounts : the contact accounts
 */
@AllArgsConstructor
/**
 * @NoArgsConstructor
 */
@NoArgsConstructor
@Entity
public class Person {
    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * name (unique)
     */
    @NaturalId
    @Column(unique = true)
    private String name;

    /**
     * email
     */
    private String email;

    /**
     * password
     */
    private String password;

    /**
     * enabled
     */
    private boolean enabled = true;

    /**
     * The roles (authorities)
     */
    @OneToMany(mappedBy = "person", orphanRemoval = true)
    @Cascade({ CascadeType.ALL })
    private Set<Role> roles = new HashSet<>();

    /**
     * The accounts
     */
    @OneToMany(mappedBy = "person", orphanRemoval = true)
    @Cascade({ CascadeType.ALL })
    private Set<Account> accounts = new HashSet<>();

    /**
     * The contact accounts
     */
    @ManyToMany
    @Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "person_contactaccount", joinColumns = @JoinColumn(name = "person_id"), inverseJoinColumns = @JoinColumn(name = "account_id"))
    private Set<Account> contactAccounts = new HashSet<>();

    /**
     * Adding an account
     * 
     * @param account : the account
     */
    public void addAccount(Account account) {
	accounts.add(account);
	account.setPerson(this);
    }

    /**
     * Adding a contact account
     * 
     * @param account : the account
     */
    public void addContactAccount(Account account) {
	contactAccounts.add(account);
	account.getContactPersons().add(this);
    }

    /**
     * Constructor
     * 
     * @param name : the name of the person
     */
    public Person(String name) {
	super();
	this.name = name;
    }

    /***
     * Constructor (for tests)
     * 
     * @param id : the technical id of the person
     */

    public Person(Long id) {
	super();
	this.id = id;
    }

    /**
     * @param name  : the name of the person
     * @param email : the email of the person
     */
    public Person(String name, String email) {
	super();
	this.email = email;
	this.name = name;
    }

    /**
     * Getting the list of transactions associated with a person
     * 
     * @return : the list of transactions
     */
    @SuppressWarnings("unchecked")
    public List<Transaction> getTransactions() {
	Set<Transaction> transactionsSet = new HashSet<>();

	for (Account account : accounts) {
	    transactionsSet.addAll(account.getTransactionsFrom());
	    transactionsSet.addAll(account.getTransactionsTo());
	}

	Set<Transaction> transactionsTreeSet = new TreeSet<>(transactionsSet);

	List<Transaction> transactionList = new ArrayList<>();
	transactionList.addAll(transactionsTreeSet);
	Collections.sort(transactionList);

	return transactionList;
    }
}
