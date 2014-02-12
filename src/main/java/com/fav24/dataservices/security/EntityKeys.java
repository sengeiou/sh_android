package com.fav24.dataservices.security;

import java.util.AbstractList;
import java.util.ArrayList;

import com.fav24.dataservices.domain.KeyItem;


/**
 * Clase que define la sección Keys de la definición de las políticas de acceso de una entidad.
 * 
 * @author Fav24
 */
public class EntityKeys {

	private AbstractList<EntityKey> entityKeys;


	/**
	 * Contructor por defecto.
	 */
	public EntityKeys() {
		entityKeys = new ArrayList<EntityKey>();
	}

	/**
	 * Constructor de copia.
	 * 
	 * @param entityKeys Objeto referencia a copiar.
	 */
	public EntityKeys(EntityKeys entityKeys) {

		if (entityKeys.entityKeys != null) {

			this.entityKeys = new ArrayList<EntityKey>();

			for (EntityKey entityKey : entityKeys.entityKeys) {
				this.entityKeys.add(new EntityKey(entityKey));	
			}

		}
		else {
			this.entityKeys = null;
		}
	}

	/**
	 * Retorna true o false en función de si los elementos que conforman la clave corresponden al 100% con alguna de las claves.
	 * 
	 * @param keyItems Elementos que conforman la clave a comprobar.
	 * 
	 * @return true o false en función de si los elementos que conforman la clave corresponden al 100% con alguna de las claves.
	 */
	public boolean containsKey(AbstractList<KeyItem> keyItems) {

		if (keyItems != null) {

			String[] aliases = new String[keyItems.size()];
			int i=0;
			for (KeyItem keyItem : keyItems) {
				aliases[i++] = keyItem.getName();
			}

			return containsKey(aliases);
		}

		return false;
	}

	/**
	 * Retorna true o false en función de si la lista de alias corresponde al 100% con alguna de las claves.
	 * 
	 * @param aliases Aliases a comprobar.
	 * 
	 * @return true o false en función de si la lista de alias corresponde al 100% con alguna de las claves.
	 */
	public boolean containsKey(String[] aliases) {

		if (aliases != null) {

			for(EntityKey key : entityKeys) {
				if (key.corresponds(aliases)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Retorna la estructura que contiene el conjunto de claves.
	 * 
	 * @return la estructura que contiene el conjunto de claves.
	 */
	public AbstractList<EntityKey> getKeys() {
		return entityKeys;
	}

	/**
	 * Retorna el primer elemento de una clave con el alias indicado.
	 *  
	 * @param alias El alias a usar.
	 * 
	 * @return el primer elemento de una clave con el alias indicado.
	 */
	public EntityAttribute getFirstKeyAttributeByAlias(String alias) {

		if (entityKeys != null && alias != null) {

			for(EntityKey key : entityKeys) {

				EntityAttribute keyItem = key.getAttribute(alias);

				if (keyItem != null) {
					return keyItem;
				}
			}
		}

		return null;
	}
}
