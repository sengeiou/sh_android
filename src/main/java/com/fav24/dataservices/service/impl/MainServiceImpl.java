package com.fav24.dataservices.service.impl;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.MainService;
import com.fav24.dataservices.util.JDBCUtils;


/**
 * Implementación de servicio Main. 
 */
@Component
@Scope("prototype")
public class MainServiceImpl implements MainService {

	private static Map<String, String> DataSourceInformation;
	private static Map<String, String> StatsDataSourceInformation;

	@Autowired
	private JdbcTemplate jdbcTemplate;


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> getDataSourceInformation() throws ServerException {

		try {

			if (DataSourceInformation == null) {
				DataSourceInformation = JDBCUtils.getConnectionInformation(jdbcTemplate.getDataSource().getConnection());
			}

			return DataSourceInformation;

		} catch (MalformedURLException | SQLException e) {

			throw new ServerException(ERROR_DATASOURCE_GET_INFO_FAILED, ERROR_DATASOURCE_GET_INFO_FAILED_MESSAGE);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> getStatsDataSourceInformation() throws ServerException {

		try {

			if (StatsDataSourceInformation == null) {
				StatsDataSourceInformation = JDBCUtils.getConnectionInformation(jdbcTemplate.getDataSource().getConnection());
			}

			return StatsDataSourceInformation; 

		} catch (MalformedURLException | SQLException e) {

			throw new ServerException(ERROR_STATS_DATASOURCE_GET_INFO_FAILED, ERROR_STATS_DATASOURCE_GET_INFO_FAILED_MESSAGE);
		}
	}
}
