package com.paymybuddy.finance.service;

import java.security.Principal;

import com.paymybuddy.finance.model.Person;

/**
 * @author trimok
 *
 */
public interface ILoginService {
    /**
     * Getting a Person object from a Principal
     * 
     * @param user : the principal
     * @return : a Person object
     */
    Person getPersonFromPrincipal(Principal user);
}