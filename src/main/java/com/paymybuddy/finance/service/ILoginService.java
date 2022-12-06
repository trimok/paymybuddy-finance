package com.paymybuddy.finance.service;

import java.security.Principal;

import com.paymybuddy.finance.model.Person;

public interface ILoginService {

    // OAuth2 / OIDC login
    Person getOauth2UserId(Principal user);

    // Basic login
    Person getUserId(Principal principal);
}