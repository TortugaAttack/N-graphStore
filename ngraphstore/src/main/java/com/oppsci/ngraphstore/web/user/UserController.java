package com.oppsci.ngraphstore.web.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder encoder;

	public List<User> getUser() {

		List<User> users = userService.getAllUsers();
		return users;
	}

	public User getUserByID(int id) {
		return userService.getUserByID(id);
	}

	public User getUserByName(String name) {
		return userService.getUserByName(name);
	}

	public int addUser(String userName, String password) {
		return userService.addUser(userName, password, encoder);
	}

	public int updateUser(User user) {
		return userService.updateUser(user);
	}

	public int deleteUser(int id) {
		return userService.deleteUser(id);
	}
}
