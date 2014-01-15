package com.fav24.dataservices.exception;

/**
 * Excepción general de servidor.
 * 
 * @author Fav24
 */
public class ServerException extends Exception {

	private static final long serialVersionUID = 7715129933110523169L;
	
	private String errorCode;
	private String message;

	/**
	 * Constructor por defecto.
	 */
	public ServerException() {
		this(null);
	}
	
	/**
	 * Constructor
	 * 
	 * @param errorCode Código de error inicial de la excepción.
	 */
	public ServerException(String errorCode) {
		this(errorCode, null);
	}
	
	/**
	 * Constructor
	 * 
	 * @param errorCode Código de error inicial de la excepción.
	 */
	public ServerException(String errorCode, String message) {
		this.errorCode = errorCode;
		this.setMessage(message);
	}
	
	/**
	 * Retorna el código de error asociado a la excepción.
	 * 
	 * @return el código de error asociado a la excepción.
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Asigna el código de error asociado a la excepción.
	 * 
	 * @param errorCode Código de error a asociar.
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * Retorna el mensaje de error asociado a la excepción.
	 * 
	 *  @return el mensaje de error asociado a la excepción.
	 */
	public String getMessage() {
		return message == null ? super.getMessage() : message;
	}

	/**
	 * Asigna el mensaje de error asociado a la excepción.
	 * 
	 * @param message Mensaje a asignar
	 */
	public void setMessage(String message) {
		this.message = message;
	}
}
