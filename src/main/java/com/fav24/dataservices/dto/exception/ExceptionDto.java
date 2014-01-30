package com.fav24.dataservices.dto.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fav24.dataservices.dto.BaseDto;
import com.fav24.dataservices.exception.ServerException;



/**
 * Excepción en forma de objecto de transferencia, para ser retornado.
 * 
 * @author Fav24
 */
public class ExceptionDto extends BaseDto {

	private static final long serialVersionUID = -4141405994855019539L;

	@JsonIgnore
	private String exceptionMessage;

	/**
	 * Constructor por defecto.
	 */
	public ExceptionDto() {
		this(null, null);
	}

	/**
	 * Constructor para el retorno de un error. 
	 *  
	 * @param e Excepción a notificar en la respuesta.
	 */
	public ExceptionDto(ServerException e) {
		super(e);
		
		setStatusCode(e.getErrorCode());
		setStatusMessage(e.getMessage());
	}
	
	/**
	 * Constructor.
	 * 
	 * @param errorCode Código de error asociado al mensaje.
	 * @param message Texto del mensaje.
	 */
	public ExceptionDto(String errorCode, String message) {
		this(errorCode, message, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param errorCode Código de error asociado al mensaje.
	 * @param message Texto del mensaje.
	 * @param exceptionMessage Mensaje asociado a la excepción.
	 */
	public ExceptionDto(String errorCode, String message, String exceptionMessage) {
		super(errorCode, message);

		this.exceptionMessage = exceptionMessage;

		setStatusCode(errorCode);
		setStatusMessage(exceptionMessage);
	}

	/**
	 * Retorna el mensaje asociado a la excepción.
	 * 
	 * @return el mensaje asociado a la excepción.
	 */
	public String getExceptionMessage() {
		return exceptionMessage;
	}

	/**
	 * Asigna el mensaje asociado a la excepción.
	 * 
	 * @param exceptionMessage Mensaje asociado a la excepción a signar.
	 */
	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;

		setStatusMessage(exceptionMessage);
	} 
}
