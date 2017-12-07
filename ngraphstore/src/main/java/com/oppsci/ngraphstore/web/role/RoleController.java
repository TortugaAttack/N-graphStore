package com.oppsci.ngraphstore.web.role;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.oppsci.ngraphstore.web.user.User;

@Controller
public class RoleController {
	
	@Autowired
	RoleService roleService;
	
	public Role getRoleByID(int id) {
		return roleService.getRoleByID(id);
	}
	
	public Role getRoleByName(String name) {
		return roleService.getRoleByName(name);
	}
	
	public int addRoleToUser(int userID, Role role) {
		return roleService.addRoleToUser(userID, role);
	}
	
	public int deleteRoleFromUser(User user, Role role) {
		return roleService.deleteRoleFromUser(user, role);
	}
	
	public int setUserAsAdmin(int userID) {
		return roleService.setUserAsAdmin(userID);
	}
	
	public List<Role> getUserRoles(int id) {
		return roleService.getUserRoles(id);
	}
}
