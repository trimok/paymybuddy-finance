package com.paymybuddy.finance.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class PayMyBuddySecurityConfig {

    @Bean
    public UserDetailsService users() {
	// The builder will ensure the passwords are encoded before saving in memory
	UserDetails user = User.builder()
		.username("person@person.mail")
		.password(passwordEncoder().encode("person123"))
		.roles("USER")
		.build();
	UserDetails admin = User.builder()
		.username("admin@admin.mail")
		.password(passwordEncoder().encode("admin123"))
		.roles("USER", "ADMIN")
		.build();
	return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	http.authorizeHttpRequests()
		.requestMatchers("/admin").hasRole("ADMIN")
		.requestMatchers("/user").hasRole("USER")
		.anyRequest().authenticated()
		.and()
		.formLogin()
		.and()
		.oauth2Login();

	return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
	return new BCryptPasswordEncoder();
    }
}
