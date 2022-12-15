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
}
