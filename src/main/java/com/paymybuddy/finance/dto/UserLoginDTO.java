package com.paymybuddy.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author trimok
 *
 */
@Getter
@Setter
/**
 * @NoArgsConstructor
 */
@NoArgsConstructor
/**
 * @param username : username
 * @param password : password
 * @param password : email
 */
@AllArgsConstructor
public class UserLoginDTO {

    /**
     * username
     */
    private String username;
    /**
     * password
     */
    private String password;
    /**
     * email
     */
    private String email;

    /**
     * 
     * Constructor
     * 
     * @param username : username
     * @param password : password
     */
    public UserLoginDTO(String username, String password) {
	super();
	this.username = username;
	this.password = password;
    }

}
