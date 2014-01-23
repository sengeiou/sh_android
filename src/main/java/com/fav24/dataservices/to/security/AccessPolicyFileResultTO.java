package com.fav24.dataservices.to.security;

import java.net.URL;

import com.fav24.dataservices.to.ResultBaseTO;


/**
 * Clase que contiene la estructura de respuesta a una petición de carga de
 * políticas de acceso 
 * 
 * @author Fav24
 */
public class AccessPolicyFileResultTO extends ResultBaseTO {
	
	private static final long serialVersionUID = -3480334905166868119L;
	
	private URL url;

	/**
	 * Retorna la url del fichero de definición de políticas de acceso.
	 * 
	 * @return la url del fichero de definición de políticas de acceso.
	 */
	public URL getURL() {
		return url;
	}

	/**
	 * Asigna la url del fichero de definición de políticas de acceso.
	 * 
	 * @param generic La url a asignar.
	 */
	public void setURL(URL url) {
		this.url = url;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		AccessPolicyFileResultTO other = (AccessPolicyFileResultTO) obj;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "AccessPolicyFileResultTO [url=" + url + ", toString()=" + super.toString() + "]";
	}
}
