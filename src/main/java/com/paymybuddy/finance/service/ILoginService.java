package com.paymybuddy.finance.service;

import java.security.Principal;

import com.paymybuddy.finance.modelmemory.PersonMemory;

public interface ILoginService {

    // OAuth2 / OIDC login
    PersonMemory getOauth2UserId(Principal user);

    // Basic login
    PersonMemory getUserId(Principal principal);
}