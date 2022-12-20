package com.paymybuddy.finance;

import static com.paymybuddy.finance.constants.Constants.AUTHORITY_OAUTH2_USER;
import static com.paymybuddy.finance.constants.Constants.AUTHORITY_OIDC_USER;
import static com.paymybuddy.finance.constants.Constants.PAY_MY_BUDDY_GENERIC_USER;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.paymybuddy.finance.model.Person;
import com.paymybuddy.finance.service.IAccountService;
import com.paymybuddy.finance.service.IBankService;
import com.paymybuddy.finance.service.IFinanceService;
import com.paymybuddy.finance.service.IPersonService;
import com.paymybuddy.finance.service.ITransactionService;

@ContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@TestMethodOrder(value = org.junit.jupiter.api.MethodOrderer.OrderAnnotation.class)
public class SecurityTest {

    private static final String REGISTERING_USER = "titi@titi.com";
    private static final String REGISTERING_PASSWORD = "titi";

    private final static String OAUTH2_USER = "trimok";
    private final static String OAUTH2_PASSWORD = "password";

    private final static String OIDC_USER = "google.googelisant";
    private final static String OIDC_PASSWORD = "password";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserDetailsManager userDetailsManager;

    @Autowired
    IFinanceService financeService;

    @Autowired
    IPersonService personService;

    @Autowired
    IBankService bankService;

    @Autowired
    IAccountService accountService;

    @Autowired
    ITransactionService transactionService;

    private Map<String, Object> sessionAttrs = null;

    @BeforeEach
    public void beforeEach() {
	financeService.deleteAll();
	financeService.initApplication();
    }

    @AfterEach
    public void afterEach() {
	financeService.deleteAll();
    }

    @Test
    public void testLoginPage() throws Exception {
	mockMvc.perform(get("/login")).andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void testLoginUser() throws Exception {

	mockMvc.perform(formLogin("/login").user(PAY_MY_BUDDY_GENERIC_USER).password(
		"password")).andExpect(authenticated());

    }

    @Test
    public void testRegisterNewUser() throws Exception {

	mockMvc.perform(
		MockMvcRequestBuilders.post("/registerNewUser")
			.param("username", REGISTERING_USER).param("password", REGISTERING_PASSWORD)
			.param("confirmPassword", REGISTERING_PASSWORD))
		.andExpect(status().isOk());

	mockMvc.perform(formLogin("/login").user(REGISTERING_USER).password(
		REGISTERING_PASSWORD)).andExpect(authenticated());

    }

    @Test
    @WithMockUser(username = OAUTH2_USER, authorities = { AUTHORITY_OAUTH2_USER })
    public void testOAuth2User() throws Exception {

	financeService.createAuthorityPerson(OAUTH2_USER, OAUTH2_PASSWORD, AUTHORITY_OAUTH2_USER);
	Person person = personService.findFetchWithAllPersonByName(OAUTH2_USER);

	sessionAttrs = new HashMap<String, Object>();
	sessionAttrs.put("person", person);

	mockMvc.perform(
		MockMvcRequestBuilders.post("/gotoContact").sessionAttrs(sessionAttrs))
		.andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = OIDC_USER, authorities = { AUTHORITY_OIDC_USER })
    public void testOidcUser() throws Exception {

	financeService.createAuthorityPerson(OIDC_USER, OIDC_PASSWORD, AUTHORITY_OIDC_USER);
	Person person = personService.findFetchWithAllPersonByName(OIDC_USER);

	sessionAttrs = new HashMap<String, Object>();
	sessionAttrs.put("person", person);

	mockMvc.perform(
		MockMvcRequestBuilders.post("/gotoContact").sessionAttrs(sessionAttrs))
		.andExpect(status().isOk());
    }
}
