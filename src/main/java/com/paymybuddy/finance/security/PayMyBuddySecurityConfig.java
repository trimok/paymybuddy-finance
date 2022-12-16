package com.paymybuddy.finance.security;

import static com.paymybuddy.finance.constants.Constants.ROLE_OAUTH2_USER;
import static com.paymybuddy.finance.constants.Constants.ROLE_OIDC_USER;
import static com.paymybuddy.finance.constants.Constants.ROLE_USER;

import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * @author trimok
 *
 */
@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = { "com.paymybuddy.finance" })
public class PayMyBuddySecurityConfig {

    /**
     * The datasource
     */
    @Autowired
    public DataSource dataSource;

    /**
     * The specific UserDetailsManager
     * 
     * @param dataSource : the datasource
     * @return : the PayMyBuddyJdbcUserDetailsManager
     */
    @Bean
    JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource) {
	JdbcUserDetailsManager jdbcUserDetailsManager = new PayMyBuddyJdbcUserDetailsManager();
	jdbcUserDetailsManager.setDataSource(dataSource);

	return jdbcUserDetailsManager;
    }

    /**
     * Configuring the filter chain
     * 
     * @param http : the http object
     * @return : SecurityFilterChain
     * @throws Exception : any exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

	http.authorizeHttpRequests()
		.requestMatchers("/registerNewUser", "/error").permitAll()
		.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
		.anyRequest().hasAnyAuthority(ROLE_USER, ROLE_OIDC_USER, ROLE_OAUTH2_USER)
		.and().formLogin()
		.loginPage("/login").failureUrl("/login?error=true")
		.permitAll()
		.and().oauth2Login().userInfoEndpoint().userAuthoritiesMapper(this.userAuthoritiesMapper())
		.and().loginPage("/login").failureUrl("/login?error=true").permitAll()
		.and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
		.logoutSuccessUrl("/login?logout=true")
		.deleteCookies("JSESSIONID")
		.invalidateHttpSession(true).permitAll()
		.and().csrf().disable();

	return http.build();
    }

    private GrantedAuthoritiesMapper userAuthoritiesMapper() {
	return (authorities) -> {
	    Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

	    authorities.forEach(authority -> {
		if (OidcUserAuthority.class.isInstance(authority)) {
		    mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_OIDC_USER"));
		} else if (OAuth2UserAuthority.class.isInstance(authority)) {
		    mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_OAUTH2_USER"));
		}
	    });

	    return mappedAuthorities;
	};
    }

    /**
     * Password encoder
     * 
     * @return : a BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
	return new BCryptPasswordEncoder();
    }
}
