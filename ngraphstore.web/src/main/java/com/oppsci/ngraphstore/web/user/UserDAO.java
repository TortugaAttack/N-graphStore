package com.oppsci.ngraphstore.web.user;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class UserDAO {

	private final static String GET_USER_BY_NAME = "SELECT * FROM USER WHERE USERNAME=:userName";
	private final static String GET_USER_BY_ID = "SELECT id, userName, password FROM USER WHERE ID=:id";
	private static final String INSERT_USER = "INSERT INTO USER(userName, password) SELECT * FROM (VALUES(:userName, :password)) WHERE NOT EXISTS ( SELECT userName FROM USER WHERE userName=:userName)";
	private static final String GET_ALL_USERS = "SELECT * FROM USER";
	private static final String DELETE_USER = "DELETE FROM USER WHERE id=:id";
	private static final String DELETE_USER_ROLES = "DELETE FROM USER_ROLES where user_id=:id";
	private static final String UPDATE_USER_PASSWORD = "UPDATE USER SET password=:password WHERE id=:id";

	private final NamedParameterJdbcTemplate template;

	public UserDAO(DataSource dataSource) {
		this.template = new NamedParameterJdbcTemplate(dataSource);
	}

	public int updateUser(User user) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("id", user.getId());
		parameters.addValue("password", user.getPassword());
		return this.template.update(UPDATE_USER_PASSWORD, parameters);
	}

	public int deleteUser(int id) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("id", id);
		int user =  this.template.update(DELETE_USER, parameters);
		int role = this.template.update(DELETE_USER_ROLES, parameters);
		return user<=0||role<=0?-1:1;
	}

	public User getUserByName(String userName) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("userName", userName);
		List<User> userList = this.template.query(GET_USER_BY_NAME, parameters, new UserRowMapper());
		if (userList.isEmpty())
			return null;
		// name is Unique
		return userList.get(0);
	}

	public User getUserByID(int id) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("id", id);
		List<User> userList = this.template.query(GET_USER_BY_ID, parameters, new UserRowMapper());
		if (userList.isEmpty())
			return null;
		// name is Unique
		return userList.get(0);
	}

	public List<User> getAllUsers() {
		return this.template.query(GET_ALL_USERS, new UserRowMapper());
	}

	public int addUser(String userName, String encode) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("userName", userName);
		parameters.addValue("password", encode);
		return this.template.update(INSERT_USER, parameters);
	}

}
