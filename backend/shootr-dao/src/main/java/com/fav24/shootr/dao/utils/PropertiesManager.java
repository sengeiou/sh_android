package com.fav24.shootr.dao.utils;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.log4j.Logger;

public class PropertiesManager {
	private static Logger logger = Logger.getLogger(PropertiesManager.class);
	
	private final static String FILE_NAME = "sqlqueries.properties";
	private final static long REFRESH_DELAY = 30000; //(1000* 60 * 5)
	
	private static PropertiesConfiguration configuration = null;
	
	static {
		configuration = new PropertiesConfiguration();
		configuration.setDelimiterParsingDisabled(true);
		try {
			configuration.setFileName(FILE_NAME);
			configuration.load();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		FileChangedReloadingStrategy strategy = new FileChangedReloadingStrategy();
		strategy.setRefreshDelay(REFRESH_DELAY);
		configuration.setReloadingStrategy(strategy);
	}

	public static synchronized String getProperty(final String key) {
		String property = String.valueOf(configuration.getProperty(key));
		logger.debug("Property " + key + " read. Value: " + property);
		return property;
	}
}
