package com.fav24.dataservices.service.datasources.impl;

import java.sql.Connection;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.domain.datasource.DataSources;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.datasources.DataSourcesService;
import com.fav24.dataservices.util.JDBCUtils;


/**
 * Implementación de servicio para la gestión de orígenes de datos. 
 */
@Scope("singleton")
@Component
public class DataSourcesServiceImpl implements DataSourcesService {

	private Map<String, String> dataServicesDataSourceInformation;
	private Map<String, String> statisticsDataSourceInformation;


	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized Map<String, String> getDataServiceDataSourceInformation() throws ServerException {

		try {

			if (dataServicesDataSourceInformation == null) {

				DataSource dataSourceDataService = DataSources.getDataSourceDataService();

				if (dataSourceDataService != null) {
					dataServicesDataSourceInformation = JDBCUtils.getConnectionInformation(dataSourceDataService.getConnection());
				}
			}

			return dataServicesDataSourceInformation;

		} catch (Throwable t) {

			throw new ServerException(ERROR_DATASOURCE_GET_INFO_FAILED, ERROR_DATASOURCE_GET_INFO_FAILED_MESSAGE + " (" + t.getMessage() + ")");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getDataServiceDataSourceTime() throws ServerException {

		DataSource dataSourceDataService = DataSources.getDataSourceDataService();

		if (dataSourceDataService != null) {

			Connection connection = null;

			try {

				connection = dataSourceDataService.getConnection();

				return JDBCUtils.getConnectionDateTime(connection);

			} catch (Throwable t) {

				throw new ServerException(ERROR_DATASOURCE_GET_TIMESTAMP_FAILED, ERROR_DATASOURCE_GET_TIMESTAMP_FAILED_MESSAGE);
			}
			finally {
				JDBCUtils.CloseQuietly(connection);
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized Map<String, String> getStatisticsDataSourceInformation() throws ServerException {

		try {

			if (statisticsDataSourceInformation == null) {

				DataSource dataSourceStatistics = DataSources.getDataSourceStatistics();

				if (dataSourceStatistics != null) {
					statisticsDataSourceInformation = JDBCUtils.getConnectionInformation(dataSourceStatistics.getConnection());
				}
			}

			return statisticsDataSourceInformation; 

		} catch (Throwable t) {

			throw new ServerException(ERROR_STATS_DATASOURCE_GET_INFO_FAILED, ERROR_STATS_DATASOURCE_GET_INFO_FAILED_MESSAGE);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getStatisticsDataSourceTime() throws ServerException {

		DataSource dataSourceStatistics = DataSources.getDataSourceStatistics();

		if (dataSourceStatistics != null) {

			try {

				return JDBCUtils.getConnectionDateTime(dataSourceStatistics.getConnection());

			} catch (Throwable t) {

				throw new ServerException(ERROR_STATS_DATASOURCE_GET_TIMESTAMP_FAILED, ERROR_STATS_DATASOURCE_GET_TIMESTAMP_FAILED_MESSAGE);
			}
		}

		return null;
	}
}
