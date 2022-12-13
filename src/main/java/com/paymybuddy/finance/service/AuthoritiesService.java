package com.paymybuddy.finance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paymybuddy.finance.repository.AuthoritiesRepository;

@Service
public class AuthoritiesService implements IAuthoritiesService {

    @Autowired
    private AuthoritiesRepository authoritiesRepository;

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public void deleteAllAuthorities() {
	authoritiesRepository.deleteAll();
	authoritiesRepository.flush();
    }
}
