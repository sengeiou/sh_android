package com.fav24.dataservices.domain.policy;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import com.fav24.dataservices.domain.Requestor;
import com.fav24.dataservices.exception.ServerException;


/**
 * Clase que contiene la estructura genérica de políticas de acceso sobre entidades.
 */
public class RemoteFiles {

	public static final String ERROR_INVALID_URL = "RF003";
	public static final String ERROR_INVALID_URL_MESSAGE = "La URL <%s> no es válida.";

	private Requestor requestor;
	private URL[] urls;


	/**
	 * Constructor por defecto.
	 */
	public RemoteFiles() {
		this((URL[])null);
	}

	/**
	 * Constructor con parámetros.
	 * 
	 * @param files Conjunto de ficheros a incluir.
	 * 
	 * @throws ServerException 
	 */
	public RemoteFiles(File [] files) throws ServerException {

		URL[] urls = null;

		if (files != null && files.length > 0) {

			urls = new URL[files.length];

			int i = 0;
			
			try {
				for (; i<files.length; i++) {
					urls[i] = files[i].toURI().toURL();
				}
			} catch (MalformedURLException e) {
				throw new ServerException(ERROR_INVALID_URL, String.format(ERROR_INVALID_URL_MESSAGE, files[i]));
			}
		}

		setURLs(urls);
	}

	/**
	 * Constructor con parámetro.
	 * 
	 * @param urls URLs de acceso a los ficheros.
	 */
	public RemoteFiles(URL[] urls) {
		this.urls = urls;
	}

	/**
	 * Constructor con parámetro.
	 * 
	 * @param urls URLs de las que se obtuvieron las políticas de acceso.
	 * 
	 * @throws ServerException 
	 */
	public RemoteFiles(String[] urls) throws ServerException {

		int i=0;

		try {
			if (urls != null) {

				this.urls = new URL[urls.length];
				for (String url : urls) {
					this.urls[i++] = new URL(url);
				}
			}
		} catch (MalformedURLException e) {
			throw new ServerException(ERROR_INVALID_URL, String.format(ERROR_INVALID_URL_MESSAGE, urls[i]));
		}
	}

	/**
	 * Constructor de copia.
	 * 
	 * @param remoteFiles Objeto referencia a copiar.
	 */
	public RemoteFiles(RemoteFiles remoteFiles) {

		if (remoteFiles.urls != null) {

			urls = new URL[urls.length];
			System.arraycopy(remoteFiles.urls, 0, urls, 0, remoteFiles.urls.length);
			requestor = new Requestor(remoteFiles.requestor);
		}
		else {

			urls = null;
		}

		if (remoteFiles.requestor != null) {

			requestor = new Requestor(remoteFiles.requestor);
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
	 * Retorna las URLs de los ficheros remotos.
	 * 
	 * @return las URLs de los ficheros remotos.
	 */
	public URL[] getURLs() {
		return urls;
	}

	/**
	 * Retorna las URLs de los ficheros remotos.
	 * 
	 * @return las URLs de los ficheros remotos.
	 */
	public String[] getURLsAsStrings() {

		if (urls != null) {
			String[] accessPolicyURLsAsStrings = new String[urls.length];

			for(int i=0; i<urls.length; i++) {
				accessPolicyURLsAsStrings[i] = urls[i].toExternalForm();
			}

			return accessPolicyURLsAsStrings;
		}

		return null;
	}

	/**
	 * Asigna las URLs de los ficheros a denotar.
	 * 
	 * @param urls URLs de los ficheros a asignar.
	 */
	public void setURLs(URL[] urls) {
		this.urls = urls;
	}
}
