package com.fav24.dataservices.dto.security;

import java.net.URL;

import com.fav24.dataservices.dto.ResultBaseDto;
import com.fav24.dataservices.exception.ServerException;


/**
 * Clase que contiene la estructura de respuesta a una petición de carga de
 * políticas de acceso 
 * 
 * @author Fav24
 */
public class AccessPolicyFileResultDto extends ResultBaseDto {
	
	private static final long serialVersionUID = -3480334905166868119L;
	
	private URL url;

	
	/**
	 * Constructor por defecto.
	 */
	public AccessPolicyFileResultDto() {
	}
	
	/**
	 * Constructor de excepción.
	 * 
	 * @param e Excepción a retornar.
	 */
	public AccessPolicyFileResultDto(ServerException e) {
		super(e);
	}
	
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
		AccessPolicyFileResultDto other = (AccessPolicyFileResultDto) obj;
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
