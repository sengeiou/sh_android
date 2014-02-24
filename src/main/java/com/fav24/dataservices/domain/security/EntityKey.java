package com.fav24.dataservices.domain.security;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;



/**
 * Clase que define una clave de la sección Keys de la definición de las políticas de acceso de una entidad.
 * 
 * @author Fav24
 */
public class EntityKey {

	private AbstractList<EntityAttribute> key;
	private Map<String, EntityAttribute> entityKeyByAlias;


	/**
	 * Contructor por defecto.
	 */
	public EntityKey() {
		key = new ArrayList<EntityAttribute>();
		entityKeyByAlias = new HashMap<String, EntityAttribute>();
	}

	/**
	 * Constructor de copia.
	 * 
	 * @param entityKey Objeto referencia a copiar.
	 */
	public EntityKey(EntityKey entityKey) {

		if (entityKey.key != null) {

			this.key = new ArrayList<EntityAttribute>();
			this.entityKeyByAlias = new HashMap<String, EntityAttribute>();

			for (EntityAttribute attribute : entityKey.key) {
				
				EntityAttribute attributeCopy = new EntityAttribute(attribute);
				
				this.key.add(attributeCopy);	
				this.entityKeyByAlias.put(attributeCopy.getAlias(), attributeCopy);	
			}
		}
		else {
			this.key = null;
			this.entityKeyByAlias = null;
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

			return entityKeyByAlias.containsKey(alias);
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

			return entityKeyByAlias.get(alias);
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
	 * Nota: no usar la lista de retorno para añadir atributos.
	 * @see #addKeyAttribute(EntityAttribute)
	 * 
	 * @return la estructura que contiene el conjunto de attributos de una clave.
	 */
	public final AbstractList<EntityAttribute> getKey() {
		return key;
	}

	/**
	 * Añade el atributo al conjunto de attributos de una clave.
	 * 
	 * @param keyAttribute Atributo clave a añadir.
	 */
	public void addKeyAttribute(EntityAttribute keyAttribute) {
		
		key.add(keyAttribute);
		entityKeyByAlias.put(keyAttribute.getAlias(), keyAttribute);
	}
	
	/**
	 * Retorna la lista de nombre de campos que conforman la clave.
	 * 
	 * @return la lista de nombre de campos que conforman la clave.
	 */
	public String getKeyNamesString() {

		StringBuilder keyNamesString = new StringBuilder();

		Iterator<EntityAttribute> keyIterator = key.iterator();

		if (keyIterator.hasNext()) {

			keyNamesString.append(keyIterator.next().getName());

			while(keyIterator.hasNext()) {
				keyNamesString.append(',');
				keyNamesString.append(keyIterator.next().getName());
			}
		}

		return keyNamesString.toString();
	}
}
