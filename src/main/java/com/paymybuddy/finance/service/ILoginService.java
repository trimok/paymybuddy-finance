package com.paymybuddy.finance.service;

import java.security.Principal;

import com.paymybuddy.finance.model.Person;

public interface ILoginService {
    Person getPersonFromPrincipal(Principal user);
}