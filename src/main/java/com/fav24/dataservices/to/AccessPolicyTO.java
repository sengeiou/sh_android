package com.fav24.dataservices.to;

import com.fav24.dataservices.security.AccessPolicy;


/**
 * Clase que contiene la estructura de una petición genérica de acciones sobre entidades.
 * 
 * @author Fav24
 */
public class AccessPolicyTO extends BaseTO {

	private static final long serialVersionUID = 2649617444051699918L;

	private AccessPolicy accessPolicy;


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
		return "GenericTO [accessPolicy=" + accessPolicy + ", toString()=" + super.toString() + "]";
	}
}
