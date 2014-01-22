package com.fav24.dataservices.security;

import java.util.AbstractList;
import java.util.ArrayList;


/**
 * Clase que define una clave de la sección Keys de la definición de las políticas de acceso de una entidad.
 * 
 * @author Fav24
 */
public class EntityKey {

	private AbstractList<EntityAttribute> key;

	/**
	 * Contructor por defecto.
	 */
	public EntityKey() {
		key = new ArrayList<EntityAttribute>();
	}

	/**
	 * Constructor de copia.
	 * 
	 * @param entityKey Objeto referencia a copiar.
	 */
	public EntityKey(EntityKey entityKey) {

		if (entityKey.key != null) {
			this.key = new ArrayList<EntityAttribute>();

			for (EntityAttribute attribute : entityKey.key) {
				this.key.add(new EntityAttribute(attribute));	
			}
		}
		else {
			this.key = null;
		}
	}

	/**
	 * Retorna true o false en función de si la lista de alias corresponde al 100% con la clave.
	 * 
	 * @param aliases Aliases a comprobar.
	 * 
	 * @return true o false en función de si la lista de alias corresponde al 100% con la clave.
	 */
	public boolean corresponds(String [] aliases) {

		if (aliases == null || aliases.length != key.size()) {
			return false;
		}

		for (String alias : aliases) {

			if (!hasAttribute(alias)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Retorna true o false en función de si existe o no el atributo correspondiente al alias indicado.
	 * 
	 * @param alias Alias a comprobar.
	 * 
	 * @return true o false en función de si existe o no el atributo correspondiente al alias indicado.
	 */
	public boolean hasAttribute(String alias) {

		if (alias != null) {

			for(EntityAttribute attribute : key) {
				if (alias.equals(attribute.getAlias())) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Retorna el atributo correspondiente al alias indicado.
	 * 
	 * @param alias Alias del atributo a solicitar.
	 * 
	 * @return el atributo correspondiente al alias indicado.
	 */
	public EntityAttribute getAttribute(String alias) {

		if (alias != null) {

			for(EntityAttribute attribute : key) {
				if (alias.equals(attribute.getAlias())) {
					return attribute;
				}
			}
		}

		return null;
	}

	/**
	 * Retorna el atributo correspondiente al nombre indicado.
	 * 
	 * @param alias Alias del atributo a solicitar.
	 * 
	 * @return el atributo correspondiente al alias indicado.
	 */
	public EntityAttribute getAttributeByName(String name) {

		if (name != null) {

			for(EntityAttribute attribute : key) {
				if (name.equals(attribute.getName())) {
					return attribute;
				}
			}
		}

		return null;
	}

	/**
	 * Retorna la estructura que contiene el conjunto de attributos de una clave.
	 * 
	 * @return la estructura que contiene el conjunto de attributos de una clave.
	 */
	public AbstractList<EntityAttribute> getKey() {
		return key;
	}
}
