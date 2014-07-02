package com.fav24.dataservices.util;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;


/**
 * Utilidades para añadir recursos dinámicamente.
 */
public class DynamicURLClassLoaderUtils {

	/**
	 * Añade la url indicada al class loader indicado.
	 * 
	 * @param url URL con el recurso o directorio de clases a añadir.
	 * @param classLoader Class loader al que se añadirá la URL.
	 */
	public static void addURL(URL url, URLClassLoader classLoader) {

		Class<?> sysClass = URLClassLoader.class;

		try {
			Method sysMethod = sysClass.getDeclaredMethod("addURL", URL.class);
			sysMethod.setAccessible(true);
			sysMethod.invoke(classLoader, url);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Añade las urls indicadas al class loader indicado.
	 * 
	 * @param urls URLs con los recursos o directorios de clases a añadir.
	 * @param classLoader Class loader al que se añadirán las URLs.
	 */
	public static void addURLs(URL []urls, URLClassLoader classLoader) {

		Class<?> sysClass = URLClassLoader.class;

		try {
			Method sysMethod = sysClass.getDeclaredMethod("addURL", URL.class);
			sysMethod.setAccessible(true);

			for (URL url : urls) {

				sysMethod.invoke(classLoader, url);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
