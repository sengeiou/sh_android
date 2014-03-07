package com.fav24.dataservices.service.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.HashMap;
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Long> getSystemMemoryStatus() {

		Map<String, Long> systemMemoryStatus = new HashMap<String, Long>();

		long maxMemory = Runtime.getRuntime().maxMemory(); 
		long totalMemory = Runtime.getRuntime().totalMemory();
		long usedMemory = totalMemory - Runtime.getRuntime().freeMemory(); 
		long freeMemory = maxMemory - usedMemory;

		systemMemoryStatus.put(MAX_MEMORY, maxMemory);
		systemMemoryStatus.put(TOTAL_MEMORY, totalMemory);
		systemMemoryStatus.put(USED_MEMORY, usedMemory);
		systemMemoryStatus.put(FREE_MEMORY, freeMemory);

		return systemMemoryStatus;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Map<String, Long>> getSystemStorageStatus() {

		Map<String, Map<String, Long>> systemStorageStatus = new HashMap<String, Map<String, Long>>();

		for (Path root : FileSystems.getDefault().getRootDirectories())
		{
			Map<String, Long> storageStatus = new HashMap<String, Long>();

			try	{

				FileStore store = Files.getFileStore(root);

				storageStatus.put(TOTAL_STORAGE_SPACE, store.getTotalSpace());
				storageStatus.put(TOTAL_USABLE_STORAGE_SPACE, store.getUsableSpace());

				systemStorageStatus.put(root.toString(), storageStatus);
			}
			catch (IOException e) {
			}
		}

		return systemStorageStatus;
	}
}
