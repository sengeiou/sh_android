package com.shootr.android.service.dataservice.generic;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;




/**
 * Clase que contiene la estructura de una petición genérica de acciones sobre entidades.
 * 
 * @author Fav24
 */
public class GenericDto extends BaseDto {

	private static final long serialVersionUID = 2649617444051699918L;

	@JsonInclude(Include.NON_NULL)
	private OperationDto[] ops;

	
	/**
	 * Constructor por defecto.
	 */
	public GenericDto() {
		ops = null;
	}

	
	/**
	 * Retorna el array de operaciones.
	 * 
	 * @return el array de operaciones.
	 */
	public OperationDto[] getOps() {
		return ops;
	}

	/**
	 * Asigna el array de operaciones.
	 * 
	 * @param ops El array de operaciones a asignar.
	 */
	public void setOps(OperationDto[] ops) {
		this.ops = ops;
	}
}
