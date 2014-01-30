package com.fav24.dataservices.security;

import java.net.MalformedURLException;
import java.net.URL;

import com.fav24.dataservices.domain.Requestor;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.security.AccessPolicyService;


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
	 * Constructor con parámetro.
	 * 
	 * @param accessPolicyURLs URLs de las que se obtuvieron las políticas de acceso.
	 */
	public AccessPolicyFiles(URL[] accessPolicyURLs) {
		this.accessPolicyURLs = accessPolicyURLs;
	}

	/**
	 * Constructor con parámetro.
	 * 
	 * @param accessPolicyURLs URLs de las que se obtuvieron las políticas de acceso.
	 * 
	 * @throws ServerException 
	 */
	public AccessPolicyFiles(String[] accessPolicyURLs) throws ServerException {

		int i=0;

		try {
			if (accessPolicyURLs != null) {

				this.accessPolicyURLs = new URL[accessPolicyURLs.length];
				for (String url : accessPolicyURLs) {
					this.accessPolicyURLs[i++] = new URL(url);
				}
			}
		} catch (MalformedURLException e) {
			throw new ServerException(AccessPolicyService.ERROR_INVALID_URL, String.format(AccessPolicyService.ERROR_INVALID_URL_MESSAGE, accessPolicyURLs[i]));
		}
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
	 * Retorna las URLs de los ficheros que contienen las políticas de acceso.
	 * 
	 * @return las URLs de los ficheros que contienen las políticas de acceso.
	 */
	public URL[] getURLs() {
		return accessPolicyURLs;
	}

	/**
	 * Retorna las URLs de los ficheros que contienen las políticas de acceso.
	 * 
	 * @return las URLs de los ficheros que contienen las políticas de acceso.
	 */
	public String[] getURLsAsStrings() {

		if (accessPolicyURLs != null) {
			String[] accessPolicyURLsAsStrings = new String[accessPolicyURLs.length];

			for(int i=0; i<accessPolicyURLs.length; i++) {
				accessPolicyURLsAsStrings[i] = accessPolicyURLs[i].toExternalForm();
			}

			return accessPolicyURLsAsStrings;
		}

		return null;
	}

	/**
	 * Asigna las URLs de los ficheros que contienen las políticas de acceso.
	 * 
	 * @param accessPolicyURLs URLs de los ficheros que contienen las políticas de acceso a asignar.
	 */
	public void setURLs(URL[] accessPolicyURLs) {
		this.accessPolicyURLs = accessPolicyURLs;
	}
}
