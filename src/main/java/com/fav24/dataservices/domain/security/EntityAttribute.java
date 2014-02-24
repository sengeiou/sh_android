package com.fav24.dataservices.domain.security;


/**
 * Esta clase contiene la definición de un atributo básico. 
 * 
 * @author Fav24
 */
public class EntityAttribute implements Comparable<EntityAttribute> {

	private String alias;
	private String name;


	/**
	 * Constructor por defecto.
	 */
	public EntityAttribute() {
		this(null, null);
	}

	/**
	 * Constructor por defecto.
	 * 
	 * @param alias El alias de este atributo.
	 * @param name El nombre de este atributo.
	 */
	public EntityAttribute(String alias, String name) {

		this.alias = alias;
		this.name = name;
	}

	/**
	 * Constructor de copia.
	 * 
	 * @param entityAttribute Objeto referencia a copiar.
	 */
	public EntityAttribute(EntityAttribute entityAttribute) {

		this.alias = entityAttribute.alias;
		this.name = entityAttribute.name;
	}


	/**
	 * Retorna el alias de este atributo.
	 * 
	 * @return el alias de este atributo.
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * Asigna el alias de este atributo.
	 * 
	 * @param alias El alias a asignar.
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * Retorna el nombre de este atributo.
	 * 
	 * @return el nombre de este atributo.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Asigna el nombre de este atributo.
	 * 
	 * @param name El nombre a asignar.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alias == null) ? 0 : alias.hashCode());
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
		EntityAttribute other = (EntityAttribute) obj;
		if (alias == null) {
			if (other.alias != null)
				return false;
		} else if (!alias.equals(other.alias))
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(EntityAttribute o) {

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
