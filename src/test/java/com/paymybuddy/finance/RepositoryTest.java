package com.paymybuddy.finance;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.paymybuddy.finance.model.Person;
import com.paymybuddy.finance.repository.PersonRepository;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@TestMethodOrder(value = org.junit.jupiter.api.MethodOrderer.OrderAnnotation.class)
public class RepositoryTest {

    private static final String SECURE_USER = "user@user.com";

    @Autowired
    private PersonRepository personRepository;

    private Person person = new Person(SECURE_USER, SECURE_USER);

    @BeforeEach
    void beforeEach() {

    }

    @AfterEach
    public void destroyAll() {
	personRepository.delete(person);
    }

    @Test
    void testFindPersonByName() {
	Person personDatabase = personRepository.save(person);

	AtomicInteger validIdFound = new AtomicInteger();

	if (personDatabase.getId() > 0) {
	    validIdFound.getAndIncrement();
	}

	assertThat(validIdFound.intValue()).isEqualTo(1);

	List<Person> persons = personRepository.findAll();
	assertThat(persons.size()).isEqualTo(1);
    }

}
