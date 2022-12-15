package com.paymybuddy.finance.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.paymybuddy.finance.model.Person;
import com.paymybuddy.finance.service.IFinanceService;
import com.paymybuddy.finance.service.ILoginService;
import com.paymybuddy.finance.service.IPersonService;

/**
 * @author trimok
 *
 */
@Controller
@SessionAttributes(value = { "person" })
public class LoginController {
    /**
     * The login service
     */
    @Autowired
    private ILoginService loginService;

    /**
     * The finance service
     */
    @Autowired
    private IFinanceService financeService;

    /**
     * The person service
     */
    @Autowired
    private IPersonService personService;

    /**
     * The environment
     */
    @Autowired
    private Environment env;

    /**
     * The UserDetailsManager
     */
    @Autowired
    private UserDetailsManager userDetailsManager;

    // Login Callback
    /**
     * General login callback for authentification
     * 
     * @param model           : the model
     * @param user            : the Principal
     * @param personAttribute : the Person session object
     * @return : the next page (login, home)
     */
    @GetMapping("/*")
    public String getUserInfo(Model model, Principal user, @ModelAttribute("person") Person personAttribute) {
	if (personAttribute.getName() != null) {
	    // Should never happen
	    return "home";
	} else {

	    // Authorization (Standard, OAuth2, OpenIDC)
	    Person person = loginService.getPersonFromPrincipal(user);

	    if (person != null) {
		// If needed (Oauth2, OpenIDC) Person initialization (creating account)
		person = financeService.initPerson(person);

		person = personService.findFetchWithAllPersonByName(person.getName());
		model.addAttribute("person", person);
		return "home";
	    } else {
		// Anormal call and/or anormal authorization, return to login
		// Should never happen
		return "login";
	    }
	}
    }

    /**
     * First connexion to login page
     * 
     * @param model : the model
     * @return : the login page
     */
    @GetMapping("/login")
    public String login(Model model) {

	// To avoid log errors
	model.addAttribute("person", new Person());

	model.addAttribute("urlGoogle", (String) env.getProperty("url.oauth2.authorization.google"));
	model.addAttribute("urlGithub", (String) env.getProperty("url.oauth2.authorization.github"));

	model.addAttribute("urlIconGoogle", (String) env.getProperty("url.icon.google"));
	model.addAttribute("urlIconGithub", (String) env.getProperty("url.icon.github"));

	return "login";
    }

    /**
     * Connexion to the registerNewUser page
     * 
     * @return : the registerNewUser page
     */
    @GetMapping(value = "/registerNewUser")
    public String goToRegisterNewUser() {
	return "registerNewUser";
    }

    /**
     * Registering of a new user
     * 
     * @param model           : the model
     * @param username        : the username
     * @param password        : the password
     * @param confirmPassword : the confirmationof the passwoed
     * @return : next page (registerNewUser or login)
     */
    @PostMapping(value = "/registerNewUser")
    public String registerNewUser(Model model, @RequestParam String username, @RequestParam String password,
	    @RequestParam String confirmPassword) {

	model.addAttribute("username", username);

	if (!password.equals(confirmPassword)) {
	    model.addAttribute("passwordsDontMatch", true);

	    return "registerNewUser";
	} else if (userDetailsManager.userExists(username)) {
	    model.addAttribute("userAlreadyRegistered", true);

	    return "login";
	} else {

	    // User secure creation
	    financeService.createSecurePerson(username, password);

	    model.addAttribute("userSuccessfullyRegistered", true);

	    // To avoid error message logs (need session attributes)
	    model.addAttribute("person", new Person());

	    return "login";
	}
    }
}
