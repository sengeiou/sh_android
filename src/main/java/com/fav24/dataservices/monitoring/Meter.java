package com.fav24.dataservices.monitoring;


/**
 * Clase abstracta que debe extender cualquier medidor.
 */
public abstract class Meter
{
	/**
	 * Retorna el nombre del medidor.
	 *  
	 * @return el nombre del medidor.
	 */
	public abstract String getMeterName();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getMeterName() == null) ? 0 : getMeterName().hashCode());
		return result;
	}
}