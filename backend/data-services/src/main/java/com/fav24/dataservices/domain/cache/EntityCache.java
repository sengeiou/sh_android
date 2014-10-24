package com.fav24.dataservices.domain.cache;



/**
 * Configuración de caché para una determinada entidad.
 */
public class EntityCache extends CacheConfiguration implements Comparable<EntityCache>
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

	/**
	 * Retorna una nueva instancia idéntica a esta.
	 * 
	 * @return la nueva instancia.
	 */
	public EntityCache clone() {

		EntityCache clone = new EntityCache();

		super.clone(clone);

		clone.alias = alias;

		return clone;
	}

	/**
	 * Compara el alias de esta caché de entidad con el de la suministrada por parámetro.
	 * 
	 * Este método está pensado para permitir la ordenación de las cachés de entidades, por el alias de la entidad.
	 * 
	 * @return un entero negativo, cero, o un entero positivo si este objeto es menor, igual, o mayor que el indicado por parámetro.
	 */
	@Override
	public int compareTo(EntityCache o) {

		if (o == null) {
			return 1;	
		}

		if (alias == null) {
			if (o.alias == null) {
				return 0;
			}
			else {
				return -1;
			}
		}

		return alias.compareTo(o.alias);
	}
}
