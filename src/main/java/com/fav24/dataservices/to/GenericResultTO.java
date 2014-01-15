package com.fav24.dataservices.to;

import com.fav24.dataservices.domain.Generic;


/**
 * 
 * 
 * @author Fav24
 */
public class GenericResultTO extends ResultBaseTO {
	
	private static final long serialVersionUID = -3480334905166868119L;
	
	private  Generic generic;
	
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
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((generic == null) ? 0 : generic.hashCode());
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
		GenericResultTO other = (GenericResultTO) obj;
		if (generic == null) {
			if (other.generic != null)
				return false;
		} else if (!generic.equals(other.generic))
			return false;
		return true;
	}
}
