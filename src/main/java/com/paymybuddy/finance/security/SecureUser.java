package com.paymybuddy.finance.security;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.paymybuddy.finance.dto.UserLoginDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author trimok
 *
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SecureUser implements UserDetails {

    /**
     * serialVersionUID
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * userLogin
     */
    private UserLoginDTO userLogin = null;

    /**
     * authorities
     */
    Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

    /*
     * 
     */
    public SecureUser(UserLoginDTO userLogin) {
	this.userLogin = userLogin;
    }

    /*
     * 
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
	return authorities;
    }

    /*
     * Adding an authority
     */
    public void addAuthority(GrantedAuthority authority) {
	authorities.add(authority);
    }

    /**
     * password
     */
    @Override
    public String getPassword() {
	return userLogin.getPassword();
    }

    /**
     * username
     */
    @Override
    public String getUsername() {
	return userLogin.getUsername();
    }

    /**
     * email
     */
    public String getEmail() {
	return userLogin.getEmail();
    }

    /**
     * isAccountNonExpired
     */
    @Override
    public boolean isAccountNonExpired() {
	return true;
    }

    /**
     * isAccountNonLocked
     */
    @Override
    public boolean isAccountNonLocked() {
	return true;
    }

    /**
     * isCredentialsNonExpired
     */
    @Override
    public boolean isCredentialsNonExpired() {
	return true;
    }

    /**
     * isEnabled
     */
    @Override
    public boolean isEnabled() {
	return true;
    }
}
