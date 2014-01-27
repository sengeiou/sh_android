package com.fav24.dataservices.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fav24.dataservices.exception.ServerException;

/**
 * Clase base de todos los obejos de transferencia.
 *  
 * @author Fav24
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class BaseDto implements Serializable {

	private static final long serialVersionUID = 6557890098016497204L;
	
	@JsonInclude(Include.NON_NULL)
	private String statusCode;
	@JsonInclude(Include.NON_NULL)
	private String statusMessage;
	
	@JsonInclude(Include.NON_NULL)
	@JsonUnwrapped(enabled=true)
	private RequestorDto requestor;

	/**
	 * Constructor por defecto.
	 */
	public BaseDto() {

		this.statusCode = null;
		this.statusMessage = null;
		this.requestor = null;
	}

	/**
	 * Constructor.
	 * 
	 * @param requestor Quien realiza la petición.
	 */
	public BaseDto(RequestorDto requestor) {
		
		this.statusCode = null;
		this.statusMessage = null;
		this.requestor = requestor;
	}
	
	/**
	 * Constructor por defecto.
	 */
	public BaseDto(ServerException e) {
		this(e.getErrorCode(), e.getMessage());
	}

	/**
	 * Constructor.
	 * 
	 * @param statusCode Código de estado a asignar a la respuesta.
	 * @param statusMessage Mensaje a asignar a la respuesta.
	 */
	public BaseDto(String statusCode, String statusMessage) {
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
	}
	
	/**
	 * Retorna quién realiza la petición.
	 *  
	 * @return quién realiza la petición.
	 */
	public RequestorDto getRequestor() {
		return requestor;
	}

	/**
	 * Asigna quien realiza la petición.
	 * 
	 * @param requestor El solicitante.
	 */
	public void setRequestor(RequestorDto requestor) {
		this.requestor = requestor;
	}

	/**
	 * Retorna el código de estado asociado a esta respuesta. 
	 *
	 * @return el código de estado asociado a esta respuesta.
	 */
	public String getStatusCode() {
		return statusCode;
	}

	/**
	 * Asigna el código de estado asociado a esta respuesta.
	 * 
	 * @param statusCode Código de estado a asignar a la respuesta.
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * Retorna el mensaje asociado a esta respuesta. 
	 *
	 * @return el mensaje asociado a esta respuesta.
	 */
	public String getStatusMessage() {
		return statusMessage;
	}

	/**
	 * Asigna el mensaje asociado a esta respuesta.
	 * 
	 * @param statusMessage Mensaje a asignar a la respuesta.
	 */
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "BaseTO [requestor=" + requestor + "]";
	}
}
