package com.oppsci.ngraphstore.web.root;

import org.apache.commons.configuration.CompositeConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.oppsci.ngraphstore.web.user.UserService;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = { "com.oppsci.ngraphstore.web.root" })
public class WebSecurity extends WebSecurityConfigurerAdapter {

	private static final String PROTECTION_PATTERN = "nraphstore.security.protectionPattern";
	protected static final String AUTH_METHOD = "ngraphstore.security.authMethod";

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private UserService userService;


	@Autowired
	private CompositeConfiguration config;
	

	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService).passwordEncoder(encoder);
	}


	@Override
	protected void configure(HttpSecurity http) throws Exception {
		String authMethod = config.getString(AUTH_METHOD);
		String[] protectionPattern=config.getStringArray(PROTECTION_PATTERN);
		switch (authMethod) {
		
		case "basic":
			http.authorizeRequests().antMatchers("/resources/**", "/registration").permitAll()
					.antMatchers(protectionPattern).authenticated().antMatchers("/auth/admin**")
					.hasAuthority("ROLE_ADMIN").anyRequest().permitAll().and().httpBasic().and().logout().permitAll()
					.logoutSuccessUrl("/login?logout").permitAll().logoutUrl("/logout").and().csrf().disable();
			break;
		case "none":
			http.authorizeRequests().antMatchers("/auth/admin", "/auth/settings", "/login", "/logout").denyAll().anyRequest().permitAll()
					.and().csrf().disable();
			break;
		case "form":
		default:
			http.authorizeRequests().antMatchers("/resources/**", "/registration").permitAll()
					.antMatchers(protectionPattern).authenticated().antMatchers("/auth/admin**")
					.hasAuthority("ROLE_ADMIN").anyRequest().permitAll().and().formLogin().loginPage("/login")
					.permitAll().and().logout().permitAll().logoutSuccessUrl("/login?logout").permitAll()
					.logoutUrl("/logout");
			break;
		}

	}

}
