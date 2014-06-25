package com.fav24.dataservices;

import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.domain.datasource.DataSources;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.monitoring.SamplesRegister;
import com.fav24.dataservices.service.cache.CacheConfigurationService;
import com.fav24.dataservices.service.hook.HookConfigurationService;
import com.fav24.dataservices.service.policy.AccessPolicyConfigurationService;


/**
 * Clase en la que se centralizan los distintos componentes que conforman el contexto de la aplicación.
 */
@Component
public class DataServicesContext {

	public static final Logger logger = LoggerFactory.getLogger(DataServicesContext.class);

	public static final String ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_NOT_DEFINED = "AC000";
	public static final String ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_NOT_DEFINED_MESSAGE = "El parámetro " + DataServicesContext.APPLICATION_HOME + " no está definido en el contexto del servidor de aplicaciones.";
	public static final String ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_DOESNT_EXISTS = "AC001";
	public static final String ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_DOESNT_EXISTS_MESSAGE = "La ruta indicada en el parámetro " + DataServicesContext.APPLICATION_HOME + " <%s> no existe.";
	public static final String ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_IS_NOT_A_DIRECTORY = "AC002";
	public static final String ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_IS_NOT_A_DIRECTORY_MESSAGE = "La ruta indicada en el parámetro " + DataServicesContext.APPLICATION_HOME + " <%s> no hace referencia a un directorio.";

	public static final String DEFAULT_ENCODING = "UTF-8";
	public static final Charset DEFAULT_CHARSET = Charset.forName(DataServicesContext.DEFAULT_ENCODING);
	public static final Locale MAIN_LOCALE;
	public static final TimeZone MAIN_TIME_ZONE;
	public static final Calendar MAIN_CALENDAR;
	static 
	{
		MAIN_LOCALE = new Locale("cat", "ES");

		TimeZone Dummy;
		try
		{
			Dummy = TimeZone.getTimeZone("GMT+00:00");
		}
		catch(Throwable t)
		{
			Dummy = null;
		}
		MAIN_TIME_ZONE = Dummy;

		MAIN_CALENDAR = Calendar.getInstance(MAIN_TIME_ZONE, MAIN_LOCALE);
	}

	public static final String APPLICATION_NAME = "dataservices";
	public static final String APPLICATION_HOME = APPLICATION_NAME + ".home";

	private static DataServicesContext currentDataServicesContext;
	
	private String applicationHome;
	private ApplicationContext applicationContext;

	@Autowired
	protected AccessPolicyConfigurationService accessPolicyConfigurationService;
	@Autowired
	protected HookConfigurationService hookConfigurationService;
	@Autowired
	protected CacheConfigurationService cacheConfigurationService;

	
	/**
	 * Constructor por defecto.
	 */
	public DataServicesContext() {
	
		currentDataServicesContext = this;
	}

	/**
	 * Retorna la última instancia de esta clase que se creó.
	 * 
	 * @return la última instancia de esta clase que se creó.
	 */
	public static DataServicesContext getCurrentDataServicesContext() {
		return currentDataServicesContext;
	}
	
	/**
	 * Retorna la ruta base de la aplicación.
	 * 
	 * @return la ruta base de la aplicación.
	 */
	public String getApplicationHome() {

		return applicationHome;
	}

	/**
	 * Asigna la ruta base de la aplicación.
	 * 
	 * @param applicationHome La ruta base de la aplicación a asignar.
	 */
	public void setApplicationHome(String applicationHome) {

		this.applicationHome = applicationHome;
	}

	/**
	 * Retorna el contexto de aplicación Spring.
	 * 
	 * @return el contexto de aplicación Spring.
	 */
	public ApplicationContext getApplicationContext() {

		return applicationContext;
	}

	/**
	 * Asigna el contexto de aplicación Spring.
	 * 
	 * @param applicationContext El contexto de aplicación a asignar.
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {

		this.applicationContext = applicationContext;
	}

	/**
	 * Inicialización del contexto de ejecución de los servicios de datos.
	 */
	public void initDataServicesContext() {

		if (getApplicationHome() == null) {

			logger.error(ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_NOT_DEFINED_MESSAGE);
		}
		else {

			try {
				SamplesRegister.initSamplesRegister();
			}
			catch(ServerException e) {

				logger.error(e.getMessage());

				System.exit(1);
			}

			try {
				loadDataSourcesConfiguration();
			}
			catch(ServerException e) {

				logger.error(e.getMessage());

				System.exit(1);
			}

			try {
				loadGenericServiceHooks();
				loadAccessPolicy();
				loadCache();
			}
			catch(ServerException e) {

				logger.error(e.getMessage());
			}
		}
	}

	/**
	 * Carga la configuración de las fuentes de datos.
	 */
	public void loadDataSourcesConfiguration() throws ServerException {

		DataSources.loadDefaultDataSources();
	}

	/**
	 * Carga los hooks para el servicio Generic que habrá disponibles durante la ejecución.
	 * 
	 * @throws ServerException 
	 */
	public void loadGenericServiceHooks() throws ServerException {

		try {
			hookConfigurationService.loadDefaultHooks();
		}
		catch(ServerException e) {

			try {
				hookConfigurationService.dropHooks();
			} catch (ServerException e1) {
				logger.error(e1.getMessage());
			}

			throw e;
		}
	}
	
	/**
	 * Carga las políticas de acceso por defecto.
	 * 
	 * @throws ServerException 
	 */
	public void loadAccessPolicy() throws ServerException {
		
		try {
			accessPolicyConfigurationService.loadDefaultAccessPolicy();
		}
		catch(ServerException e) {
			
			try {
				accessPolicyConfigurationService.dropAccessPolicies();
			} catch (ServerException e1) {
				logger.error(e1.getMessage());
			}
			
			throw e;
		}
	}

	/**
	 * Carga la configuración de la caché por defecto.
	 * 
	 * @throws ServerException 
	 */
	public void loadCache() throws ServerException {

		try {
			cacheConfigurationService.loadDefaultCacheConfiguration();
		}
		catch(ServerException e) {

			cacheConfigurationService.dropSystemCache();

			throw e;
		}
	}
}
