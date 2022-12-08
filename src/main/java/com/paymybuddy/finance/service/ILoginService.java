package com.paymybuddy.finance.service;

import java.security.Principal;

import com.paymybuddy.finance.modelmemory.PersonMemory;

public interface ILoginService {
    PersonMemory getPersonFromPrincipal(Principal user);
}