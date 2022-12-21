package com.paymybuddy.finance.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paymybuddy.finance.model.Person;
import com.paymybuddy.finance.repository.PersonRepository;

/**
 * @author trimok
 *
 */
@Service
public class PersonService implements IPersonService {

    @Autowired
    private PersonRepository personRepository;

    /**
     * personRepository
     */
    @Autowired
    public PersonService(PersonRepository personRepository) {
	this.personRepository = personRepository;
    }

    /**
     * Saving a person object
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Person savePerson(Person person) {

	return personRepository.save(person);
    }

    /**
     * Creating a Person
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Person createPerson(String name, String email) {
	Person person = new Person(name, email);

	return personRepository.save(person);
    }

    /**
     * findPersonByName
     */
    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public Person findPersonByName(String name) {
	return personRepository.findByName(name);
    }

    /**
     * findFetchWithContactAccountsPersonByName
     */
    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public Person findFetchWithContactAccountsPersonByName(String name) {
	return personRepository.findFetchWithContactAccountsByName(name);
    }

    /**
     * findFetchWithAccountsPersonByName
     */
    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public Person findFetchWithAccountsPersonByName(String name) {
	return personRepository.findFetchWithAccountsByName(name);
    }

    /**
     * findFetchWithAccountsTransactionsPersonByName
     */
    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public Person findFetchWithAccountsTransactionsPersonByName(String name) {
	return personRepository.findFetchWithAccountsAndTransactionsByName(name);
    }

    /**
     * findFetchWithAllPersonByName
     */
    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public Person findFetchWithAllPersonByName(String name) {
	return personRepository.findFetchWithAllByName(name);
    }

    /**
     * findAllPersons
     */
    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public List<Person> findAllPersons() {
	return personRepository.findAll();
    }

    /**
     * deleteAllPersons
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllPersons() {
	personRepository.deleteAll();
	personRepository.flush();
    }

    /**
     * deletePerson
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePerson(Person person) {
	personRepository.delete(person);
	personRepository.flush();
    }
}
