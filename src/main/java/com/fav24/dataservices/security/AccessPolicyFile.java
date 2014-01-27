package com.fav24.dataservices.security;

import java.net.URL;

import com.fav24.dataservices.domain.Requestor;


/**
 * Clase que contiene la estructura genérica de políticas de acceso sobre entidades.
 * 
 * @author Fav24
 */
public class AccessPolicyFile {

	private Requestor requestor;
	private URL accessPolicyURL;


	/**
	 * Constructor por defecto.
	 */
	public AccessPolicyFile() {
		this((URL)null);
	}

	/**
	 * Constructor por defecto.
	 * 
	 * @param accessPolicyURL URL de la que se obtuvieron las políticas de acceso.
	 */
	public AccessPolicyFile(URL accessPolicyURL) {
		this.accessPolicyURL = accessPolicyURL;
	}

	/**
	 * Constructor de copia.
	 * 
	 * @param accesPolicy Objeto referencia a copiar.
	 */
	public AccessPolicyFile(AccessPolicyFile accesPolicy) {
		accessPolicyURL = accesPolicy.accessPolicyURL;

		if (accesPolicy.requestor != null) {

			requestor = new Requestor(accesPolicy.requestor);
		}
		else {

			requestor = null;
		}
	}

	/**
	 * Retorna el solicitante.
	 * 
	 * @return el solicitante.
	 */
	public Requestor getRequestor() {
		return requestor;
	}

	/**
	 * Asigna el solicitante.
	 * 
	 * @param requestor El solicitante a asignar.
	 */
	public void setRequestor(Requestor requestor) {
		this.requestor = requestor;
	}

	/**
	 * Retorna la URL del fichero que contiene estas políticas de acceso.
	 * 
	 * @return la URL del fichero que contiene estas políticas de acceso.
	 */
	public URL getURL() {
		return accessPolicyURL;
	}
}
