package com.paymybuddy.finance.security;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.paymybuddy.finance.dto.UserLoginDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SecureUser implements UserDetails {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private UserLoginDTO userLogin = null;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
	GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");

	return Arrays.asList(authority);
    }

    @Override
    public String getPassword() {
	return userLogin.getPassword();
    }

    @Override
    public String getUsername() {
	return userLogin.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
	return true;
    }

    @Override
    public boolean isAccountNonLocked() {
	return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
	return true;
    }

    @Override
    public boolean isEnabled() {
	return true;
    }
}
