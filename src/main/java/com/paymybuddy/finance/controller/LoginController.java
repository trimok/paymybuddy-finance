package com.paymybuddy.finance.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.paymybuddy.finance.model.Compte;
import com.paymybuddy.finance.model.Person;
import com.paymybuddy.finance.service.ILoginService;
import com.paymybuddy.finance.session.Context;

@Controller
@SessionAttributes(value = { "context", "person" })
public class LoginController {
    @Autowired
    private ILoginService loginService;

    @Autowired
    private Environment env;

    @ModelAttribute
    public Context getContext() {
	return new Context();
    }

    @ModelAttribute
    public Person getPerson() {
	return new Person();
    }

    // Initialization of the model
    private void initModel(Model model, Person person) {

	// Create private account + pay may buddy account for the person
	person.getComptesFrom().add(new Compte("Buddy : " + person.getName()));
	person.getComptesFrom().add(new Compte("BNP : " + person.getName()));
	person.getComptesTo().add(new Compte("Buddy : " + person.getName()));
	person.getComptesTo().add(new Compte("BNP : " + person.getName()));

	model.addAttribute("person", person);
    }

    // Login
    @GetMapping("/*")
    public String getUserInfo(Model model, Principal user) {
	Person person = null;

	// Get the Person Object from different type of login
	if (user instanceof UsernamePasswordAuthenticationToken) {
	    // Basic login
	    person = loginService.getUserId(user);
	} else if (user instanceof OAuth2AuthenticationToken) {
	    // OAuth / OIDC login
	    person = loginService.getOauth2UserId(user);
	}

	if (person != null) {
	    // Initialization of the model with the person data
	    initModel(model, person);
	    return "home";
	} else {
	    // Anormal call, return to login
	    return "login";
	}
    }

    @GetMapping("/login")
    public String login(Model model) {

	model.addAttribute("urlGoogle", (String) env.getProperty("url.oauth2.authorization.google"));
	model.addAttribute("urlGithub", (String) env.getProperty("url.oauth2.authorization.github"));

	model.addAttribute("urlIconGoogle", (String) env.getProperty("url.icon.google"));
	model.addAttribute("urlIconGithub", (String) env.getProperty("url.icon.github"));

	return "login";
    }
}
