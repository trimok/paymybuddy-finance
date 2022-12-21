package com.paymybuddy.finance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.paymybuddy.finance.repository.RoleRepository;

/**
 * @author trimok
 *
 */
@Service
public class RoleService implements IRoleService {

    /**
     * roleRepository
     */
    @Autowired
    private RoleRepository roleRepository;

    /**
     * 
     * Constructor for tests
     * 
     * @param roleRepository
     */
    @Autowired
    public RoleService(RoleRepository roleRepository) {
	super();
	this.roleRepository = roleRepository;
    }

    /**
     * deleteAllRoles
     */
    @Override
    @Transactional(readOnly = true, noRollbackFor = Exception.class)
    public void deleteAllRoles() {
	roleRepository.deleteAll();
	roleRepository.flush();
    }
}
