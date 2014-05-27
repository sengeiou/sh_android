package com.fav24.dataservices.service.hook;

import java.util.Map;
import java.util.NavigableMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fav24.dataservices.domain.security.RemoteFiles;
import com.fav24.dataservices.exception.ServerException;


/**
 * Interfaz de servicio de carga y consulta de puntos de inserción (Hooks). 
 */
public interface HookConfigurationService extends HookService {

	public static final Logger logger = LoggerFactory.getLogger(HookConfigurationService.class);

	public static final String HOOK_SOURCE_RELATIVE_LOCATION = "hooks";
	public static final String HOOK_BINARY_RELATIVE_LOCATION = HOOK_SOURCE_RELATIVE_LOCATION + "/bin";
	public static final String HOOK_SOURCE_SUFFIX = ".java";
	
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
	 * Retorna el conjunto de hooksdisponible.
	 * 
	 * @return el conjunto de hooksdisponible.
	 */
	public NavigableMap<String, GenericServiceHook> getAvailableHooks();

	/**
	 * Retorna el hook solicidato, o <code>null</code> en caso de no estar disponible.
	 * 
	 * @param alias Alias del hook a localizar.
	 * 
	 * @return el hook solicidato, o <code>null</code> en caso de no estar disponible.
	 */
	public GenericServiceHook getHook(String alias);

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
}
