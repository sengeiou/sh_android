package com.fav24.dataservices.dto;

import com.fav24.dataservices.exception.ServerException;

/**
 * Cualquier método de cualquier controlador que r
 * All Method Controllers must return a transfer object extended from
 * ResultBaseTO. This class contains information about the error code if any.
 * 
 * @author Fav24
 */
public class ResultBaseDto extends BaseDto {

	private static final long serialVersionUID = -6849812750763267868L;

	private String errorCode;
	private String message;


	/**
	 * Constructor por defecto.
	 */
	public ResultBaseDto() {
		this(null, null);
	}
	
	/**
	 * Constructor por defecto.
	 */
	public ResultBaseDto(ServerException e) {
		this(e.getErrorCode(), e.getMessage());
	}

	/**
	 * Constructor.
	 * 
	 * @param errorCode Código de error a asignar a la respuesta.
	 * @param message Mensaje a asignar a la respuesta.
	 */
	public ResultBaseDto(String errorCode, String message) {
		this.errorCode = errorCode;
		this.message = message;
	}

	/**
	 * Retorna el código de error asociado a esta respuesta. 
	 *
	 * @return el código de error asociado a esta respuesta.
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Asigna el código de error asociado a esta respuesta.
	 * 
	 * @param errorCode Código de error a asignar a la respuesta.
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * Retorna el mensaje asociado a esta respuesta. 
	 *
	 * @return el mensaje asociado a esta respuesta.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Asigna el mensaje asociado a esta respuesta.
	 * 
	 * @param message Mensaje a asignar a la respuesta.
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "ResultBaseTO [errorCode=" + errorCode + ", message=" + message + "]";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((errorCode == null) ? 0 : errorCode.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResultBaseDto other = (ResultBaseDto) obj;
		if (errorCode == null) {
			if (other.errorCode != null)
				return false;
		} else if (!errorCode.equals(other.errorCode))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		return true;
	}
}
