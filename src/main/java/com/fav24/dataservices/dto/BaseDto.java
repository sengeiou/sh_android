package com.fav24.dataservices.dto;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fav24.dataservices.exception.ServerException;

/**
 * Clase base de todos los objetos de transferencia.
 *  
 * @author Fav24
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonAutoDetect(
		creatorVisibility = JsonAutoDetect.Visibility.ANY,
		fieldVisibility = JsonAutoDetect.Visibility.ANY, 
		getterVisibility = JsonAutoDetect.Visibility.NONE, 
		isGetterVisibility = JsonAutoDetect.Visibility.NONE, 
		setterVisibility = JsonAutoDetect.Visibility.NONE)
public class BaseDto implements Serializable {

	private static final long serialVersionUID = 6557890098016497204L;

	private static final String STATUS_CODE = "code";
	private static final String STATUS_MESSAGE = "message";

	public static final String ERROR_REQUESTOR_CHECK_FAILED = "R000";
	public static final String ERROR_REQUESTOR_CHECK_FAILED_MESSAGE = "No se ha indicado información del solicitante.";
	public static final String ERROR_REQUESTOR_CHECK_INFORMATION_FAILED = "R001";
	public static final String ERROR_REQUESTOR_CHECK_INFORMATION_FAILED_MESSAGE = "La información del solicitante es incorrecta.";

	private String alias;
	private Map<String, String> status;
	@JsonUnwrapped(enabled=true)
	private RequestorDto requestor;

	/**
	 * Constructor por defecto.
	 */
	public BaseDto() {

		this.alias = null;
		this.status = new TreeMap<String, String>();
		this.requestor = null;
	}

	/**
	 * Constructor.
	 * 
	 * @param requestor Quién realiza la petición.
	 */
	public BaseDto(RequestorDto requestor) {

		this.alias = null;
		this.status = new TreeMap<String, String>();
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

		this.status = new TreeMap<String, String>();

		this.status.put(STATUS_CODE, statusCode);
		this.status.put(STATUS_MESSAGE, statusMessage);
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
	 * Retorna el mapa que contiene el estado de la información contenida en el Dto.
	 *  
	 * @return el mapa que contiene el estado de la información contenida en el Dto.
	 */
	public Map<String, String> getStatus() {
		return status;
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
		return status.get(STATUS_CODE);
	}

	/**
	 * Asigna el código de estado asociado a esta respuesta.
	 * 
	 * @param statusCode Código de estado a asignar a la respuesta.
	 */
	public void setStatusCode(String statusCode) {
		this.status.put(STATUS_CODE, statusCode);
	}

	/**
	 * Retorna el mensaje asociado a esta respuesta. 
	 *
	 * @return el mensaje asociado a esta respuesta.
	 */
	public String getStatusMessage() {
		return this.status.get(STATUS_MESSAGE);
	}

	/**
	 * Asigna el mensaje asociado a esta respuesta.
	 * 
	 * @param statusMessage Mensaje a asignar a la respuesta.
	 */
	public void setStatusMessage(String statusMessage) {
		this.status.put(STATUS_MESSAGE, statusMessage);
	}

	/**
	 * Lanza una excepción en caso de que la información base del DTO
	 * se considere inválida.
	 */
	public void checkHeader() throws ServerException {

		//Comprobación de la información del solicitante.
		if (requestor == null || requestor.getReq() == null) {
			throw new ServerException(ERROR_REQUESTOR_CHECK_FAILED, ERROR_REQUESTOR_CHECK_FAILED_MESSAGE);
		}

		if (requestor.getReq().length != RequestorDto.REQUESTOR_ARRAY_LENGTH) {
			throw new ServerException(ERROR_REQUESTOR_CHECK_INFORMATION_FAILED, ERROR_REQUESTOR_CHECK_INFORMATION_FAILED_MESSAGE);
		}
	}
}
