package com.oppsci.ngraphstore.web.role;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import com.oppsci.ngraphstore.web.user.User;

public class RoleDAO {

	private final static String GET_USER_ROLES = "SELECT r.id, r.roleName FROM ROLE r, USER_ROLES ur WHERE ur.USER_ID=:id AND r.id=ur.ROLE_ID ";

	private static final String GET_ROLE_BY_NAME = "SELECT * FROM ROLE WHERE roleName=:roleName";

	private static final String GET_ROLE_BY_ID = "SELECT * FROM ROLE WHERE id=:id";

	private static final String ADD_ROLE_TO_USER = "INSERT INTO USER_ROLES(user_id, role_id) SELECT * FROM (VALUES(:user_id, :role_id)) WHERE NOT EXISTS ( SELECT * FROM USER_ROLES WHERE user_id=:user_id AND role_id=:role_id)" ;

	private static final String DELETE_ROLE_FROM_USER = "DELETE FROM USER_ROLES WHERE user_id=:user_id AND role_id=:role_id";

	private final NamedParameterJdbcTemplate template;

	public RoleDAO(DataSource dataSource) {
		this.template = new NamedParameterJdbcTemplate(dataSource);
	}

	public Role getRoleByName(String roleName) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("roleName", roleName);
		List<Role> roleList = this.template.query(GET_ROLE_BY_NAME, parameters, new RoleRowMapper());
		if (roleList.isEmpty())
			return null;
		// name is Unique
		return roleList.get(0);

	}

	public Role getRoleByID(int id) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("id", id);
		List<Role> roleList = this.template.query(GET_ROLE_BY_ID, parameters, new RoleRowMapper());
		if (roleList.isEmpty())
			return null;
		// name is Unique
		return roleList.get(0);
	}

	public int addRoleToUser(int userID, Role role) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("user_id", userID);
		parameters.addValue("role_id", role.getId());
		return this.template.update(ADD_ROLE_TO_USER, parameters);
	}
	
	public int deleteRoleFromUser(User user, Role role) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("user_id", user.getId());
		parameters.addValue("role_id", role.getId());
		return this.template.update(DELETE_ROLE_FROM_USER, parameters);		
	}

	public List<Role> getUserRoles(int id) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("id", id);
		List<Role> roles = this.template.query(GET_USER_ROLES, parameters, new RoleRowMapper());
		return roles;
	}
}
