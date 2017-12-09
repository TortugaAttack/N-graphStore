package com.oppsci.ngraphstore.web.role;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oppsci.ngraphstore.web.user.User;

@Service("roleService")
public class RoleService {

	@Autowired
	private RoleDAO dao;

	
	public Role getRoleByName(String name) {
		return dao.getRoleByName(name);
	}

	
	public Role getRoleByID(int id) {
		return dao.getRoleByID(id);
	}

	public int addRoleToUser(int userID, Role role) {
		return dao.addRoleToUser(userID, role);
	}
	
	public int deleteRoleFromUser(User user, Role role) {
		return dao.deleteRoleFromUser(user, role);
	}
	
	public int setUserAsAdmin(int userID) {
		Role role = dao.getRoleByName("ROLE_ADMIN");
		return dao.addRoleToUser(userID, role);
	}
	
	public List<Role> getUserRoles(int id) {
		return dao.getUserRoles(id);
	}

}
