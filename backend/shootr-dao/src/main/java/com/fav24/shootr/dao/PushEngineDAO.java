package com.fav24.shootr.dao;

import java.util.List;

import com.fav24.shootr.dao.domain.PushEngine;


public interface PushEngineDAO extends BaseDAO {
	/**
	 * Retorna los PushEngines.
	 * @return Listado de PushEngines.
	 */
	public List<PushEngine> getPushEngines();
}