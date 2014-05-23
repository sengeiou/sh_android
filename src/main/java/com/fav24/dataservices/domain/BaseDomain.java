package com.fav24.dataservices.domain;



/**
 * Clase base de todos los objetos de transferencia.
 */
public class BaseDomain {

	private String alias;
	private Requestor requestor;


	/**
	 * Constructor por defecto.
	 */
	public BaseDomain() {

		this.alias = null;
		this.requestor = null;
	}

	/**
	 * Constructor de copia.
	 * 
	 * @param daseDomain Objeto referencia a copiar.
	 */
	public BaseDomain(BaseDomain daseDomain) {

		this.alias = daseDomain.alias;
		this.requestor = daseDomain.requestor;
	}

	/**
	 * Constructor.
	 * 
	 * @param alias Alias de la petición.
	 * @param requestor Quién realiza la petición.
	 */
	public BaseDomain(String alias, Requestor requestor) {

		this.alias = alias;
		this.requestor = requestor;
	}

	/**
	 * Retorna el alias de esta petición.
	 *  
	 * @return el alias de esta petición.
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * Asigna el alias de esta petición.
	 *  
	 * @param alias El alias a asignar.
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * Retorna quién realiza la petición.
	 *  
	 * @return quién realiza la petición.
	 */
	public Requestor getRequestor() {
		return requestor;
	}

	/**
	 * Asigna quien realiza la petición.
	 * 
	 * @param requestor El solicitante.
	 */
	public void setRequestor(Requestor requestor) {
		this.requestor = requestor;
	}
}
