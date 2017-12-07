package com.oppsci.ngraphstore.web.user;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class UserDAO {

    private final static String GET_USER_BY_NAME = "SELECT * FROM USER WHERE USERNAME=:userName";
    private final static String GET_USER_BY_ID = "SELECT id, userName, password FROM USER WHERE ID=:id";

    
    
	private final NamedParameterJdbcTemplate template;


	public UserDAO(DataSource dataSource) {
		this.template = new NamedParameterJdbcTemplate(dataSource);
	}

	public void updateUser(User user) {

	}

	public void deleteUser(int id) {

	}

	public void addUser(User user) {

	}

	public User getUserByName(String userName) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("userName", userName);
		List<User> userList = this.template.query(GET_USER_BY_NAME, parameters, new UserRowMapper());
		if(userList.isEmpty())
			return null;
		//name is Unique
		return userList.get(0);
	}

	public User getUserByID(int id) {
		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("id", id);
		List<User> userList = this.template.query(GET_USER_BY_ID, parameters, new UserRowMapper());
		if(userList.isEmpty())
			return null;
		//name is Unique
		return userList.get(0);
	}

	@SuppressWarnings("unchecked")
	public List<User> getAllUsers() {
		return null;
	}

}
