package com.fav24.dataservices.security;

import java.net.URL;

import com.fav24.dataservices.domain.Requestor;


/**
 * Clase que contiene la estructura genérica de políticas de acceso sobre entidades.
 * 
 * @author Fav24
 */
public class AccessPolicyFiles {

	private Requestor requestor;
	private URL[] accessPolicyURLs;


	/**
	 * Constructor por defecto.
	 */
	public AccessPolicyFiles() {
		this((URL[])null);
	}

	/**
	 * Constructor por defecto.
	 * 
	 * @param accessPolicyURLs URLs de las que se obtuvieron las políticas de acceso.
	 */
	public AccessPolicyFiles(URL[] accessPolicyURLs) {
		this.accessPolicyURLs = accessPolicyURLs;
	}

	/**
	 * Constructor de copia.
	 * 
	 * @param accesPolicy Objeto referencia a copiar.
	 */
	public AccessPolicyFiles(AccessPolicyFiles accesPolicy) {

		if (accesPolicy.accessPolicyURLs != null) {

			accessPolicyURLs = new URL[accessPolicyURLs.length];
			System.arraycopy(accesPolicy.accessPolicyURLs, 0, accessPolicyURLs, 0, accesPolicy.accessPolicyURLs.length);
			requestor = new Requestor(accesPolicy.requestor);
		}
		else {

			accessPolicyURLs = null;
		}
		
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
	 * Retorna las URLs de los fichero que contienen las políticas de acceso.
	 * 
	 * @return las URLs de los fichero que contienen las políticas de acceso.
	 */
	public URL[] getURLs() {
		return accessPolicyURLs;
	}
}
