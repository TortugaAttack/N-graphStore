package com.oppsci.ngraphstore.web.user;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class UserRowMapper implements RowMapper<User> {

	@Override
	public User mapRow(ResultSet resultSet, int rowId) throws SQLException {
		return new User(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3));

	}



}
