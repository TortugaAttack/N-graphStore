package com.oppsci.ngraphstore.web.root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.oppsci.ngraphstore.web.user.UserService;

@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = { "com.oppsci.ngraphstore.web.root" })
public class WebSecurity extends WebSecurityConfigurerAdapter {

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private UserService userService;

	// @Override
	// protected void configure(HttpSecurity http) throws Exception {
	// http.authorizeRequests().antMatchers("/resources/**",
	// "/registration").permitAll().antMatchers("/auth/**")
	// .authenticated().anyRequest().permitAll().and().formLogin().loginPage("/login").permitAll().and()
	// .logout().permitAll();
	// }

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService).passwordEncoder(encoder);
	}

	@Autowired
	private String authMethod;

	@Autowired
	private String[] protectionPattern;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		switch (authMethod) {
		case "form":
			http.authorizeRequests().antMatchers("/resources/**", "/registration").permitAll()
					.antMatchers(protectionPattern).authenticated().antMatchers("/auth/admin**")
					.hasAuthority("ROLE_ADMIN").anyRequest().permitAll().and().formLogin().loginPage("/login")
					.permitAll().and().logout().permitAll().logoutSuccessUrl("/login?logout").permitAll()
					.logoutUrl("/logout");
			break;
		case "basic":
			http.authorizeRequests().antMatchers("/resources/**", "/registration").permitAll()
					.antMatchers(protectionPattern).authenticated().antMatchers("/auth/admin**")
					.hasAuthority("ROLE_ADMIN").anyRequest().permitAll().and().httpBasic().and().logout().permitAll()
					.logoutSuccessUrl("/login?logout").permitAll().logoutUrl("/logout").and().csrf().disable();
			break;
		case "none":
			http.authorizeRequests().antMatchers("/auth/admin", "/auth/settings", "/login", "/logout").denyAll().anyRequest().permitAll()
					.and().csrf().disable();
		}

	}

}
