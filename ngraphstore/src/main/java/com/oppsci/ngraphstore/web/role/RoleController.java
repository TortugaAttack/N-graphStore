package com.oppsci.ngraphstore.web.role;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class RoleController {
	
	@Autowired
	RoleService roleService;
	
	public List<Role> getRole() {
		
		List<Role> roles = roleService.getAllRoles();
		return roles;
	}
 
	public Role getRoleByID(int id) {
		return roleService.getRoleByID(id);
	}
	
	public Role getRoleByName(String name) {
		return roleService.getRoleByName(name);
	}
	
	
	public void addRole(Role role) {	
		if(role.getId()==0)
		{
			roleService.addRole(role);
		}
		else
		{	
			roleService.updateRole(role);
		}
	}
 
	public void updateRole(Role role) {
		 roleService.updateRole(role);
	}
 
	public void deleteRole(int id) {
		roleService.deleteRole(id);
 
	}	
}
