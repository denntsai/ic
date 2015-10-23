package com.icecold.types;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class OrderRowMapper implements RowMapper {
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Order(rs.getLong("customer"), rs.getString("flavor"), rs.getInt("quantity"),
                rs.getBoolean("complete"));
    }
}
