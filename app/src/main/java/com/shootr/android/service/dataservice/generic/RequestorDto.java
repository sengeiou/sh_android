package com.shootr.android.service.dataservice.generic;





/**
 * Clase que contiene la estructura de un solicitante.
 * 
 * Nota importante: La actualización del tiempo del sistema, debe delegarse a los controladores, 
 * justo antes de enviar las repuestas con el fin de que sea lo más preciso posible, sin poder eliminar el retardo por comunicaciones.
 *  
 * @author Fav24
 */
public class RequestorDto {

	public static final int POSITION_ID_DEVICE = 0;
	public static final int POSITION_ID_USER = 1;
	public static final int POSITION_ID_PLATFORM = 2;
	public static final int POSITION_APP_VERSION = 3;
	public static final int POSITION_SYSTEM_TIME = 4;

	public static final int REQUESTOR_ARRAY_LENGTH = POSITION_SYSTEM_TIME + 1;

	private	Object[] req;


	/**
	 * Constructor por defecto.
	 */
	public RequestorDto() {
		req = new Object[REQUESTOR_ARRAY_LENGTH];
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
	 * Constructor de copia.
	 * 
	 * @param requestor Objeto de referencia.
	 */
	public RequestorDto(RequestorDto requestor) {
		
		this();
		
		copy(requestor);
	}
	
	/**
	 * Retorna el array de valores del solicitante.
	 * 
	 * @return el array de valores del solicitante.
	 */
	public Object[] getReq() {
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

	/**
	 * Copia quién realiza la petición.
	 * 
	 * @param requestor El solicitante a copiar.
	 */
	public void copy(RequestorDto requestor) {

		if (req != null && requestor != null && requestor.req != null) {

			int length = requestor.req.length < req.length ? requestor.req.length : req.length;
			
			for(int i=0; i<length; i++) {

				req[i] = requestor.req[i];
			}
		}
	}
}
