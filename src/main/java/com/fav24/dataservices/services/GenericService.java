package com.fav24.dataservices.services;

import com.fav24.dataservices.to.GenericResultTO;
import com.fav24.dataservices.to.GenericTO;


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
	public GenericResultTO processGeneric(GenericTO generic);
}
