package com.fav24.dataservices.domain.cache;



/**
 * Configuración de la persistencia de una caché.
 */
public class Persistence
{
	private Boolean active;


	/**
	 * Constructor con parámetro.
	 * 
	 * @param active Indica si la persistencia está o no activa para la caché a la
	 * que pertenece esta configuración de persistencia.
	 */
	public Persistence(Boolean active) {

		this.active = active;
	}

	/**
	 * Retorna true o false en función de si la persistencia a disco, está o no activada.
	 * 
	 * @return true o false en función de si la persistencia a disco, está o no activada.
	 */
	public Boolean isActive() {
		return active;
	}
	
	/**
	 * Asigna true o false en función de si la persistencia a disco, está o no activada.
	 * 
	 * @param active Indica si la persistencia está o no activa para la caché a la
	 * que pertenece esta configuración de persistencia.
	 */
	public void setActive(Boolean active) {
		this.active = active;
	}
}
