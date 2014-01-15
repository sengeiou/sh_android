package com.fav24.dataservices.domain;



/**
 * Clase que contiene la estructura de un solicitate.
 * 
 * @author Fav24
 */
public class Requestor {

	private	Long idDevice;
	private	Long idUser; 
	private Long idPlatform;
	private String appVersion; 
	private Long time;
	
	
	/**
	 * Retorna el identificador de dispositivo.
	 * 
	 * @return el identificador de dispositivo.
	 */
	public Long getIdDevice() {
		return idDevice;
	}
	
	/**
	 * Asigna el identificador de dispositivo.
	 * 
	 * @param idDevice El identificador de dispositivo a asignar.
	 */
	public void setIdDevice(Long idDevice) {
		this.idDevice = idDevice;
	}
	
	/**
	 * Retorna el identificador de usuario.
	 * 
	 * @return el identificador de usuario.
	 */
	public Long getIdUser() {
		return idUser;
	}
	
	/**
	 * Asigna el identificador de usuario.
	 * 
	 * @param idUser El identificador de usuario a asignar.
	 */
	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}
	
	/**
	 * Retorna el identificador de la plataforma. 
	 * 
	 * @return el identificador de la plataforma.
	 */
	public Long getIdPlatform() {
		return idPlatform;
	}
	
	/**
	 * Asigna el identificador de la plataforma.
	 * 
	 * @param idPlatform El identificador de plataforma a asignar.
	 */
	public void setIdPlatform(Long idPlatform) {
		this.idPlatform = idPlatform;
	}
	
	/**
	 * Retorna la versión de la aplicación.
	 * 
	 * @return la versión de la aplicación.
	 */
	public String getAppVersion() {
		return appVersion;
	}
	
	/**
	 * Asigna la versión de la aplicación.
	 * 
	 * @param appVersion La versión a asignar.
	 */
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	
	/**
	 * Retorna los milisegundos transcurridos desde epoch en el que se encuentra el dispositivo, en el momento de la petición.
	 * 
	 * @return los milisegundos transcurridos desde epoch en el que se encuentra el dispositivo, en el momento de la petición.
	 */
	public Long getTime() {
		return time;
	}
	
	/**
	 * Asigna los milisegundos transcurridos desde epoch en el que se encuentra el dispositivo, en el momento de la petición.
	 * 
	 * @param time Los milisegundos a asignar.
	 */
	public void setTime(Long time) {
		this.time = time;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((appVersion == null) ? 0 : appVersion.hashCode());
		result = prime * result
				+ ((idDevice == null) ? 0 : idDevice.hashCode());
		result = prime * result
				+ ((idPlatform == null) ? 0 : idPlatform.hashCode());
		result = prime * result + ((idUser == null) ? 0 : idUser.hashCode());
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
		Requestor other = (Requestor) obj;
		if (appVersion == null) {
			if (other.appVersion != null)
				return false;
		} else if (!appVersion.equals(other.appVersion))
			return false;
		if (idDevice == null) {
			if (other.idDevice != null)
				return false;
		} else if (!idDevice.equals(other.idDevice))
			return false;
		if (idPlatform == null) {
			if (other.idPlatform != null)
				return false;
		} else if (!idPlatform.equals(other.idPlatform))
			return false;
		if (idUser == null) {
			if (other.idUser != null)
				return false;
		} else if (!idUser.equals(other.idUser))
			return false;
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Requestor [idDevice=" + idDevice + ", idUser=" + idUser
				+ ", idPlatform=" + idPlatform + ", appVersion=" + appVersion
				+ ", time=" + time + "]";
	}
}
