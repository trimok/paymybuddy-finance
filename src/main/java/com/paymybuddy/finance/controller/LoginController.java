package com.paymybuddy.finance.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.paymybuddy.finance.model.Compte;
import com.paymybuddy.finance.model.Person;
import com.paymybuddy.finance.service.ILoginService;
import com.paymybuddy.finance.session.Context;

@Controller
@SessionAttributes(value = { "context" })
public class LoginController {
    @Autowired
    private ILoginService loginService;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    private static String authorizationRequestBaseUri = "oauth2/authorization";
    Map<String, String> oauth2AuthenticationUrls = new HashMap<>();

    // Initialization of the model
    private void initModel(Model model, Person person) {

	// Create private account + pay may buddy account for the person
	person.getComptesFrom().add(new Compte("Buddy : " + person.getName()));
	person.getComptesFrom().add(new Compte("BNP : " + person.getName()));
	person.getComptesTo().add(new Compte("Buddy : " + person.getName()));
	person.getComptesTo().add(new Compte("BNP : " + person.getName()));

	Context context = new Context();
	context.setPerson(person);
	model.addAttribute("context", context);
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
	    return "transfert";
	} else {
	    // Anormal call, return to login
	    return "login";
	}
    }

    @GetMapping("/login")
    public String login(Model model) {

	Iterable<ClientRegistration> clientRegistrations = null;

	ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository)
		.as(Iterable.class);
	if (type != ResolvableType.NONE &&
		ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
	    clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
	}

	clientRegistrations.forEach(registration -> oauth2AuthenticationUrls.put(registration.getClientName(),
		authorizationRequestBaseUri + "/" + registration.getRegistrationId()));

	// Create Specific attribute url for google, github
	String urlGithub = null, urlGoogle = null;
	for (String key : oauth2AuthenticationUrls.keySet()) {
	    if ("Google".equals(key)) {
		urlGoogle = oauth2AuthenticationUrls.get(key);
	    }
	    if ("GitHub".equals(key)) {
		urlGithub = oauth2AuthenticationUrls.get(key);
	    }
	}
	model.addAttribute("urlGoogle", urlGoogle);
	model.addAttribute("urlGithub", urlGithub);

	return "login";
    }
}
