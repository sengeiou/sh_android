package com.fav24.dataservices.dto.exception;


import com.fav24.dataservices.dto.ResultBaseDto;

/**
 * Excepción en forma de objecto de transferencia, para ser retornado.
 * 
 * @author Fav24
 */
public class ExceptionDto extends ResultBaseDto {

	private static final long serialVersionUID = -4141405994855019539L;

	private String exceptionMessage;

	/**
	 * Constructor por defecto.
	 */
	public ExceptionDto() {
		this(null, null);
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
	} 
}
