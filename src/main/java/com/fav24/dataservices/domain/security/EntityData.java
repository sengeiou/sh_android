package com.fav24.dataservices.domain.security;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Clase que define la sección Data de la definición de las políticas de acceso de una entidad.
 * 
 * @author Fav24
 */
public class EntityData {

	private AbstractList<EntityDataAttribute> data;
	private Map<String, EntityDataAttribute> dataAttributeByAlias;

	/**
	 * Contructor por defecto.
	 */
	public EntityData() {
		data = new ArrayList<EntityDataAttribute>();
		dataAttributeByAlias = new HashMap<String, EntityDataAttribute>();
	}
	
	/**
	 * Constructor de copia.
	 * 
	 * @param entityData Objeto referencia a copiar.
	 */
	public EntityData(EntityData entityData) {

		if (entityData.data != null) {
			this.data = new ArrayList<EntityDataAttribute>();
			this.dataAttributeByAlias = new HashMap<String, EntityDataAttribute>();
					
			for (EntityDataAttribute entityDataAttribute : entityData.data) {
				EntityDataAttribute entityDataAttributeCopy = new EntityDataAttribute(entityDataAttribute);
				this.data.add(entityDataAttributeCopy);
				this.dataAttributeByAlias.put(entityDataAttributeCopy.getAlias(), entityDataAttributeCopy);
			}
		}
		else {
			this.data = null;
			this.dataAttributeByAlias = null;
		}
	}

	/**
	 * Retorna el atributo correspondiente al alias indicado.
	 * 
	 * @param alias Alias del atributo a solicitar.
	 * 
	 * @return el atributo correspondiente al alias indicado.
	 */
	public EntityDataAttribute getAttribute(String alias) {

		if (alias != null) {

			return dataAttributeByAlias.get(alias);
		}

		return null;
	}
	
	/**
	 * Retorna true o false en función de si el atributo correspondiente al alias indicado existe o no.
	 * 
	 * @param alias Alias del atributo a comprobar.
	 * 
	 * @return true o false en función de si el atributo correspondiente al alias indicado existe o no.
	 */
	public boolean hasAttribute(String alias) {
		
		if (alias != null) {
			
			return dataAttributeByAlias.containsKey(alias);
		}
		
		return false;
	}

	/**
	 * Retorna el atributo correspondiente al nombre indicado.
	 * 
	 * @param alias Alias del atributo a solicitar.
	 * 
	 * @return el atributo correspondiente al alias indicado.
	 */
	public EntityDataAttribute getAttributeByName(String name) {
		
		if (name != null) {

			for(EntityDataAttribute attribute : data) {
				if (name.equals(attribute.getName())) {
					return attribute;
				}
			}
		}

		return null;
	}
	
	/**
	 * Retorna la estructura que contiene el conjunto de attributos de datos.
	 * 
	 * Nota: no usar la lista de retorno para añadir atributos.
	 * @see #addDataAttribute(EntityDataAttribute)
	 * 
	 * @return la estructura que contiene el conjunto de attributos de datos.
	 */
	public final AbstractList<EntityDataAttribute> getData() {
		return data;
	}
	
	/**
	 * Añade el atributo al conjunto de datos.
	 * 
	 * @param dataAttribute Atributo a añadir.
	 */
	public void addDataAttribute(EntityDataAttribute dataAttribute) {
		
		data.add(dataAttribute);
		dataAttributeByAlias.put(dataAttribute.getAlias(), dataAttribute);
	}
}
