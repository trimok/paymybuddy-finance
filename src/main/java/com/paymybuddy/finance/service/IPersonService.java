package com.paymybuddy.finance.service;

import java.util.List;

import com.paymybuddy.finance.model.Person;

public interface IPersonService {

    Person savePerson(Person person);

    Person createPerson(String name, String email);

    Person findPersonByName(String name);

    List<Person> findAllPersons();

    Person findFetchWithContactAccountsPersonByName(String name);

    Person findFetchWithAccountsTransactionsPersonByName(String name);

    Person findFetchWithAllPersonByName(String name);

    Person findFetchWithAccountsPersonByName(String name);

    void deleteAllPersons();
}