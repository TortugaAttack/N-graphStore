package com.oppsci.ngraphstore.web.rest.db;

import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oppsci.ngraphstore.web.role.Role;
import com.oppsci.ngraphstore.web.role.RoleController;
import com.oppsci.ngraphstore.web.user.User;
import com.oppsci.ngraphstore.web.user.UserController;

/**
 * Forwards REST query further to correct REST Controller
 * <ul>
 * <li>GET will be forwarded to SPARQL</li>
 * </ul>
 * 
 * @author f.conrads
 *
 */
@RestController
@RequestMapping(value = "/api")
public class ManagmentRestController {

	@Autowired
	UserController userController;

	@Autowired
	RoleController roleController;

	// TODO put logic in service not rest controller!
	@RequestMapping(value = "/auth/admin/api/createuser", method = RequestMethod.POST, headers = "Accept=application/json")
	public String addUser(@RequestParam(value = "username") String username,
			@RequestParam(value = "password") String password,
			@RequestParam(value = "isAdmin", required = false, defaultValue = "false") String isAdmin) {
		userController.addUser(username, password);
		
		User user = userController.getUserByName(username);
		Role roleU = roleController.getRoleByName("ROLE_USER");
		roleController.addRoleToUser(user.getId(), roleU);
		if (isAdmin.equals("true")) {
			Role role = roleController.getRoleByName("ROLE_ADMIN");
			roleController.addRoleToUser(user.getId(), role);
		}
		return "true";
	}

	@RequestMapping(value = "/auth/admin/api/deleteuser", method = RequestMethod.POST, headers = "Accept=application/json")
	public String deleteUser(@RequestParam(value = "username") String username) {
		Role role = roleController.getRoleByName("ROLE_ADMIN");
		User user = userController.getUserByName(username);
		if (!roleController.getUserRoles(user.getId()).contains(role)) {
			userController.deleteUser(user.getId());
		}
		return "true";
	}

	@RequestMapping(value = "/auth/admin/api/setuserrole", method = RequestMethod.POST, headers = "Accept=application/json")
	public String updateUserRole(@RequestParam(value = "username") String username,
			@RequestParam(value = "isAdmin") String isAdmin) {
		User user = userController.getUserByName(username);
		Role role = roleController.getRoleByName("ROLE_ADMIN");
		if (isAdmin.equals("true")) {
			if (!roleController.getUserRoles(user.getId()).contains(role))
				roleController.setUserAsAdmin(user.getId());
		} else {
			if (roleController.getUserRoles(user.getId()).contains(role)) {

				roleController.deleteRoleFromUser(user, role);
			}
		}
		return "true";
	}

	@RequestMapping(value = "/auth/admin/api/user", method = RequestMethod.GET, headers = "Accept=application/json")
	public User getUser(@RequestParam(value = "username") String username) {
		User user = userController.getUserByName(username);
		return user;
	}

	@RequestMapping(value = "/auth/admin/api/alluser", method = RequestMethod.GET, headers = "Accept=application/json")
	public List<User> getUser() {
		List<User> users = userController.getUser();
		return users;
	}

}
