package com.fav24.dataservices.domain;

import java.util.AbstractList;


/**
 * Clase que contiene la estructura genérica de políticas de acceso sobre entidades.
 * 
 * @author Fav24
 */
public class AccessPolicy {

	private Requestor requestor;
	private AbstractList<EntityAccessPolicy> accessPolicies;

	
	/**
	 * Constructor por defecto.
	 */
	public AccessPolicy() {
		this(null, null);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param requestor Solicitante de las operaciones.
	 * @param accessPolicies Lista de políticas solicitadas.
	 */
	public AccessPolicy(Requestor requestor, AbstractList<EntityAccessPolicy> accessPolicies) {
		this.requestor = requestor;
		this.accessPolicies = accessPolicies;
	}
	
	/**
	 * Retorna el solicitante de las políticas.
	 * 
	 * @return el solicitante de las políticas.
	 */
	public Requestor getRequestor() {
		return requestor;
	}

	/**
	 * Asigna el solicitante de las políticas.
	 * 
	 * @param requestor El solicitante de las políticas.
	 */
	public void setRequestor(Requestor requestor) {
		this.requestor = requestor;
	}

	/**
	 * Retorna la lista de políticas solicitada.
	 * 
	 * @return la lista de políticas solicitada.
	 */
	public AbstractList<EntityAccessPolicy> getAccessPolicies() {
		return accessPolicies;
	}

	/**
	 * Asigna la lista de políticas solicitadas.
	 * 
	 * @param accessPolicies La lista de políticas solicitadas.
	 */
	public void setOperations(AbstractList<EntityAccessPolicy> accessPolicies) {
		this.accessPolicies = accessPolicies;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((accessPolicies == null) ? 0 : accessPolicies.hashCode());
		result = prime * result
				+ ((requestor == null) ? 0 : requestor.hashCode());
		return result;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AccessPolicy other = (AccessPolicy) obj;
		if (accessPolicies == null) {
			if (other.accessPolicies != null)
				return false;
		} else if (!accessPolicies.equals(other.accessPolicies))
			return false;
		if (requestor == null) {
			if (other.requestor != null)
				return false;
		} else if (!requestor.equals(other.requestor))
			return false;
		return true;
	}
}
