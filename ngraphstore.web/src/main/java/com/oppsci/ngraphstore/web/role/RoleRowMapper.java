package com.oppsci.ngraphstore.web.role;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class RoleRowMapper implements RowMapper<Role> {

	@Override
	public Role mapRow(ResultSet resultSet, int rowId) throws SQLException {
		return new Role(resultSet.getInt(1), resultSet.getString(2));
	}


}
