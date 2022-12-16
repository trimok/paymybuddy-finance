package com.paymybuddy.finance.service;

import static com.paymybuddy.finance.constants.Constants.GENERIC_PASSWORD;
import static com.paymybuddy.finance.constants.Constants.ROLE_OAUTH2_USER;
import static com.paymybuddy.finance.constants.Constants.ROLE_OIDC_USER;
import static com.paymybuddy.finance.constants.Constants.ROLE_USER;

import java.security.Principal;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.paymybuddy.finance.dto.UserLoginDTO;
import com.paymybuddy.finance.security.SecureUser;

import lombok.extern.slf4j.Slf4j;

/**
 * @author trimok
 *
 */
@Service
@Slf4j
public class LoginService implements ILoginService {

    /**
     * 
     * Get a identification token from an authentication token
     * 
     * @param authToken : the authorization token
     * @return : the identifiation token
     */
    private OidcIdToken getIdToken(OAuth2AuthenticationToken authToken) {
	OAuth2User principal = authToken.getPrincipal();
	if (principal instanceof DefaultOidcUser) {
	    DefaultOidcUser oidcUser = (DefaultOidcUser) principal;
	    return oidcUser.getIdToken();
	}
	return null;
    }

    /**
     * Building a Person Object from a Principal
     */
    @Override
    public SecureUser getSecureUserFromPrincipal(Principal user) {
	SecureUser secureUser = new SecureUser();

	// Get the Person Object from different type of login
	if (user instanceof UsernamePasswordAuthenticationToken) {
	    // Basic login
	    secureUser = getSecureUserFromStandardPrincipal(user);
	} else if (user instanceof OAuth2AuthenticationToken) {
	    // OAuth / OIDC login
	    secureUser = getSecureUserFromOauth2OidcPrincipal(user);
	}
	return secureUser;
    }

    /**
     * 
     * OAuth2 / OIDC login Getting a Person object from a Principal
     * 
     * @param user
     * @return
     */
    @Override
    public SecureUser getSecureUserFromOauth2OidcPrincipal(Principal user) {
	SecureUser secureUser = new SecureUser();
	UserLoginDTO userLogin = new UserLoginDTO();

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
	    userLogin.setUsername((String) claims.get("name"));
	    userLogin.setEmail((String) claims.get("email"));
	    userLogin.setPassword(GENERIC_PASSWORD);

	    secureUser.addAuthority(new SimpleGrantedAuthority(ROLE_OIDC_USER));

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

		userLogin.setEmail(email);
		userLogin.setUsername(name);
		userLogin.setPassword(GENERIC_PASSWORD);
		secureUser.addAuthority(new SimpleGrantedAuthority(ROLE_OAUTH2_USER));
	    }
	    log.info("OAuth2, but No OIDC login");
	}

	log.info("Connection, name :" + userLogin.getUsername() + ", email : " + userLogin.getEmail());

	secureUser.setUserLogin(userLogin);
	return secureUser;
    }

    /**
     * Getting a Person object from a Principal Basic login (login after
     * registration)
     * 
     * @param principal : the principal
     * @return : a Person object
     */
    @Override
    public SecureUser getSecureUserFromStandardPrincipal(Principal principal) {
	SecureUser secureUser = new SecureUser();
	UserLoginDTO userLogin = new UserLoginDTO();

	// Get the authentication token
	UsernamePasswordAuthenticationToken token = ((UsernamePasswordAuthenticationToken) principal);
	if (token.isAuthenticated()) {
	    // Get the principal
	    User user = (User) token.getPrincipal();
	    String userId = user.getUsername();

	    // Put name and email with the same userId
	    userLogin.setUsername(userId);
	    userLogin.setEmail(userId);

	    secureUser.addAuthority(new SimpleGrantedAuthority(ROLE_USER));

	    log.info("Basic login");
	    log.info("Connection, name :" + userId + ", email : " + userId);
	} else {
	    log.error("Not Authenticated");
	    return null;
	}
	secureUser.setUserLogin(userLogin);
	return secureUser;
    }
}
