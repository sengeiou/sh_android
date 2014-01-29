package com.fav24.dataservices.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fav24.dataservices.domain.Generic;
import com.fav24.dataservices.exception.ServerException;


/**
 * Interfaz de servicio Generic. 
 * 
 * @author Fav24
 */
public interface GenericService {

	public static final Logger logger = LoggerFactory.getLogger(GenericService.class);
	
	public static final String ERROR_OPERATION_NOT_AVAILABLE = "G000";
	public static final String ERROR_START_TRANSACTION = "G001";
	public static final String ERROR_END_TRANSACTION = "G002";
	public static final String ERROR_UNCOMPLETE_REQUEST = "G003";
	
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
