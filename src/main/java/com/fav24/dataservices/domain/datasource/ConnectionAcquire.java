package com.fav24.dataservices.domain.datasource;


/**
 * Configuración del modo de adquisición de conexiones del pool.
 */
public class ConnectionAcquire
{
	private Integer increment;
	private Integer retries;
	private Long retryDelay;

	
	/**
	 * Constructor por defecto.
	 */
	public ConnectionAcquire() {

		this.increment = null;
		this.retries = null;
		this.retryDelay = null;
	}

	/**
	 * Retorna el número de conexiones que se reservan, cada vez que se amplia el pool.
	 *  
	 * @return el número de conexiones que se reservan, cada vez que se amplia el pool.
	 */
	public Integer getIncrement() {
		return increment;
	}

	/**
	 * Asigna el número de conexiones que se reservan, cada vez que se amplia el pool.
	 * 
	 * @param increment El incremento a asignar.
	 */
	public void setIncrement(Integer increment) {
		this.increment = increment;
	}

	/**
	 * Retorna el número de reintentos utilizado durante la creación de nuevas conexiones (adquisición).  
	 * 
	 * @return número de reintentos utilizado durante la creación de nuevas conexiones (adquisición).
	 */
	public Integer getRetries() {
		return retries;
	}

	/**
	 * Asigna el número de reintentos utilizado durante la creación de nuevas conexiones (adquisición).
	 * 
	 * @param retries Número de reintentos a asignar.
	 */
	public void setRetries(Integer retries) {
		this.retries = retries;
	}

	/**
	 * Retorna el retardo en milisegundos, entre reintentos de adquisión de conexiones.
	 * 
	 * @return el retardo en milisegundos, entre reintentos de adquisión de conexiones.
	 */
	public Long getRetryDelay() {
		return retryDelay;
	}

	/**
	 * Asigna el retardo en milisegundos, entre reintentos de adquisión de conexiones.
	 * 
	 * @param retryDelay El retardo a asignar.
	 */
	public void setRetryDelay(Long retryDelay) {
		this.retryDelay = retryDelay;
	}
}
