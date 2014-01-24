package com.fav24.dataservices.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fav24.dataservices.domain.Requestor;

/**
 * Clase base de todos los obejos de transferencia.
 *  
 * @author Fav24
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class BaseDto implements Serializable {

	private static final long serialVersionUID = 6557890098016497204L;
	
	@JsonInclude(Include.NON_NULL)
	private Requestor requestor;

	/**
	 * Constructor por defecto.
	 */
	public BaseDto() {
		this(null);
	}

	/**
	 * Constructor.
	 * 
	 * @param requestor Quien realiza la petición.
	 */
	public BaseDto(Requestor requestor) {
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
