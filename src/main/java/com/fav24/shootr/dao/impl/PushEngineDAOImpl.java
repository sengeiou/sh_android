package com.fav24.shootr.dao.impl;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.fav24.shootr.dao.PushEngineDAO;
import com.fav24.shootr.dao.domain.PushEngine;
import com.fav24.shootr.dao.utils.PropertiesManager;

public class PushEngineDAOImpl extends BaseDAOImpl implements PushEngineDAO {

	/**
	 * {@inheritDoc}
	 */
	public List<PushEngine> getPushEngines() {
		String query = PropertiesManager.getProperty("pushEngine.select.all");
		return getJdbcTemplate().query(query, new BeanPropertyRowMapper<PushEngine>(PushEngine.class));
	}

}