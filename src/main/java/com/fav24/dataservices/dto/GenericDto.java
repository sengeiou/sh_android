package com.fav24.dataservices.dto;

import com.fav24.dataservices.domain.Generic;


/**
 * Clase que contiene la estructura de una petición genérica de acciones sobre entidades.
 * 
 * @author Fav24
 */
public class GenericDto extends BaseDto {

	private static final long serialVersionUID = 2649617444051699918L;

	private Generic generic;


	/**
	 * Retorna la estructura genérica de operaciones con entidades.
	 * 
	 * @return la estructura genérica de operaciones con entidades.
	 */
	public Generic getGeneric() {
		return generic;
	}

	/**
	 * Asigna la estructura genérica de operaciones con entidades.
	 * 
	 * @param generic La estructura genérica de operaciones a asignar.
	 */
	public void setGeneric(Generic generic) {
		this.generic = generic;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "GenericTO [generic=" + generic + ", toString()=" + super.toString() + "]";
	}
}
