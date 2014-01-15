package com.fav24.dataservices.to;

import com.fav24.dataservices.domain.AccessPolicy;


/**
 * 
 * 
 * @author Fav24
 */
public class AccessPolicyResultTO extends ResultBaseTO {
	
	private static final long serialVersionUID = -3480334905166868119L;
	
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
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((accessPolicy == null) ? 0 : accessPolicy.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AccessPolicyResultTO other = (AccessPolicyResultTO) obj;
		if (accessPolicy == null) {
			if (other.accessPolicy != null)
				return false;
		} else if (!accessPolicy.equals(other.accessPolicy))
			return false;
		return true;
	}
}
