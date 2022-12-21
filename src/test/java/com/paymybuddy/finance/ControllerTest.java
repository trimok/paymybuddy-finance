package com.paymybuddy.finance;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.paymybuddy.finance.controller.FinanceController;
import com.paymybuddy.finance.controller.LoginController;
import com.paymybuddy.finance.dto.ContactDTO;
import com.paymybuddy.finance.dto.TransferDTO;
import com.paymybuddy.finance.model.Account;
import com.paymybuddy.finance.model.Person;
import com.paymybuddy.finance.service.IAccountService;
import com.paymybuddy.finance.service.IFinanceService;
import com.paymybuddy.finance.service.ILoginService;
import com.paymybuddy.finance.service.IPersonService;

import jakarta.servlet.http.HttpSession;

@WebMvcTest({ LoginController.class, FinanceController.class })
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@TestMethodOrder(value = org.junit.jupiter.api.MethodOrderer.OrderAnnotation.class)
public class ControllerTest {

    private static final String SECURE_USER = "user@user.com";
    private static final String PASSWORD = "password";

    @MockBean
    private UserDetailsManager userDetailsManager;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    IFinanceService financeService;

    @MockBean
    IPersonService personService;

    @MockBean
    ILoginService loginService;

    @MockBean
    IAccountService accountService;

    @Mock
    HttpSession mockHttpSession;

    private HashMap<String, Object> sessionAttr;

    private Person person;

    @BeforeAll
    public void beforeAll() {
	person = new Person(SECURE_USER, PASSWORD);

	sessionAttr = new HashMap<String, Object>();
	sessionAttr.put("person", person);
    }

    @AfterAll
    public void afterAll() {
    }

    @Test
    public void testFinanceControllerGoToContact() throws Exception {

	when(userDetailsManager.userExists(any(String.class))).thenReturn(false);
	when(personService.findFetchWithAllPersonByName(SECURE_USER)).thenReturn(person);
	when(accountService.findAllAccountsExceptPersonAccounts(person)).thenReturn(new ArrayList<Account>());

	mockMvc
		.perform(MockMvcRequestBuilders.post("/gotoContact").sessionAttrs(sessionAttr))
		.andExpect(status().is(200))
		.andExpect(view().name("contact"))
		.andExpect(model().attributeExists("contactDTO"));

	verify(personService, times(1)).findFetchWithAllPersonByName(any(String.class));
	verify(accountService, times(1)).findAllAccountsExceptPersonAccounts(any(Person.class));
    }

    @Test
    public void testFinanceControllerAddContact() throws Exception {
	ContactDTO contactDTO = new ContactDTO(null, 1L, 1L);

	when(personService.findFetchWithAllPersonByName(SECURE_USER)).thenReturn(person);
	when(accountService.validateCreateContactAccount(any(Person.class), any(ContactDTO.class)))
		.thenReturn(new ArrayList<String>());

	mockMvc
		.perform(MockMvcRequestBuilders.post("/addContactAccount").sessionAttrs(sessionAttr).flashAttr(
			"contactDTO",
			contactDTO))
		.andExpect(status().is(200))
		.andExpect(view().name("contact"))
		.andExpect(model().attributeExists("contactDTO"));

	verify(personService, times(1)).findFetchWithAllPersonByName(any(String.class));
	verify(accountService, times(1)).validateCreateContactAccount(any(Person.class), any(ContactDTO.class));
	verify(accountService, times(1)).createContactAccount(any(Person.class), any(ContactDTO.class));
    }

    @Test
    public void testFinanceControllerRemoveContact() throws Exception {
	ContactDTO contactDTO = new ContactDTO(null, 1L, 1L);

	when(personService.findFetchWithAllPersonByName(SECURE_USER)).thenReturn(person);
	when(accountService.validateRemoveContactAccount(any(Person.class), any(ContactDTO.class)))
		.thenReturn(new ArrayList<String>());

	mockMvc
		.perform(MockMvcRequestBuilders.post("/removeContactAccount").sessionAttrs(sessionAttr).flashAttr(
			"contactDTO",
			contactDTO))
		.andExpect(status().is(200))
		.andExpect(view().name("contact"))
		.andExpect(model().attributeExists("contactDTO"));

	verify(personService, times(1)).findFetchWithAllPersonByName(any(String.class));
	verify(accountService, times(1)).validateRemoveContactAccount(any(Person.class), any(ContactDTO.class));
	verify(accountService, times(1)).removeContactAccount(any(Person.class), any(ContactDTO.class));
    }

    @Test
    public void testFinanceControllerGoToTransfer() throws Exception {

	when(personService.findFetchWithAllPersonByName(SECURE_USER)).thenReturn(person);

	mockMvc
		.perform(MockMvcRequestBuilders.post("/gotoTransfer").sessionAttrs(sessionAttr))
		.andExpect(status().is(200))
		.andExpect(view().name("transfer"))
		.andExpect(model().attributeExists("transferDTO"));

	verify(personService, times(1)).findFetchWithAllPersonByName(any(String.class));
    }

    @Test
    public void testFinanceControllerTransfer() throws Exception {
	TransferDTO transferDTO = new TransferDTO(1L, 2L, "virement", 1000.0);

	when(personService.findFetchWithAllPersonByName(SECURE_USER)).thenReturn(person);
	when(financeService.validateCreateTransaction(any(Person.class), any(TransferDTO.class)))
		.thenReturn(new ArrayList<String>());

	mockMvc
		.perform(MockMvcRequestBuilders.post("/transfer").sessionAttrs(sessionAttr).flashAttr("transferDTO",
			transferDTO))
		.andExpect(status().is(200))
		.andExpect(view().name("transfer"))
		.andExpect(model().attributeExists("transferDTO"));

	verify(personService, times(1)).findFetchWithAllPersonByName(any(String.class));
	verify(financeService, times(1)).validateCreateTransaction(any(Person.class), any(TransferDTO.class));
	verify(financeService, times(1)).createTransaction(any(Person.class), any(TransferDTO.class));
    }

    @Test
    public void testLoginControllerRegisterNewUser() throws Exception {
	when(financeService.validateCreateAuthorityUserPerson(any(String.class), any(String.class), any(String.class)))
		.thenReturn(new ArrayList<String>());
	when(financeService.createAuthorityUserPerson(any(String.class), any(String.class)))
		.thenReturn(person);

	mockMvc
		.perform(MockMvcRequestBuilders.post("/registerNewUser").sessionAttrs(sessionAttr).param(
			"username", SECURE_USER).param("password", PASSWORD).param("confirmPassword", PASSWORD))
		.andExpect(status().is(200))
		.andExpect(view().name("login"));

	verify(financeService, times(1)).validateCreateAuthorityUserPerson(any(String.class), any(String.class),
		any(String.class));
	verify(financeService, times(1)).createAuthorityUserPerson(any(String.class), any(String.class));
    }
}
