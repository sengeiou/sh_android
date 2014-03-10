package com.fav24.dataservices.service.impl;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.monitoring.SystemMonitoring;
import com.fav24.dataservices.monitoring.SystemMonitoring.MonitorSampleData;
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

	private static SystemMonitoring SystemActivityMonitoring;

	static {
		SystemActivityMonitoring = new SystemMonitoring();
	}

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractList<MonitorSampleData> getSystemMemoryStatus(Long period, Long timeRange) {

		return SystemActivityMonitoring.getSystemMemoryStatus(period, timeRange);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractList<MonitorSampleData> getSystemCpuActivity(Long period, Long timeRange) {

		return SystemActivityMonitoring.getSystemCpuActivity(period, timeRange);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, AbstractList<MonitorSampleData>> getSystemStorageStatus(Long period, Long timeRange) {

		return SystemActivityMonitoring.getSystemStorageStatus(period, timeRange);
	}
}
