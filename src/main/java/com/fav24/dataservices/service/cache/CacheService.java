package com.fav24.dataservices.service.cache;



/**
 * Interfaz para servicios relacionados con la caché y los gestores de caché. 
 * 
 * @author Fav24
 */
public interface CacheService {

	public static final String ERROR_LOADING_CACHE_CONFIGURATION_FILES = "CS000";
	public static final String ERROR_LOADING_CACHE_CONFIGURATION_FILES_MESSAGE = "No se indicó ninguna URL de ningún fichero de configuración de caché.";
	public static final String ERROR_INVALID_CACHE_CONFIGURATION_FILE_URL = "CS001";
	public static final String ERROR_INVALID_CACHE_CONFIGURATION_FILE_URL_MESSAGE = "La URL <%s> no se corresponde con ningún fichero de configuración de caché.";
	public static final String ERROR_INVALID_URL = "CS002";
	public static final String ERROR_INVALID_URL_MESSAGE = "La URL <%s> no es válida.";
}
