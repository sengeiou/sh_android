package com.fav24.dataservices.monitoring;


/**
 * Interface que debe implementar cualquier medidor.
 */
public interface Meter
{
	/**
	 * Retorna el nombre del medidor.
	 *  
	 * @return el nombre del medidor.
	 */
	public String getMeterName();
}