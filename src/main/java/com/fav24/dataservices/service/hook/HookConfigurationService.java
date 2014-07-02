package com.fav24.dataservices.service.hook;

import java.io.File;
import java.net.URL;
import java.util.AbstractList;
import java.util.Map;
import java.util.NavigableMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fav24.dataservices.domain.policy.RemoteFiles;
import com.fav24.dataservices.exception.ServerException;


/**
 * Interfaz de servicio de carga y consulta de puntos de inserción (Hooks). 
 */
public interface HookConfigurationService extends HookService {

	public static final Logger logger = LoggerFactory.getLogger(HookConfigurationService.class);

	public static final String HOOK_FILES_RELATIVE_LOCATION = "hooks";
	public static final String HOOK_FILES_SUFFIX = ".java";
	public static final String HOOK_DEPENDENCY_FILES_RELATIVE_LOCATION = HOOK_FILES_RELATIVE_LOCATION + File.separator + "libs";
	public static final String HOOK_DEPENDENCY_FILES_SUFFIX = ".jar";

	
	/**
	 * Carga los hooks que serán asignables desde las políticas de acceso.
	 * 
	 * @return los diagnósticos del compilador, en caso de producirse errores.
	 */
	public Map<String, StringBuilder> loadDefaultHooks() throws ServerException;

	/**
	 * Descarga el conjunto de hooks.
	 */
	public void dropHooks() throws ServerException;

	/**
	 * Retorna el conjunto de hooks disponible.
	 * 
	 * @return el conjunto de hooks disponible.
	 */
	public NavigableMap<String, GenericServiceHook> getAvailableHooks();

	/**
	 * Retorna las urls del conjunto de hooks disponible.
	 * 
	 * @return las urls del conjunto de hooks disponible.
	 */
	public NavigableMap<String, URL> getAvailableHooksSourceUrls();

	/**
	 * Retorna el hook solicidato, o <code>null</code> en caso de no estar disponible.
	 * 
	 * @param alias Alias del hook a localizar.
	 * 
	 * @return el hook solicidato, o <code>null</code> en caso de no estar disponible.
	 */
	public GenericServiceHook getHook(String alias);

	/**
	 * Retorna la URL del fuente para el hook solicidato, o <code>null</code> en caso de no estar disponible.
	 * 
	 * @param alias Alias del hook a localizar.
	 * 
	 * @return la URL del fuente para el hook solicidato, o <code>null</code> en caso de no estar disponible.
	 */
	public URL getHookSourceURL(String alias);

	/**
	 * Carga el conjunto de hooks especificados en la estructura suministrada.
	 * 
	 * @param hookFiles Conjunto de hooks a cargar.
	 * 
	 * @return los resultados de la compilación.
	 * 
	 * @throws ServerException
	 */
	public Map<String, StringBuilder> loadHooks(RemoteFiles hookFiles) throws ServerException;

	/**
	 * Retorna el classpath a organizado por ubicaciones de clases y dependencias.
	 * 
	 * @return el classpath a organizado por ubicaciones de clases y dependencias.
	 * 
	 * @throws ServerException 
	 */
	public AbstractList<AbstractList<String>> getOrganizedClassPath() throws ServerException;
}
