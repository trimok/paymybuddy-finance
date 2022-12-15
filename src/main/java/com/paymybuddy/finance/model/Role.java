package com.paymybuddy.finance.model;

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
 *         Specific AUTHORITIES table
 */
@Getter
@Setter
/**
 * @AllArgsConstructor
 * 
 * @param id     : id
 * @param name   : name
 * @param roles  : roles (ex : ROLE_USER)
 * @param person : the person
 */
@AllArgsConstructor
/**
 * @NoArgsConstructor
 */
@NoArgsConstructor
@Entity
public class Role {
    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * name
     */
    private String name;

    /**
     * roles
     */
    private String roles;

    /**
     * the person associated with the role
     */
    @ManyToOne
    private Person person;
}