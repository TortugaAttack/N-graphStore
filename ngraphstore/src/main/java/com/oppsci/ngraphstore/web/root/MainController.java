package com.oppsci.ngraphstore.web.root;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {

	@RequestMapping(value = { "/", "index**" }, method = RequestMethod.GET)
	public ModelAndView defaultPage() {
		ModelAndView model = new ModelAndView();
		model.setViewName("index");
		return model;

	}

	@RequestMapping(value = "/sparql-view", method = RequestMethod.GET)
	public ModelAndView sparqlPage() {
		ModelAndView model = new ModelAndView();
		model.setViewName("sparql");
		return model;
	}

	@RequestMapping(value = "/auth/update", method = RequestMethod.GET)
	public ModelAndView updatePage(ModelMap model2, Principal principal) {
		ModelAndView model = new ModelAndView();
		model.setViewName("update");
		return model;
	}

	@RequestMapping(value = "/auth/upload", method = RequestMethod.GET)
	public ModelAndView uploadPage(ModelMap model2, Principal principal) {
		ModelAndView model = new ModelAndView();
		model.setViewName("upload");
		return model;
	}

	@RequestMapping(value = "/auth/settings", method = RequestMethod.GET)
	public ModelAndView settingsPage(ModelMap model2, Principal principal) {
		ModelAndView model = new ModelAndView();
		model.setViewName("settings");
		return model;
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(ModelMap model) {

		return "login";

	}

	@RequestMapping(value = "/loginError", method = RequestMethod.GET)
	public String loginError(ModelMap model) {
		model.addAttribute("error", "true");
		return "login";

	}

}