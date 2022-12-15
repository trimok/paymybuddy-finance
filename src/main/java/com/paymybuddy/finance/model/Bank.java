package com.paymybuddy.finance.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.NaturalId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
 * @NoArgsConstructor
 */
@NoArgsConstructor
@Entity
public class Bank {
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
     * the accounts of the bank
     */
    @OneToMany(mappedBy = "bank", orphanRemoval = true)
    @Cascade({ CascadeType.ALL })
    private List<Account> accounts = new ArrayList<>();

    /**
     * Constructor
     * 
     * @param name : the name of the bank
     */
    public Bank(String name) {
	super();
	this.name = name;
    }

    /**
     * Adding an account
     * 
     * @param account : the account to be added
     */
    public void addAccount(Account account) {
	accounts.add(account);
	account.setBank(this);
    }

}
