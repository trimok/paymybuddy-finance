package com.paymybuddy.finance.service;

import java.security.Principal;

import com.paymybuddy.finance.security.SecureUser;

/**
 * @author trimok
 *
 */
public interface ILoginService {
    /**
     * Getting a Person object from a Principal
     * 
     * @param user : the principal
     * @return : a SecureUser object
     */
    SecureUser getSecureUserFromPrincipal(Principal user);

    /**
     * 
     * OAuth2 / OIDC login Getting a Person object from a Principal
     * 
     * @param user : the principal
     * @return : a SecureUser object
     */
    SecureUser getSecureUserFromOauth2OidcPrincipal(Principal user);

    /**
     * Getting a Person object from a Principal Basic login (login after
     * registration)
     * 
     * @param user : the principal
     * @return : a SecureUser object
     */
    SecureUser getSecureUserFromStandardPrincipal(Principal principal);
}