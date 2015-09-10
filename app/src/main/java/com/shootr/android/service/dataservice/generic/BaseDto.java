package com.shootr.android.service.dataservice.generic;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/**
 * Clase base de todos los objetos de transferencia.
 *  
 * @author Fav24
 */
public class BaseDto implements Serializable {

	private static final long serialVersionUID = 6557890098016497204L;

	private static final String STATUS_CODE = "code";
	private static final String STATUS_MESSAGE = "message";
	private static final String STATUS_SUBCODE = "subcode";


	public static final int POSITION_ID_DEVICE = 0;
	public static final int POSITION_ID_USER = 1;
	public static final int POSITION_ID_PLATFORM = 2;
	public static final int POSITION_APP_VERSION = 3;
	public static final int POSITION_SYSTEM_TIME = 4;

	private String alias;
	private Map<String, String> status;
	private Object[] req;

	/**
	 * Constructor por defecto.
	 */
	public BaseDto() {
		this.alias = null;
		this.status = new TreeMap<>();
		this.req = null;
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
	public Object[] getReq() {
		return req;
	}

	/**
	 * Asigna quien realiza la petición.
	 * 
	 * @param req El solicitante.
	 */
	public void setReq(Object[] req) {
		this.req = req;
	}

	public void setReq(Long idDevice, Long idUser, Long idPlatform, Long appVersion, Long systemTime) {
		req = new Object[5];
		req[POSITION_ID_DEVICE] = null;
		req[POSITION_ID_USER] = null;
		req[POSITION_ID_PLATFORM] = idPlatform;
		req[POSITION_APP_VERSION] = appVersion;
		req[POSITION_SYSTEM_TIME] = systemTime;
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

    public String getStatusSubcode() {
        return this.status.get(STATUS_SUBCODE);
    }

    public void setStatusSubcode(String statusSubcode) {
        this.status.put(STATUS_SUBCODE, statusSubcode);
    }
}
