package com.fav24.dataservices.dto.security;

import com.fav24.dataservices.dto.BaseDto;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.security.AccessPolicy;


/**
 * Clase que contiene la estructura de una petición de consulta de
 * políticas de acceso
 * 
 * @author Fav24
 */
public class AccessPolicyDto extends BaseDto {

	private static final long serialVersionUID = 2649617444051699918L;

	private AccessPolicy accessPolicy;


	/**
	 * Constructor para el retorno de un error. 
	 *  
	 * @param e Excepción a notificar en la respuesta.
	 */
	public AccessPolicyDto(ServerException e) {
		super(e);
	}
	
	/**
	 * Retorna la estructura de políticas de acceso de las entidades.
	 * 
	 * @return la estructura de políticas de acceso de las entidades.
	 */
	public AccessPolicy getAccessPolicy() {
		return accessPolicy;
	}

	/**
	 * Asigna la estructura de políticas de acceso de las entidades.
	 * 
	 * @param generic La estructura de políticas de acceso de las entidades.
	 */
	public void setAccessPolicy(AccessPolicy accessPolicy) {
		this.accessPolicy = accessPolicy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "AccessPolicyTO [accessPolicy=" + accessPolicy + ", toString()=" + super.toString() + "]";
	}
}
