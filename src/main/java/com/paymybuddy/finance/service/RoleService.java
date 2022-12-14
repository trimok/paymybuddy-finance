package com.paymybuddy.finance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paymybuddy.finance.repository.RoleRepository;

@Service
public class RoleService implements IRoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public void deleteAllRoles() {
	roleRepository.deleteAll();
	roleRepository.flush();
    }
}
