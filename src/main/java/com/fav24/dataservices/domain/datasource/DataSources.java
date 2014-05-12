package com.fav24.dataservices.domain.datasource;

import java.io.File;
import java.net.MalformedURLException;
import java.util.AbstractList;

import javax.sql.DataSource;

import com.fav24.dataservices.DataServicesContext;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.util.FileUtils;
import com.fav24.dataservices.util.JDBCUtils;
import com.fav24.dataservices.xml.datasource.DataSourcesDOM;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;



public class DataSources
{
	public static final String APPLICATION_DATASOURCES_FILES_SUFFIX = ".datasources.xml";

	public static final String ERROR_LOADING_DATASOURCES_FILE = "DS000";
	public static final String ERROR_LOADING_DATASOURCES_FILE_MESSAGE = "No se encontró ningún fichero de definición de fuentes de datos.";
	public static final String ERROR_LOADING_DATASOURCES_FILE_TOO_MANY_FILES = "DS001";
	public static final String ERROR_LOADING_DATASOURCES_FILE_TOO_MANY_FILES_MESSAGE = "Se han encontrado más de un fichero de definición de fuentes de datos. <%s>.";
	public static final String ERROR_INVALID_DATASOURCES_FILE_URL = "DS002";
	public static final String ERROR_INVALID_DATASOURCES_FILE_URL_MESSAGE = "La URL <%s> no se corresponde con ningún fichero de definición de fuentes de datos.";

	private static DataSource dataSourceDataService;
	private static DataSource dataSourceStatistics;
	private static DataSources dataSources;

	private String version;
	private String description;
	private DataSourceConfiguration dataServices;
	private DataSourceConfiguration statistics;


	/**
	 * Constructor por defecto sin paràmetros.
	 */
	public DataSources() {

		this.version = null;
		this.description = null;
		this.dataServices = null;
		this.statistics = null;
	}

	/**
	 * Retorna la versión de data-services para la que fué creado esta configuración de fuentes de datos.
	 *  
	 * @return la versión de data-services para la que fué creado esta configuración de fuentes de datos.
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Asigna la versión de data-services para la que fué creado esta configuración de fuentes de datos.
	 *  
	 * @param version La versión de data-services para la que fué creado esta configuración de fuentes de datos.
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Retorna una descripción de esta configuración de fuentes de datos.
	 * 
	 * @return una descripción de esta configuración de fuentes de datos.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Asigna una descripción de esta configuración de fuentes de datos.
	 * 
	 * @param description Descripción a asignar a esta configuración de fuentes de datos.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Retorna la configuración de la fuente de datos asociada a los servicios de datos. 
	 * 
	 * @return la configuración de la fuente de datos asociada a los servicios de datos.
	 */
	public DataSourceConfiguration getDataServices() {
		return dataServices;
	}

	/**
	 * Asigna la configuración de la fuente de datos asociada a los servicios de datos.
	 * 
	 * @param dataServices Configuración a asignar.
	 */
	public void setDataServices(DataSourceConfiguration dataServices) {
		this.dataServices = dataServices;
	}

	/**
	 * Retorna la configuración de la fuente de datos en donde se almacenará la información estadística.
	 * 
	 * @return la configuración de la fuente de datos en donde se almacenará la información estadística.
	 */
	public DataSourceConfiguration getStatistics() {
		return statistics;
	}

	/**
	 * Asigna la configuración de la fuente de datos en donde se almacenará la información estadística.
	 * 
	 * @param dataServices Configuración a asignar.
	 */
	public void setStatistics(DataSourceConfiguration statistics) {
		this.statistics = statistics;
	}

	/**
	 * Añade propiedades y configuraciones específicas a la configuracion de la conexión, según el motor de bases de datos.
	 * 
	 * @param configuration Estructura de configuracion de la conexión sobre la que se añaden las propiedades.
	 */
	public static void addSpecificProperties(HikariConfig configuration) {

		switch(JDBCUtils.getProduct(configuration.getDataSourceClassName())) {

		case JDBCUtils.DB_PRODUCT_HSQL:
			break;

		case JDBCUtils.DB_PRODUCT_MYSQL:
			configuration.addDataSourceProperty("zeroDateTimeBehavior", "convertToNull");
			break;

		case JDBCUtils.DB_PRODUCT_ORACLE:
			break;

		case JDBCUtils.DB_PRODUCT_POSTGRESQL:
			break;
		}
	}

	/**
	 * Construye los singletons de acceso a datos para los servicios de datos.
	 */
	public static javax.sql.DataSource constructDataSource(DataSourceConfiguration dataSourceConfiguration) {

		HikariConfig configuration = new HikariConfig();

		// Configuración del pool.
		ConnectionPoolConfiguration connectionPoolConfiguration = dataSourceConfiguration.getConnectionPoolConfiguration();
		configuration.setPoolName(dataSourceConfiguration.getName());
		configuration.setInitializationFailFast(false);

		// Configuración de las conexiones gestionadas.
		ConnectionConfiguration connectionConfiguration = connectionPoolConfiguration.getConnectionConfiguration();

		configuration.setAutoCommit(connectionConfiguration.getAutoCommit());
		configuration.setConnectionTimeout(connectionConfiguration.getTimeout());
		configuration.setTransactionIsolation(connectionConfiguration.getTransactionIsolation().getTransactionIsolationTypeISOName());
		configuration.setJdbc4ConnectionTest(connectionConfiguration.getJdbc4ConnectionTest());
		if (connectionConfiguration.getInitSql() != null && !connectionConfiguration.getInitSql().isEmpty()) {
			configuration.setConnectionInitSql(connectionConfiguration.getInitSql());
		}
		if (connectionConfiguration.getTestQuery() != null && !connectionConfiguration.getTestQuery().isEmpty()) {
			configuration.setConnectionTestQuery(connectionConfiguration.getTestQuery());
		}

		// Resto de configuraciones del pool.
		configuration.setDataSourceClassName(connectionPoolConfiguration.getDataSourceClassName());
		configuration.setIdleTimeout(connectionPoolConfiguration.getIdleTimeout());
		configuration.setMaxLifetime(connectionPoolConfiguration.getMaxLifetime());
		configuration.setMaximumPoolSize(connectionPoolConfiguration.getMaximumPoolSize());
		configuration.setMinimumIdle(connectionPoolConfiguration.getMinimumPoolSize());

		// Configuración del acceso a la fuente.
		configuration.setUsername(dataSourceConfiguration.getUserName());
		configuration.setPassword(dataSourceConfiguration.getPassword());
		configuration.addDataSourceProperty("serverName", dataSourceConfiguration.getHost());
		configuration.addDataSourceProperty("portNumber", dataSourceConfiguration.getPort());
		configuration.addDataSourceProperty("databaseName", dataSourceConfiguration.getDatabaseName());

		// Configuraciones específicas.
		addSpecificProperties(configuration);

		HikariDataSource dataSource = new HikariDataSource(configuration);

		return dataSource;
	}

	/**
	 * Construye los singletons de acceso a datos para los servicios de datos.
	 */
	public void constructDataServicesDataSource() {

		dataSourceDataService = constructDataSource(dataServices);
	}

	/**
	 * Construye los singletons de acceso a datos para la gestión de estadísticas de uso.
	 */
	public void constructStatisticsDataSource() {

		dataSourceStatistics = constructDataSource(statistics);
	}

	/**
	 * Retorna la fuente de datos de los servicios de datos.
	 * 
	 * @return la fuente de datos de los servicios de datos.
	 */
	public static final DataSource getDataSourceDataService() {
		return dataSourceDataService;
	}

	/**
	 * Retorna la fuente de datos de la insformación estadística.
	 * 
	 * @return la fuente de datos de la insformación estadística.
	 */
	public static final DataSource getDataSourceStatistics() {
		return dataSourceStatistics;
	}

	/**
	 * Carga las fuentes de datos contenidas en el directorio base de la aplicación
	 * definido en el parámetro: "dataservices.home".
	 * 
	 * @throws ServerException 
	 */
	public static final void loadDefaultDataSources() throws ServerException {

		String applicationHome = DataServicesContext.getCurrentDataServicesContext().getApplicationHome();

		// Se cargan los archivos de configuración de fuentes datos existentes.
		File applicationHomeDir = new File(applicationHome);

		if (applicationHomeDir.exists() && applicationHomeDir.isDirectory()) {

			AbstractList<File> datasourcesFiles = FileUtils.getFilesWithSuffix(applicationHome, APPLICATION_DATASOURCES_FILES_SUFFIX, null);

			if (datasourcesFiles.size() == 0) {

				throw new ServerException(ERROR_LOADING_DATASOURCES_FILE, ERROR_LOADING_DATASOURCES_FILE_MESSAGE);
			}
			else if (datasourcesFiles.size() > 1) {

				StringBuilder dataSourcesConfigurationFilesFound = null;
				for (File datasourcesFile : datasourcesFiles) {

					if (dataSourcesConfigurationFilesFound == null) {
						dataSourcesConfigurationFilesFound = new StringBuilder();
					}
					else {
						dataSourcesConfigurationFilesFound.append(", ");
					}

					dataSourcesConfigurationFilesFound.append(datasourcesFile.getName());
				}

				throw new ServerException(ERROR_LOADING_DATASOURCES_FILE_TOO_MANY_FILES, String.format(ERROR_LOADING_DATASOURCES_FILE_TOO_MANY_FILES_MESSAGE, dataSourcesConfigurationFilesFound));
			}
			else {

				for(File datasourcesFile : datasourcesFiles) {

					try {

						dataSources = new DataSourcesDOM(datasourcesFile.toURI().toURL());

						dataSources.constructDataServicesDataSource();
						dataSources.constructStatisticsDataSource();
					} 
					catch (MalformedURLException e) {
						throw new ServerException(ERROR_INVALID_DATASOURCES_FILE_URL, 
								String.format(ERROR_INVALID_DATASOURCES_FILE_URL_MESSAGE, datasourcesFile.toURI().toString()));
					}
				}
			}
		}
		else {

			throw new ServerException(DataServicesContext.ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_NOT_DEFINED, 
					DataServicesContext.ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_NOT_DEFINED_MESSAGE);
		}
	}

	/**
	 * Retorna las fuentes de datos configuradas.
	 * 
	 * @return las fuentes de datos configuradas.
	 */
	public static DataSources getCurrentDataSources() {

		return dataSources;
	}
}
