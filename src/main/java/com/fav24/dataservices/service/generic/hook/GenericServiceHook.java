package com.fav24.dataservices.service.generic.hook;

import com.fav24.dataservices.domain.generic.Generic;
import com.fav24.dataservices.domain.generic.Operation;

/**
 * Puntos de incorporación al ciclo de vido de una petición al servicio de datos genérico.
 * 
 * Todas las classes que implementen esta interfaz, serán cargadas por el servidor, y estarán disponibles mientras este esté en servicio.
 * 
 * @param <T> Tipo de conecxión.
 */
public interface GenericServiceHook<T> {

	/** Continua con la ejecución de la petición normalmente, después de ejecutar el método del "hook".*/
	public static final int CONTINUE = 0;
	/** Detiene la ejecución de la petición con éxito, después de ejecutar el método del "hook".*/
	public static final int STOP_OK = 1;
	/** Detiene la ejecución de la petición con error, después de ejecutar el método del "hook".*/
	public static final int STOP_KO = -1;
	
	/**
	 * Método que se instancia antes de ejecutar el bloque de operaciones de una petición.
	 * 
	 * @param connnection Conexión abierta al subsistema.
	 * @param generic Estructura completa de la petición.
	 * 
	 * @return {@linkplain #CONTINUE}, {@linkplain #STOP_OK} o {@linkplain #STOP_KO}. 
	 */
	public int requestBegin(T connnection, Generic generic);
	
	/**
	 * Método que se instancia depués de ejecutar el bloque de operaciones de una petición.
	 * 
	 * @param connnection Conexión abierta al subsistema.
	 * @param generic Estructura completa de la petición.
	 * 
	 * @return {@linkplain #CONTINUE}, {@linkplain #STOP_OK} o {@linkplain #STOP_KO}. 
	 */
	public int requestEnd(T connnection, Generic generic);
	
	/**
	 * Método que se instancia antes de ejecutar cada operación del bloque de operaciones de una petición.
	 * 
	 * @param connnection Conexión abierta al subsistema.
	 * @param generic Estructura completa de la petición.
	 * @param operation Estructura de la operación que se ejecutará a continuación.
	 * 
	 * @return {@linkplain #CONTINUE}, {@linkplain #STOP_OK} o {@linkplain #STOP_KO}. 
	 */
	public int operationBegin(T connnection, Generic generic, Operation operation);
	
	/**
	 * Método que se instancia antes despues de ejecutar cada operación del bloque de operaciones de una petición.
	 * 
	 * @param connnection Conexión abierta al subsistema.
	 * @param generic Estructura completa de la petición.
	 * @param operation Estructura de la operación que se ejecutará a continuación.
	 * 
	 * @return {@linkplain #CONTINUE}, {@linkplain #STOP_OK} o {@linkplain #STOP_KO}. 
	 */
	public int operationEnd(T connnection, Generic generic, Operation operation);
}
