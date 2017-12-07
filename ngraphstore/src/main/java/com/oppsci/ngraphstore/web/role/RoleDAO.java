package com.oppsci.ngraphstore.web.role;

import java.util.List;

import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.oppsci.ngraphstore.web.user.User;
import com.oppsci.ngraphstore.web.user.UserRowMapper;

public class RoleDAO {

	private final static String GET_USER_ROLES = "SELECT r.id, r.roleName FROM ROLE r, USER_ROLES ur WHERE ur.USER_ID=:id AND r.id=ur.ROLE_ID ";
	
	private final NamedParameterJdbcTemplate template;

	public RoleDAO(DataSource dataSource) {
			this.template = new NamedParameterJdbcTemplate(dataSource);
		}

	public void updateRole(Role Role) {

	}

	public void deleteRole(int id) {

	}

	public void addRole(Role role) {

	}

	public Role getRoleByName(String roleName) {
		return null;

	}

	public Role getRoleByID(int id) {
		return null;

	}

	@SuppressWarnings("unchecked")
	public List<Role> getAllRoles() {
		return null;

	}

	public List<Role> getUserRoles(int id) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("id", id);
		List<Role> roles = this.template.query(GET_USER_ROLES, parameters, new RoleRowMapper());
		return roles;
	}
}
