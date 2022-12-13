package com.paymybuddy.finance.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.paymybuddy.finance.dto.UserLoginDTO;
import com.paymybuddy.finance.model.Person;
import com.paymybuddy.finance.security.SecureUser;
import com.paymybuddy.finance.service.IFinanceService;
import com.paymybuddy.finance.service.ILoginService;
import com.paymybuddy.finance.service.IPersonService;

@Controller
@SessionAttributes(value = { "person" })
public class LoginController {
    @Autowired
    private ILoginService loginService;

    @Autowired
    private IFinanceService financeService;

    @Autowired
    private IPersonService personService;

    @Autowired
    private Environment env;

    @Autowired
    private JdbcUserDetailsManager userDetailsManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Login Callback
    @GetMapping("/*")
    public String getUserInfo(Model model, Principal user, @ModelAttribute("person") Person personAttribute) {
	if (personAttribute.getName() != null) {
	    // Should never happen
	    return "login";
	} else {

	    // Authorization
	    Person person = loginService.getPersonFromPrincipal(user);

	    if (person != null) {
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

    @GetMapping(value = "/registerNewUser")
    public String goToRegisterNewUser() {
	return "registerNewUser";
    }

    @PostMapping(value = "/registerNewUser")
    public String registerNewUser(Model model, @RequestParam String username, @RequestParam String password,
	    @RequestParam String confirmPassword) {

	if (!password.equals(confirmPassword)) {
	    model.addAttribute("passwordsDontMatch", true);
	    model.addAttribute("username", username);
	    return "registerNewUser";
	}

	UserLoginDTO userLogin = new UserLoginDTO(username, password);
	userLogin.setPassword(passwordEncoder.encode(userLogin.getPassword()));

	model.addAttribute("username", username);

	if (userDetailsManager.userExists(username)) {
	    model.addAttribute("userAlreadyRegistered", true);
	    return "login";
	} else {
	    userDetailsManager.createUser(new SecureUser(userLogin));
	    model.addAttribute("userSuccessfullyRegistered", true);
	    return "login";
	}
    }
}
