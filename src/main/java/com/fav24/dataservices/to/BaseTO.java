package com.fav24.dataservices.to;

import java.io.Serializable;

import com.fav24.dataservices.domain.Requestor;

/**
 * Clase base de todos los obejos de transferencia.
 *  
 * @author Fav24
 */
public class BaseTO implements Serializable {

	private static final long serialVersionUID = 6557890098016497204L;

	private Requestor requestor;

	/**
	 * Constructor por defecto.
	 */
	public BaseTO() {
		this(null);
	}

	/**
	 * Constructor.
	 * 
	 * @param requestor Quien realiza la petición.
	 */
	public BaseTO(Requestor requestor) {
		this.requestor = requestor;
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "BaseTO [requestor=" + requestor + "]";
	}
}
