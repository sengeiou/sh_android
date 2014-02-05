package com.fav24.dataservices.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fav24.dataservices.domain.Generic;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.security.AccessPolicy;


/**
 * Interfaz de servicio Generic. 
 * 
 * @author Fav24
 */
public interface GenericService {

	public static final Logger logger = LoggerFactory.getLogger(GenericService.class);
	
	public static final String ERROR_ACCESS_POLICY_CHECK_FAILED = "G000";
	public static final String ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE = "Fallo en el chequeo de las políticas de acceso contra la fuente de datos.";
	public static final String ERROR_OPERATION_NOT_AVAILABLE = "G001";
	public static final String ERROR_OPERATION_NOT_AVAILABLE_MESSAGE = "La operación %s no está disponible.";
	public static final String ERROR_START_TRANSACTION = "G002";
	public static final String ERROR_START_TRANSACTION_MESSAGE = "Error al iniciar la transacción.";
	public static final String ERROR_END_TRANSACTION = "G003";
	public static final String ERROR_END_TRANSACTION_MESSAGE = "Error al finalizar la transacción.";
	public static final String ERROR_UNCOMPLETE_REQUEST = "G004";
	public static final String ERROR_UNCOMPLETE_REQUEST_MESSAGE = "Es necesario indicar KEY o FILTER para la resolución de esta petición.";
	
	/**
	 * Procesa en contenido de una estructura Generic.
	 * 
	 * @param generic Estructura generica de operaciones a procesar.
	 * 
	 * @return estructura generic de entrada, enriquecida con los resultados de la salida.
	 * 
	 * @throws ServerException 
	 */
	public Generic processGeneric(Generic generic) throws ServerException;

	/**
	 * Chequea las políticas de acceso definidas, contra la fuente de datos a la que ataca el servicio.
	 * 
	 * Los chequeos que realiza son:
	 * 	- Que las entidades existen en la fuente de datos.
	 *  - Que los attributos de cada una de las entidades, están disponibles en la fuente de datos.
	 *  - Que el conjunto de claves especificado en las políticas de acceso, existe en la fuente de datos.
	 *  - Que el conjunto de filtros especificado en las políticas de acceso, se corresponde con su correspondiente conjunto de índices en la fuente de datos.
	 *  
	 * Nota: En este punto, es posible aprovechar el acceso a los metadatos de las entidades de la fuente de datos, para obtener el tipo de dato asociado a cada atributo de dato, clave o filtro.
	 * 
	 * @param accessPolicy Política de acceso a chequear.
	 * 
	 * @throws ServerException
	 */
	public void checkAccessPoliciesAgainstDataSource(AccessPolicy accessPolicy) throws ServerException;
}
