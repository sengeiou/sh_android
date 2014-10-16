package com.fav24.shootr.dao.impl;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.fav24.shootr.dao.UserDAO;
import com.fav24.shootr.dao.domain.User;
import com.fav24.shootr.dao.utils.PropertiesManager;

public class UserDAOImpl extends BaseDAOImpl implements UserDAO {

	@Override
	public User getUserById(Long idUser) {
		String query = PropertiesManager.getProperty("user.select.byId");
		List<User> users = getJdbcTemplate().query(query, new Object[] { idUser }, new BeanPropertyRowMapper<User>(User.class));
		return (users != null && !users.isEmpty()) ? users.get(0) : null; 
	}

}