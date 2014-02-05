package com.fav24.dataservices.dto;

import com.fav24.dataservices.exception.ServerException;



/**
 * Clase que contiene la estructura de una petición genérica de acciones sobre entidades.
 * 
 * @author Fav24
 */
public class GenericDto extends BaseDto {

	private static final long serialVersionUID = 2649617444051699918L;

	private OperationDto[] ops;

	
	/**
	 * Constructor por defecto.
	 */
	public GenericDto() {
		ops = null;
	}
	
	/**
	 * Constructor para el retorno de un error. 
	 *  
	 * @param e Excepción a notificar en la respuesta.
	 */
	public GenericDto(ServerException e) {
		super(e);
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
