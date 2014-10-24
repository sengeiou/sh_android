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
	public static final String ERROR_INVALID_REQUEST_KEY_MESSAGE = "La entidad <%s> no es accesible mediante la clave <%s>.";
	public static final String ERROR_INVALID_REQUEST_NO_FILTER = "G008";
	public static final String ERROR_INVALID_REQUEST_NO_FILTER_MESSAGE = "La entidad <%s> debe ser accedida mediante uno de los filtros definidos.";
	public static final String ERROR_INVALID_REQUEST_FILTER = "G009";
	public static final String ERROR_INVALID_REQUEST_FILTER_MESSAGE = "La entidad <%s> no es accesible mediante el filtro <%s>.";
	public static final String ERROR_INVALID_REQUEST_FILTER_ATTRIBUTE = "G010";
	public static final String ERROR_INVALID_REQUEST_FILTER_ATTRIBUTE_MESSAGE = "Filtro no válido para la entidad <%s>. El atributo <%s> no está definido.";
	public static final String ERROR_INVALID_REQUEST_KEY_ATTRIBUTE = "G011";
	public static final String ERROR_INVALID_REQUEST_KEY_ATTRIBUTE_MESSAGE = "Clave no válida para la entidad <%s>. El atributo <%s> no está definido.";

	public static final String ERROR_OPERATION = "G012";
	public static final String ERROR_OPERATION_MESSAGE = "No ha sido posible realizar la operación <%s> sobre la entidad <%s> debido a: %s";
	public static final String ERROR_INVALID_CREATE_REQUEST = "G013";
	public static final String ERROR_INVALID_CREATE_REQUEST_MESSAGE = "No se ha indicado ningún dato a crear para la entidad <%s>.";
	public static final String ERROR_CREATE_DUPLICATE_ROW = "G014";
	public static final String ERROR_CREATE_DUPLICATE_ROW_MESSAGE = "Existe al menos un registro con al menos un una de las claves indicadas. <%s>.";
	public static final String ERROR_REFURBISHING_ROW = "G015";
	public static final String ERROR_REFURBISHING_ROW_MESSAGE = "No ha sido posible recuperar el registro para el elemento <%s> debido a: %s";
	public static final String ERROR_UPDATING_ROW = "G016";
	public static final String ERROR_UPDATING_ROW_MESSAGE = "No ha sido posible actualizar el registro para el elemento <%s> debido a: %s";
	public static final String ERROR_REFURBISHED_ROW_LOST = "G017";
	public static final String ERROR_REFURBISHED_ROW_LOST_MESSAGE = "No ha sido posible localizar el registro recuperado. <%s>.";
	public static final String ERROR_INVALID_UPDATE_REQUEST = "G018";
	public static final String ERROR_INVALID_UPDATE_REQUEST_MESSAGE = "No se ha indicado ningún dato a modificar para la entidad <%s>.";
	public static final String ERROR_UPDATE_ENTITY_LACKS_PRIMARY_KEY = "G019";
	public static final String ERROR_UPDATE_ENTITY_LACKS_PRIMARY_KEY_MESSAGE = "No es posible modificar datos de la entidad <%s>, debido a que no tiene definida ninguna clave primaria.";
	public static final String ERROR_INVALID_CREATEUPDATE_REQUEST = "G020";
	public static final String ERROR_INVALID_CREATEUPDATE_REQUEST_MESSAGE = "No se ha indicado ningún dato a crear o modificar para la entidad <%s>.";
	public static final String ERROR_INVALID_UPDATECREATE_REQUEST = "G021";
	public static final String ERROR_INVALID_UPDATECREATE_REQUEST_MESSAGE = "No se ha indicado ningún dato a modificar o crear para la entidad <%s>.";
	public static final String ERROR_DUPLICATED_ROW = "G022";
	public static final String ERROR_DUPLICATED_ROW_MESSAGE = "Existe más de un registro con al menos un una de las claves indicadas. <%s>.";
	public static final String ERROR_INVALID_READONLY_POLICY = "G023";
	public static final String ERROR_INVALID_READONLY_POLICY_MESSAGE = "No se permite escribir en los atributos informados para la entidad <%s>.";
	public static final String ERROR_HOOK_NOT_LOADED = "G024";
	public static final String ERROR_HOOK_NOT_LOADED_MESSAGE = "El hook <%s> no está disponible para la entidad <%s>, en la request de alias <%s>.";
	public static final String ERROR_HOOK_STOP_KO = "G025";
	public static final String ERROR_HOOK_STOP_KO_MESSAGE = "El hook <%s> para la entidad <%s>, en la request de alias <%s>, ha terminado con errores: %s";
	
	public static final String ERROR_UNKNOWN = "G999";
	public static final String ERROR_UNKNOWN_MESSAGE = "Error desconocido en la petición. Mensaje interno: <%s>";


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
