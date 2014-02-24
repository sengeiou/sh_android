package com.fav24.dataservices.domain.cache;


/**
 * Configuración de caché para una determinada entidad.
 */
public class EntityCache extends CacheConfiguration
{
	private String alias;


	/**
	 * Constructor sin parámetros.
	 */
	public EntityCache() {
	}
	
	/**
	 * Constructor con parámetros.
	 * 
	 * @param parentConfiguration Configuración padre para los valores por defecto.
	 */
	public EntityCache(CacheConfiguration parentConfiguration) {

		super(parentConfiguration);
	}
	
	/**
	 * Constructor con parámetros.
	 *  
	 * @param alias Alias de la entidad a la que se aplicará esta configuración de caché.
	 * @param parentConfiguration Configuración padre para los valores por defecto.
	 */
	public EntityCache(String alias, CacheConfiguration parentConfiguration) {

		super(parentConfiguration);
		this.alias = alias;
	}

	/**
	 * Retorna el alias de la entidad a la que hace referencia esta caché.
	 * 
	 * @return el alias de la entidad a la que hace referencia esta caché.
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * Asigna el alias de la entidad a la que hace referencia esta caché.
	 *  
	 * @param alias Alias a asignar.
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}
}
