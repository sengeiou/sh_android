package com.fav24.dataservices;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import org.springframework.context.ApplicationContext;


public class DataServicesContext {

	public static final String ENCODING = "UTF-8";
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

	private static String applicationHome;
	private static ApplicationContext applicationContext;


	/**
	 * Retorna la ruta base de la aplicación.
	 * 
	 * @return la ruta base de la aplicación.
	 */
	public static String getApplicationHome() {

		return applicationHome;
	}

	/**
	 * Asigna la ruta base de la aplicación.
	 * 
	 * @param applicationHome La ruta base de la aplicación a asignar.
	 */
	public static void setApplicationHome(String applicationHome) {

		DataServicesContext.applicationHome = applicationHome;
	}

	/**
	 * Retorna el contexto de aplicación Spring.
	 * 
	 * @return el contexto de aplicación Spring.
	 */
	public static ApplicationContext getApplicationContext() {

		return applicationContext;
	}

	/**
	 * Asigna el contexto de aplicación Spring.
	 * 
	 * @param applicationContext El contexto de aplicación a asignar.
	 */
	public static void setApplicationContext(ApplicationContext applicationContext) {

		DataServicesContext.applicationContext = applicationContext;
	}
}
