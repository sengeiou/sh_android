package com.fav24.shootr.dao;

import com.fav24.shootr.dao.domain.User;

public interface UserDAO extends BaseDAO {
	public User getUserById(Long idUser);
}