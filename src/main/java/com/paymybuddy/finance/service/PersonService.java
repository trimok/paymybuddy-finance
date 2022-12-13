package com.paymybuddy.finance.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paymybuddy.finance.model.Person;
import com.paymybuddy.finance.repository.PersonRepository;

@Service
public class PersonService implements IPersonService {

    @Autowired
    PersonRepository personRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Person savePerson(Person person) {

	return personRepository.save(person);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Person createPerson(String name, String email) {
	Person person = new Person(name, email);

	return personRepository.save(person);
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public Person findPersonByName(String name) {
	return personRepository.findByName(name);
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public Person findFetchWithContactAccountsPersonByName(String name) {
	return personRepository.findFetchWithContactAccountsByName(name);
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public Person findFetchWithAccountsPersonByName(String name) {
	return personRepository.findFetchWithAccountsByName(name);
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public Person findFetchWithAccountsTransactionsPersonByName(String name) {
	return personRepository.findFetchWithAccountsAndTransactionsByName(name);
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public Person findFetchWithAllPersonByName(String name) {
	return personRepository.findFetchWithAllByName(name);
    }

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public List<Person> findAllPersons() {
	return personRepository.findAll();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllPersons() {
	personRepository.deleteAll();
	personRepository.flush();
    }
}
