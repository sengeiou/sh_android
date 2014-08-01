package com.fav24.dataservices.dto;



/**
 * Objeto de transferencia del resultado de una petición.
 */
public class ResultDto {

	public static final String RESULT_OK = "OK";
	public static final String RESULT_ERROR = "ERROR";
	public static final String RESULT_WARNING = "WARNING";
	
	private String result;
	private String resultCode;
	private String message;
	private String explanation;


	/**
	 * Constructor por defecto.
	 */
	public ResultDto() {
		this(null, null);
	}

	/**
	 * Constructor
	 * 
	 * @param result Resultado de la ejecución.
	 * @param resultCode Código del resultado de la ejecución.
	 */
	public ResultDto(String result, String resultCode) {

		this.setResult(result);
		this.setResultCode(resultCode);
		this.setMessage(null);
		this.setExplanation(null);
	}

	/**
	 * Constructor
	 * 
	 * @param result Resultado de la ejecución.
	 * @param resultCode Código del resultado de la ejecución.
	 * @param message Mensaje asociado al resultado de la ejecución.
	 */
	public ResultDto(String result, String resultCode, String message) {

		this.setResult(result);
		this.setResultCode(resultCode);
		this.setMessage(message);
		this.setExplanation(null);
	}

	/**
	 * Constructor
	 * 
	 * @param result Resultado de la ejecución.
	 * @param resultCode Código del resultado de la ejecución.
	 * @param message Mensaje asociado al resultado de la ejecución.
	 * @param explanation Una explicación completa del resultado de la ejecución. 
	 */
	public ResultDto(String result, String resultCode, String message, String explanation) {

		this.setResult(result);
		this.setResultCode(resultCode);
		this.setMessage(message);
		this.setExplanation(explanation);
	}

	/**
	 * Retorna el resultado de la ejecución.
	 * 
	 * @return el resultado de la ejecución.
	 */
	public String getResult() {
		return result;
	}

	/**
	 * Asigna el resultado de la ejecución.
	 * 
	 * @param result Resultado de la ejecución.
	 */
	public void setResult(String result) {
		this.result = result;
	}
	
	/**
	 * Retorna el código del resultado de la ejecución.
	 * 
	 * @return el código del resultado de la ejecución.
	 */
	public String getResultCode() {
		return resultCode;
	}
	
	/**
	 * Asigna el código del resultado de la ejecución.
	 * 
	 * @param errorCode Código de resultado a asociar.
	 */
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	/**
	 * Retorna el mensaje asociado al resultado de la ejecución.
	 * 
	 *  @return el mensaje asociado al resultado de la ejecución.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Asigna el mensaje asociado al resultado de la ejecución.
	 * 
	 * @param message Mensaje a asignar
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Retorna una explicación completa del resultado de la ejecución.
	 * 
	 * @return una explicación completa del resultado de la ejecución.
	 */
	public String getExplanation() {
		return explanation;
	}

	/**
	 * Asigna una explicación completa del resultado de la ejecución.
	 * 
	 * @param explanation Explicación completa del resultado de la ejecución.
	 */
	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}
}
