package com.fav24.dataservices.to;

import java.io.Serializable;

/**
 * Clase base de todos los obejos de transferencia.
 *  
 * @author Fav24
 */
public class BaseTO implements Serializable {

	private static final long serialVersionUID = 6557890098016497204L;

	private Requester requester;

	/**
	 * Constructor por defecto.
	 */
	public BaseTO() {
		this(null);
	}

	/**
	 * Constructor.
	 * 
	 * @param requester Quien realiza la petición.
	 */
	public BaseTO(Requester requester) {
		this.requester = requester;
	}

	/**
	 * Retorna quién realiza la petición.
	 *  
	 * @return quién realiza la petición.
	 */
	public Requester getRequester() {
		return requester;
	}

	/**
	 * Asigna quien realiza la petición.
	 * 
	 * @param requester El solicitante.
	 */
	public void setRequester(Requester requester) {
		this.requester = requester;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "BaseTO [requester=" + requester + "]";
	}
}
