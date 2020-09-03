package com.kgovt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@Order(1)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http
		.authorizeRequests()
		.antMatchers("/resources/**", "/css/**", "/fonts/**", "/img/**", "/js/**", "/webjars/**").permitAll()
		.antMatchers("/").permitAll()
		.antMatchers("/admin/**").authenticated()
		.and().formLogin().loginPage("/admin/login").defaultSuccessUrl("/admin/applicationList", true)
		.permitAll()
		.and()
		.logout()
		.clearAuthentication(true)
		.deleteCookies("JSESSIONID")
		.invalidateHttpSession(true)
		.logoutRequestMatcher(new AntPathRequestMatcher("/admin/logout"))
		.logoutSuccessUrl("/admin/login").permitAll();
		
		http .csrf().disable();

	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}	
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();		
		provider.setUserDetailsService(userDetailsService);	
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
	  web.ignoring().antMatchers("/js/**","/resources/**");
	}
	

}
