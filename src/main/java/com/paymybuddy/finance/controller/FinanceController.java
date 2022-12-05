package com.paymybuddy.finance.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.paymybuddy.finance.model.Compte;
import com.paymybuddy.finance.model.Context;
import com.paymybuddy.finance.model.Person;
import com.paymybuddy.finance.model.Transaction;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@SessionAttributes(value = { "context", "person" })
public class FinanceController {

    // Initialization of the model
    public void initModel(Model model, Person person) {

	model.addAttribute("context", new Context());
	model.addAttribute("person", person);

	model.addAttribute("comptes", Compte.getComptes());

	// Create private account + pay may buddy account for the person
	person.getComptesFrom().add(new Compte("Buddy : " + person.getName()));
	person.getComptesFrom().add(new Compte("BNP : " + person.getName()));
	person.getComptesTo().add(new Compte("Buddy : " + person.getName()));
	person.getComptesTo().add(new Compte("BNP : " + person.getName()));
    }

    // Login
    @GetMapping("/")
    public String getUserInfo(Model model, Principal user) {
	Person person = null;

	if (user instanceof UsernamePasswordAuthenticationToken) {
	    // Basic login
	    person = getUserId(user);
	} else if (user instanceof OAuth2AuthenticationToken) {
	    // OAuth / OIDC login
	    person = getOauth2UserId(user);
	}

	// Initialization of the model with the person data
	initModel(model, person);

	return "transfert";
    }

    // Cast an id token from a DefaultOidcUser principal, himself get (after cast)
    // from an authentication token
    private OidcIdToken getIdToken(OAuth2AuthenticationToken authToken) {
	OAuth2User principal = authToken.getPrincipal();
	if (principal instanceof DefaultOidcUser) {
	    DefaultOidcUser oidcUser = (DefaultOidcUser) principal;
	    return oidcUser.getIdToken();
	}
	return null;
    }

    // OAuth2 / OIDC login
    private Person getOauth2UserId(Principal user) {
	Person person = new Person();

	// Authentication token
	OAuth2AuthenticationToken authToken = ((OAuth2AuthenticationToken) user);

	// Principal
	OAuth2User principal = authToken.getPrincipal();

	// OAuth2 infos
	Map<String, Object> userAttributes = null;
	if (authToken.isAuthenticated()) {
	    // Data principal
	    userAttributes = principal.getAttributes();
	} else {
	    log.error("Not Authenticated");
	}

	// OIDC Infos
	Map<String, Object> claims = null;
	OidcIdToken idToken = getIdToken(authToken);
	if (idToken != null) {
	    // Claims
	    claims = idToken.getClaims();

	    // Get the name / email infos from the claims
	    person.setName((String) claims.get("name"));
	    person.setEmail((String) claims.get("email"));

	    log.info("OAuth2 / OIDC lofin");

	} else {
	    // Only OAuth2
	    // Get the name info from the login name or login
	    String name;
	    if (userAttributes != null) {
		name = (String) userAttributes.get("name");
		if (name == null) {
		    name = (String) userAttributes.get("login");
		}
		// Email is very likely to be null, here
		String email = (String) userAttributes.get("email");

		person.setEmail(email);
		person.setName(name);
	    }
	    log.info("OAuth2, but No OIDC login");
	}

	log.info("Connection, name :" + person.getName() + ", email : " + person.getEmail());
	return person;
    }

    // Basic login
    private Person getUserId(Principal principal) {
	Person person = new Person();
	// Get the authentication token
	UsernamePasswordAuthenticationToken token = ((UsernamePasswordAuthenticationToken) principal);
	if (token.isAuthenticated()) {
	    // Get the principal
	    User user = (User) token.getPrincipal();
	    String userId = user.getUsername();

	    // Put name and email with the same userId
	    person.setName(userId);
	    person.setEmail(userId);

	    log.info("Basic login");
	    log.info("Connection, name :" + userId + ", email : " + userId);
	} else {
	    log.error("Not Authenticated");
	}
	return person;
    }

    @PostMapping("/transfert")
    public String transfert(Model model, @ModelAttribute("person") Person person,
	    @ModelAttribute("context") Context context) {
	person.getTransactions().add(new Transaction(context.getTransfertCompteFrom(), context.getTransfertCompteTo(),
		context.getDescription(), context.getTransfertAmount()));
	return "transfert";
    }

    @PostMapping("/addCompteTo")
    public String addConnection(Model model, @ModelAttribute("person") Person person,
	    @ModelAttribute("context") Context context) {
	person.getComptesTo().add(new Compte(context.getNewCompteTo()));
	return "transfert";
    }
}
