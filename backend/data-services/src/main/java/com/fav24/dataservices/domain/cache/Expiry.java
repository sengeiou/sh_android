package com.fav24.dataservices.domain.cache;



/**
 * Configuración de los tiempos de vida de un elemento.
 */
public class Expiry
{
	public static final Long DEFAULT_TIME_TO_IDLE_SECONDS = 120L;
	public static final Long DEFAULT_TIME_TO_LIVE_SECONDS = 120L;
	public static final Boolean DEFAULT_ETERNAL = false;

	private Boolean eternal;
	private Long timeToIdleSeconds;
	private Long timeToLiveSeconds;


	/**
	 * Constructor con parámetro.
	 * 
	 * @param eternal Indica si los elementos deben perdurar el paso del tiempo.
	 */
	public Expiry(Boolean eternal) {

		this.eternal = eternal;
	}

	/**
	 * Retorna true o false en función de si los elementos de la caché 
	 * están o no afectados por el paso del tiempo.
	 * 
	 * Nota: Si esta propiedad se activa, se ignoran los valores de TimeToIdleSeconds y TimeToLiveSeconds.
	 * 
	 * @return true o false en función de si los elementos de la caché 
	 * están o no afectados por el paso del tiempo.
	 */
	public Boolean isEternal() {
		return eternal;
	}
	
	/**
	 * Asigna true o false en función de si los elementos de la caché 
	 * están o no afectados por el paso del tiempo.
	 * 
	 * Nota: Si esta propiedad se activa, se ignoran los valores de TimeToIdleSeconds y TimeToLiveSeconds.
	 * 
	 * @param eternal True o false en función de si los elementos de la caché se consideran o no eternos. 
	 */
	public void setEternal(Boolean eternal) {
		this.eternal = eternal;
	}
	
	/**
	 * Retorna el tiempo máximo de vida en segundos de un elemento que no está siendo consultado.
	 *   
	 * @return el tiempo máximo de vida en segundos de un elemento que no está siendo consultado.
	 */
	public Long getTimeToIdleSeconds() {

		if (timeToIdleSeconds != null) {
			return timeToIdleSeconds;
		}

		return DEFAULT_TIME_TO_IDLE_SECONDS; 
	}

	/**
	 * Asigna el tiempo máximo de vida en segundos de un elemento que no está siendo consultado.
	 * 
	 * @param timeToIdleSeconds El tiempo máximo de vida en segundos a asignar.
	 */
	public void setTimeToIdleSeconds(Long timeToIdleSeconds) {
		this.timeToIdleSeconds = timeToIdleSeconds;
	}

	/**
	 * Retorna el tiempo máximo de vida en segundos de un elemento.
	 * 
	 * @return el tiempo máximo de vida en segundos de un elemento.
	 */
	public Long getTimeToLiveSeconds() {

		if (timeToLiveSeconds != null) {
			return timeToLiveSeconds;
		}

		return DEFAULT_TIME_TO_LIVE_SECONDS; 
	}

	/**
	 * Asigna el tiempo máximo de vida en segundos de un elemento.
	 * 
	 * @param timeToLiveSeconds El tiempo máximo de vida en segundos a asignar.
	 */
	public void setTimeToLiveSeconds(Long timeToLiveSeconds) {
		this.timeToLiveSeconds = timeToLiveSeconds;
	}
	
	/**
	 * Retorna una nueva instancia idéntica a esta.
	 * 
	 * @return la nueva instancia.
	 */
	public Expiry clone() {
		
		Expiry clone = new Expiry(isEternal());
		
		clone.setTimeToIdleSeconds(getTimeToIdleSeconds());
		clone.setTimeToLiveSeconds(getTimeToLiveSeconds());
		
		return clone;
	}
}
