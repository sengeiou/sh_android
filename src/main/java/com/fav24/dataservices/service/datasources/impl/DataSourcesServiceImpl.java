package com.fav24.dataservices.service.datasources.impl;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.domain.datasource.DataSources;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.datasources.DataSourcesService;
import com.fav24.dataservices.util.JDBCUtils;


/**
 * Implementación de servicio Main. 
 */
@Component
@Scope("prototype")
public class DataSourcesServiceImpl implements DataSourcesService {

	private static Map<String, String> dataServicesDataSourceInformation;
	private static Map<String, String> statisticsDataSourceInformation;


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> getDataServiceDataSourceInformation() throws ServerException {

		try {

			if (dataServicesDataSourceInformation == null && DataSources.getDataSourceDataService() != null) {
				dataServicesDataSourceInformation = JDBCUtils.getConnectionInformation(DataSources.getDataSourceDataService().getConnection());
			}

			return dataServicesDataSourceInformation;

		} catch (MalformedURLException | SQLException e) {

			throw new ServerException(ERROR_DATASOURCE_GET_INFO_FAILED, ERROR_DATASOURCE_GET_INFO_FAILED_MESSAGE + " (" + e.getMessage() + ")");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> getStatisticsDataSourceInformation() throws ServerException {

		try {

			if (statisticsDataSourceInformation == null && DataSources.getDataSourceStatistics() != null) {
				statisticsDataSourceInformation = JDBCUtils.getConnectionInformation(DataSources.getDataSourceStatistics().getConnection());
			}

			return statisticsDataSourceInformation; 

		} catch (MalformedURLException | SQLException e) {

			throw new ServerException(ERROR_STATS_DATASOURCE_GET_INFO_FAILED, ERROR_STATS_DATASOURCE_GET_INFO_FAILED_MESSAGE);
		}
	}
}
