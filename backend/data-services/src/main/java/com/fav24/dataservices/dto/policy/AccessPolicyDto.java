package com.fav24.dataservices.dto.policy;

import com.fav24.dataservices.dto.BaseDto;
import com.fav24.dataservices.exception.ServerException;


/**
 * Clase que contiene la estructura de una petición de consulta de
 * políticas de acceso
 */
public class AccessPolicyDto extends BaseDto {

	private static final long serialVersionUID = 2649617444051699918L;

	private EntityAccessPolicyDto[] policies;


	/**
	 * Constructor por defecto.
	 */
	public AccessPolicyDto() {
		policies = null;
	}
	
	/**
	 * Constructor para el retorno de un error. 
	 *  
	 * @param e Excepción a notificar en la respuesta.
	 */
	public AccessPolicyDto(ServerException e) {
		super(e);
	}
	
	/**
	 * Retorna el array de estructuras de políticas de acceso de las entidades.
	 * 
	 * @return el array de estructuras de políticas de acceso de las entidades.
	 */
	public EntityAccessPolicyDto[] getAccessPolicies() {
		return policies;
	}

	/**
	 * Asigna el array de estructuras de políticas de acceso de las entidades.
	 * 
	 * @param policies El array de estructuras de políticas de acceso de las entidades a asignar.
	 */
	public void setAccessPolicies(EntityAccessPolicyDto[] policies) {
		this.policies = policies;
	}
}
