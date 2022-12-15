package com.paymybuddy.finance.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

/**
 * @author trimok
 *
 */
public class PayMyBuddyJdbcUserDetailsManager extends JdbcUserDetailsManager {

    /**
     * CREATE_AUTHORITY_QUERY
     */
    private final static String CREATE_AUTHORITY_QUERY = "insert into role (name, roles, person_id) values (?,?,?)";
    /**
     * CREATE_USER_QUERY
     */
    private final static String CREATE_USER_QUERY = "insert into person (name, email, password, enabled) values (?,?,?,?)";
    /**
     * USER_ID_EXISTS_QUERY
     */
    private final static String USER_ID_EXISTS_QUERY = "select id from person where name = ?";
    /**
     * USERNAME_EXISTS_QUERY
     */
    private final static String USERNAME_EXISTS_QUERY = "select name from person where name = ?";
    /**
     * USERS_BY_USERNAME_QUERY
     */
    private final static String USERS_BY_USERNAME_QUERY = "select name, password, enabled from person where name = ?";
    /**
     * AUTHORITIES_BY_USERNAME_QUERY
     */
    private final static String AUTHORITIES_BY_USERNAME_QUERY = "select name, roles from role where name = ?";

    /**
     * Constructor
     */
    public PayMyBuddyJdbcUserDetailsManager() {
	setCreateUserSql(CREATE_USER_QUERY);
	setUserExistsSql(USERNAME_EXISTS_QUERY);
	setUsersByUsernameQuery(USERS_BY_USERNAME_QUERY);
	setAuthoritiesByUsernameQuery(AUTHORITIES_BY_USERNAME_QUERY);
    }

    /**
     * Creating user
     */
    @Override
    public void createUser(final UserDetails user) {
	getJdbcTemplate().update(CREATE_USER_QUERY, (ps) -> {
	    ps.setString(1, user.getUsername());
	    ps.setString(2, user.getUsername());
	    ps.setString(3, user.getPassword());
	    ps.setBoolean(4, user.isEnabled());
	});
	if (getEnableAuthorities()) {
	    insertUserAuthorities(user);
	}
    }

    /**
     * Create authority
     * 
     * @param user : the user
     */
    private void insertUserAuthorities(UserDetails user) {
	Long personId = getJdbcTemplate().queryForObject(USER_ID_EXISTS_QUERY, Long.class, user.getUsername());

	for (GrantedAuthority auth : user.getAuthorities()) {
	    getJdbcTemplate().update(CREATE_AUTHORITY_QUERY, user.getUsername(), auth.getAuthority(), personId);
	}
    }
}
