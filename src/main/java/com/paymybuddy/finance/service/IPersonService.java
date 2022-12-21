package com.paymybuddy.finance.service;

import java.util.List;

import com.paymybuddy.finance.model.Person;

/**
 * @author trimok
 *
 */
public interface IPersonService {

    /**
     * Creating a person in the database
     * 
     * @param person : the person object
     * @return : the created person object
     */
    Person savePerson(Person person);

    /**
     * Creating a person in the database
     * 
     * @param name  : person name
     * @param email : person email
     * @return : the created person object
     */
    Person createPerson(String name, String email);

    /**
     * findPersonByName
     * 
     * @param name : the person name
     * @return : a Person object
     */
    Person findPersonByName(String name);

    /**
     * findAllPersons
     * 
     * @return : the list of all Person
     */
    List<Person> findAllPersons();

    /**
     * findFetchWithContactAccountsPersonByName
     * 
     * @param name : person name
     * @return : a Person Object
     */
    Person findFetchWithContactAccountsPersonByName(String name);

    /**
     * findFetchWithAccountsTransactionsPersonByName
     * 
     * @param name : person name
     * @return : a Person Object
     */
    Person findFetchWithAccountsTransactionsPersonByName(String name);

    /**
     * findFetchWithAllPersonByName
     * 
     * @param name : person name
     * @return : a Person Object
     */
    Person findFetchWithAllPersonByName(String name);

    /**
     * findFetchWithAccountsPersonByName
     * 
     * @param name : person name
     * @return : a Person Object
     */
    Person findFetchWithAccountsPersonByName(String name);

    /**
     * deleteAllPersons
     */
    void deleteAllPersons();

    /**
     * deletePerson
     */
    void deletePerson(Person person);
}