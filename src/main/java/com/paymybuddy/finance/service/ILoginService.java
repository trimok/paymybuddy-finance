package com.paymybuddy.finance.service;

import java.security.Principal;

import com.paymybuddy.finance.security.PayMyBuddyUserDetails;

/**
 * @author trimok
 *
 */
public interface ILoginService {
    /**
     * Getting a Person object from a Principal
     * 
     * @param user : the principal
     * @return : a PayMyBuddyUserDetails object
     */
    PayMyBuddyUserDetails getUserDetailsUserFromPrincipal(Principal user);

    /**
     * 
     * OAuth2 / OIDC login Getting a Person object from a Principal
     * 
     * @param user : the principal
     * @return : a PayMyBuddyUserDetails object
     */
    PayMyBuddyUserDetails getUserDetailsFromOauth2OidcPrincipal(Principal user);

    /**
     * Getting a Person object from a Principal Basic login (login after
     * registration)
     * 
     * @param user : the principal
     * @return : a PayMyBuddyUserDetails object
     */
    PayMyBuddyUserDetails getUserDetailsFromStandardPrincipal(Principal principal);
}