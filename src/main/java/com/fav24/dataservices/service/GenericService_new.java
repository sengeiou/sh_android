package com.fav24.dataservices.service;

import com.fav24.dataservices.dto.GenericResultDto;
import com.fav24.dataservices.dto.GenericDto;


/**
 * Interfaz de servicio Generic. 
 * 
 * @author Fav24
 */
public interface GenericService {

	/**
	 * Ejecuta una petición sobre el servicio Generic.
	 * 
	 * @param generic Estructura generica de operaciones a procesar.
	 * 
	 * @return estructura de resultado.
	 */
	public GenericResultDto processGeneric(GenericDto generic);
}
