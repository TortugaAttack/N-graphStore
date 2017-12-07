package com.oppsci.ngraphstore.web.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
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
	
	
	public void addUser(User user) {	
		if(user.getId()==0)
		{
			userService.addUser(user);
		}
		else
		{	
			userService.updateUser(user);
		}
	}
 
	public void updateUser(User user) {
		 userService.updateUser(user);
	}
 
	public void deleteUser(int id) {
		userService.deleteUser(id);
 
	}	
}
