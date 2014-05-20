package com.fav24.dataservices.service.generic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fav24.dataservices.domain.generic.Generic;
import com.fav24.dataservices.exception.ServerException;


/**
 * Interfaz de servicio Generic. 
 */
public interface GenericService {

	public static final Logger logger = LoggerFactory.getLogger(GenericService.class);

	public static final String ERROR_OPERATION_NOT_AVAILABLE = "G001";
	public static final String ERROR_OPERATION_NOT_AVAILABLE_MESSAGE = "La operación <%s> no está disponible para la entidad <%s>.";
	public static final String ERROR_START_TRANSACTION = "G002";
	public static final String ERROR_START_TRANSACTION_MESSAGE = "Error al iniciar la transacción. %s";
	public static final String ERROR_END_TRANSACTION = "G003";
	public static final String ERROR_END_TRANSACTION_MESSAGE = "Error al finalizar la transacción. %s";

	public static final String ERROR_MALFORMED_REQUEST = "G004";
	public static final String ERROR_MALFORMED_REQUEST_MESSAGE = "Petición mal formada. El atributo <%s> es incorrecto, está desubicado o no existe.";

	public static final String ERROR_UNCOMPLETE_KEY_FILTER_REQUEST = "G005";
	public static final String ERROR_UNCOMPLETE_KEY_FILTER_REQUEST_MESSAGE = "Es necesario indicar KEY o FILTER para la resolución de esta petición.";
	public static final String ERROR_INVALID_REQUEST_NO_KEY = "G006";
	public static final String ERROR_INVALID_REQUEST_NO_KEY_MESSAGE = "La entidad <%s> únicamente es accesible mediante clave.";
	public static final String ERROR_INVALID_REQUEST_KEY = "G007";
	public static final String ERROR_INVALID_REQUEST_KEY_MESSAGE = "La entidad <%s> no es accesible mediante la clave indicada.";
	public static final String ERROR_INVALID_REQUEST_NO_FILTER = "G008";
	public static final String ERROR_INVALID_REQUEST_NO_FILTER_MESSAGE = "La entidad <%s> debe ser accedida mediante uno de los filtros definidos.";
	public static final String ERROR_INVALID_REQUEST_FILTER = "G009";
	public static final String ERROR_INVALID_REQUEST_FILTER_MESSAGE = "La entidad <%s> no es accesible mediante el filtro indicado.";

	public static final String ERROR_OPERATION = "G010";
	public static final String ERROR_OPERATION_MESSAGE = "No ha sido posible realizar la operación <%s> sobre la entidad <%s> debido a: %s";
	public static final String ERROR_INVALID_CREATE_REQUEST = "G011";
	public static final String ERROR_INVALID_CREATE_REQUEST_MESSAGE = "No se ha indicado ningún dato a crear para la entidad <%s>.";
	public static final String ERROR_CREATE_DUPLICATE_ROW = "G012";
	public static final String ERROR_CREATE_DUPLICATE_ROW_MESSAGE = "Existe al menos un registro con al menos un una de las claves indicadas. <%s>.";
	public static final String ERROR_CREATE_REFURBISHED_ROW_LOST = "G013";
	public static final String ERROR_CREATE_REFURBISHED_ROW_LOST_MESSAGE = "No ha sido posible localizar el registro recuperado. <%s>.";
	public static final String ERROR_INVALID_UPDATE_REQUEST = "G014";
	public static final String ERROR_INVALID_UPDATE_REQUEST_MESSAGE = "No se ha indicado ningún dato a modificar para la entidad <%s>.";
	public static final String ERROR_UPDATE_ENTITY_LACKS_PRIMARY_KEY = "G015";
	public static final String ERROR_UPDATE_ENTITY_LACKS_PRIMARY_KEY_MESSAGE = "No es posible modificar datos de la entidad <%s>, debido a que no tiene definida ninguna clave primaria.";

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
}
