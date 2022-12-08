package com.paymybuddy.finance.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.paymybuddy.finance.modelmemory.CompteMemory;
import com.paymybuddy.finance.modelmemory.PersonMemory;
import com.paymybuddy.finance.service.ILoginService;
import com.paymybuddy.finance.session.Context;

@Controller
@SessionAttributes(value = { "context", "person" })
public class LoginController {
    @Autowired
    private ILoginService loginService;

    @Autowired
    private Environment env;

    // Initialization of the model
    private void initModel(Model model, PersonMemory person) {

	// Create private account + pay may buddy account for the person
	person.getComptesFrom().add(new CompteMemory("Buddy : " + person.getName()));
	person.getComptesFrom().add(new CompteMemory("BNP : " + person.getName()));
	person.getComptesTo().add(new CompteMemory("Buddy : " + person.getName()));
	person.getComptesTo().add(new CompteMemory("BNP : " + person.getName()));

	model.addAttribute("person", person);
	model.addAttribute("context", new Context());
    }

    // Login
    @GetMapping("/*")
    public String getUserInfo(Model model, Principal user) {
	// Anormal call
	if (model.getAttribute("person") != null) {
	    return "login";
	}

	// Authorization
	PersonMemory person = loginService.getPersonFromPrincipal(user);

	if (person != null) {
	    // Initialization of the model with the person data
	    initModel(model, person);
	    return "home";
	} else {
	    // Anormal call and/or anormal authorization, return to login
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
