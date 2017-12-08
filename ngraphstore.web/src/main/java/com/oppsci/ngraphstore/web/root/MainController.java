package com.oppsci.ngraphstore.web.root;

import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {
	
	@Autowired
	public String authMethod;

	private void addCredentialInfo(ModelAndView model) {
		Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		Set<String> roles = new HashSet<String>();
		for(GrantedAuthority authority : authorities) {
			roles.add(authority.getAuthority());
		}
		
		boolean authenticated = roles.contains("ROLE_ADMIN") || roles.contains("ROLE_USER");
		model.addObject("authenticated", authenticated);
		model.addObject("isAdmin", roles.contains("ROLE_ADMIN"));
		model.addObject("authMethod", authMethod);
	}

	@RequestMapping(value = { "/", "index**" }, method = RequestMethod.GET)
	public ModelAndView defaultPage() {
		ModelAndView model = new ModelAndView();
		addCredentialInfo(model);

		model.setViewName("index");
		return model;

	}

	@RequestMapping(value = "/sparql-view", method = RequestMethod.GET)
	public ModelAndView sparqlPage() {
		ModelAndView model = new ModelAndView();
		addCredentialInfo(model);

		model.setViewName("sparql");
		return model;
	}

	@RequestMapping(value = "/auth/update", method = RequestMethod.GET)
	public ModelAndView updatePage(ModelMap model2, Principal principal) {
		ModelAndView model = new ModelAndView();
		addCredentialInfo(model);

		model.setViewName("update");
		return model;
	}

	@RequestMapping(value = "/auth/admin", method = RequestMethod.GET)
	public ModelAndView adminPage(ModelMap model2, Principal principal) {
		ModelAndView model = new ModelAndView();
		addCredentialInfo(model);
		model.setViewName("admin");
		return model;
	}

	@RequestMapping(value = "/auth/upload", method = RequestMethod.GET)
	public ModelAndView uploadPage(ModelMap model2, Principal principal) {
		ModelAndView model = new ModelAndView();
		addCredentialInfo(model);

		model.setViewName("upload");
		return model;
	}

	@RequestMapping(value = "/auth/settings", method = RequestMethod.GET)
	public ModelAndView settingsPage(ModelMap model2, Principal principal) {
		ModelAndView model = new ModelAndView();
		addCredentialInfo(model);

		model.setViewName("settings");

		return model;
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView login(ModelMap model2) {
		ModelAndView model = new ModelAndView();
		addCredentialInfo(model);

		model.setViewName("login");

		return model;

	}

	@RequestMapping("/logout")
	public String logoutUrl() {
		return "logout";
	}

	@RequestMapping(value = "/loginError", method = RequestMethod.GET)
	public String loginError(ModelMap model) {
		model.addAttribute("error", "true");
		return "login";

	}

}