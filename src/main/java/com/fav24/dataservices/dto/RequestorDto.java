package com.fav24.dataservices.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;




/**
 * Clase que contiene la estructura de un solicitante.
 * 
 * Nota importante: La actualización del tiempo del sistema, debe delegarse a los controladores, 
 * justo antes de enviar las repuestas con el fin de que sea lo más preciso posible, sin poder eliminar el retardo por comunicaciones.
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
public class RequestorDto {

	public static final int POSITION_ID_DEVICE = 0;
	public static final int POSITION_ID_USER = 1;
	public static final int POSITION_ID_PLATFORM = 2;
	public static final int POSITION_APP_VERSION = 3;
	public static final int POSITION_SYSTEM_TIME = 4;

	public static final int REQUESTOR_ARRAY_LENGTH = POSITION_SYSTEM_TIME + 1;

	private	Long[] req;


	/**
	 * Constructor por defecto.
	 */
	public RequestorDto() {
		req = new Long[REQUESTOR_ARRAY_LENGTH];
	}

	/**
	 * Constructor completo.
	 * 
	 * @param idDevice Identificador del dispositivo.
	 * @param idUser Identificador de usuario.
	 * @param idPlatform Identificador de plataforma.
	 * @param appVersion Versión de la aplicación.
	 * @param systemTime Timestamp del sistema.
	 */
	public RequestorDto(Long idDevice, Long idUser, Long idPlatform, Long appVersion, Long systemTime) {

		this();

		req[POSITION_ID_DEVICE] = idDevice;
		req[POSITION_ID_USER] = idUser;
		req[POSITION_ID_PLATFORM] = idPlatform;
		req[POSITION_APP_VERSION] = appVersion;
		req[POSITION_SYSTEM_TIME] = systemTime;
	}

	/**
	 * Retorna el array de valores del solicitante.
	 * 
	 * @return el array de valores del solicitante.
	 */
	public Long[] getReq() {
		return req;
	}

	/**
	 * Asigna el array de valores del solicitante.
	 * 
	 * @param req El array de valores del solicitante a asignar.
	 */
	public void setReq(Long[] req) {
		this.req = req;
	}
	
	/**
	 * Asigna la hora de sistema en milisegundos.
	 * 
	 * @param milliseconds Milisegundos después de epoch.
	 */
	public void setSystemTime(Long milliseconds) {
		req[POSITION_SYSTEM_TIME] = milliseconds;
	}
}
