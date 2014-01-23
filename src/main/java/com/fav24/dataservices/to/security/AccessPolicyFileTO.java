package com.fav24.dataservices.to.security;

import java.net.URL;

import com.fav24.dataservices.to.BaseTO;


/**
 * Clase que contiene la estructura de una petición de carga 
 * de un fichero de definición de políticas de acceso.
 * 
 * @author Fav24
 */
public class AccessPolicyFileTO extends BaseTO {

	private static final long serialVersionUID = 2649617444051699918L;

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
	public String toString() {
		return "AccessPolicyFileTO [url=" + url + ", toString()=" + super.toString() + "]";
	}
}
