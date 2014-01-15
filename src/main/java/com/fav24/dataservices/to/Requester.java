package com.fav24.dataservices.to;

import java.io.Serializable;

/**
 * Clase utilizada en BaseTO para identificar quién hace cada peticion.
 * 
 * @author Fav24
 */
public class Requester implements Serializable {

	private static final long serialVersionUID = -2084825771568303345L;

	private Long idDevice;
	private Long idUser;

	
	/**
	 * Contructor por defecto.
	 */
	public Requester() {
		this(null, null);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param idDevice Identificador del dispositivo que realiza una cierta petición.
	 * @param idUser Identificador del usuario del dispositivo.
	 */
	public Requester(Long idDevice, Long idUser){
		this.idDevice = idDevice;
		this.idUser = idUser;
	}
	
	/**
	 * Retorna el identificador de dispositivo.
	 * 
	 * @return identificador de dispositivo
	 */
	public Long getIdDevice() {
		return idDevice;
	}

	/**
	 * Asigna identificador de dispositivo.
	 * 
	 * @param idDevice identificador de dispositivo.
	 */
	public void setIdDevice(Long idDevice) {
		this.idDevice = idDevice;
	}

	/**
	 * Retorna el identificador de usuario.
	 * 
	 * @return identificador de usuario.
	 */
	public Long getIdUser() {
		return idUser;
	}

	/**
	 * Asigna identificador de usuario.
	 * @param idUser identificador de usuario.
	 */
	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "Requester [idDevice=" + idDevice + ", idUser=" + idUser + "]";
	}
}
