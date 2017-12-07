package com.oppsci.ngraphstore.web.role;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("roleService")
public class RoleService {

	@Autowired
	private RoleDAO dao;

	@Transactional
	public void updateRole(Role role) {
		dao.updateRole(role);
	}

	@Transactional
	public void deleteRole(int id) {
		dao.deleteRole(id);
	}

	@Transactional
	public void addRole(Role role) {
		dao.addRole(role);
	}

	@Transactional
	public Role getRoleByName(String name) {
		return dao.getRoleByName(name);
	}

	@Transactional
	public Role getRoleByID(int id) {
		return dao.getRoleByID(id);
	}

	@Transactional
	public List<Role> getAllRoles() {
		return dao.getAllRoles();
	}

}
