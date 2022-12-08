package com.paymybuddy.finance.service;

import java.security.Principal;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.paymybuddy.finance.modelmemory.PersonMemory;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LoginService implements ILoginService {

    // Get a identification token from an authentication token
    private OidcIdToken getIdToken(OAuth2AuthenticationToken authToken) {
	OAuth2User principal = authToken.getPrincipal();
	if (principal instanceof DefaultOidcUser) {
	    DefaultOidcUser oidcUser = (DefaultOidcUser) principal;
	    return oidcUser.getIdToken();
	}
	return null;
    }

    // OAuth2 / OIDC login
    @Override
    public PersonMemory getOauth2UserId(Principal user) {
	PersonMemory person = new PersonMemory();

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
	    return null;
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

	    log.info("OAuth2 / OIDC login");

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
    @Override
    public PersonMemory getUserId(Principal principal) {
	PersonMemory person = new PersonMemory();
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
	    return null;
	}
	return person;
    }
}