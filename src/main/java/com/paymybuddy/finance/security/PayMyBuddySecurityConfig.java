package com.paymybuddy.finance.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = { "com.paymybuddy.finance" })
public class PayMyBuddySecurityConfig {

    @Autowired
    public DataSource dataSource;

    @Bean
    JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource) {
	JdbcUserDetailsManager jdbcUserDetailsManager = new PayMyBuddyJdbcUserDetailsManager();
	jdbcUserDetailsManager.setDataSource(dataSource);

	return jdbcUserDetailsManager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

	http.authorizeHttpRequests()
		.requestMatchers("/registerNewUser", "/error").permitAll()
		.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
		.anyRequest().hasAnyAuthority("ROLE_USER")
		.and().formLogin()
		.loginPage("/login").failureUrl("/login?error=true")
		.permitAll()
		.and().oauth2Login().loginPage("/login").failureUrl("/login?error=true").permitAll()
		.and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
		.logoutSuccessUrl("/login?logout=true")
		.deleteCookies("JSESSIONID")
		.invalidateHttpSession(true).permitAll();

	return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
	return new BCryptPasswordEncoder();
    }
}
