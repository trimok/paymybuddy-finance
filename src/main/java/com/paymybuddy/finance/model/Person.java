package com.paymybuddy.finance.model;

import java.util.HashSet;
import java.util.Set;

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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    @Column(unique = true)
    private String name;

    private String email;

    private String password;

    private boolean enabled = true;

    @OneToMany(mappedBy = "person", orphanRemoval = true)
    @Cascade({ CascadeType.ALL })
    private Set<Authorities> authorities = new HashSet<>();

    @OneToMany(mappedBy = "person", orphanRemoval = true)
    @Cascade({ CascadeType.ALL })
    private Set<Account> accounts = new HashSet<>();

    @ManyToMany
    @Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "person_contactaccount", joinColumns = @JoinColumn(name = "person_id"), inverseJoinColumns = @JoinColumn(name = "account_id"))
    private Set<Account> contactAccounts = new HashSet<>();

    public void addAccount(Account account) {
	accounts.add(account);
	account.setPerson(this);
    }

    public void addContactAccount(Account account) {
	contactAccounts.add(account);
	account.getContactPersons().add(this);
    }

    public Person(String name, String email) {
	super();
	this.email = email;
	this.name = name;
    }
}
