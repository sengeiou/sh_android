package com.fav24.dataservices.security;

import java.util.AbstractList;
import java.util.ArrayList;


/**
 * Clase que define un filtro de la sección Filters de la definición de las políticas de acceso de una entidad.
 * 
 * @author Fav24
 */
public class EntityFilter {

	private AbstractList<EntityAttribute> filter;

	/**
	 * Contructor por defecto.
	 */
	public EntityFilter() {
		filter = new ArrayList<EntityAttribute>();
	}

	/**
	 * Constructor de copia.
	 * 
	 * @param entityFilter Objeto referencia a copiar.
	 */
	public EntityFilter(EntityFilter entityFilter) {

		if (entityFilter.filter != null) {
			this.filter = new ArrayList<EntityAttribute>();

			for (EntityAttribute attribute : entityFilter.filter) {
				this.filter.add(new EntityAttribute(attribute));	
			}
		}
		else {
			this.filter = null;
		}
	}
	
	/**
	 * Retorna true o false en función de si la lista de alias corresponde al 100% con el filtro.
	 * 
	 * @param aliases Aliases a comprobar.
	 * 
	 * @return true o false en función de si la lista de alias corresponde al 100% con el filtro.
	 */
	public boolean corresponds(String [] aliases) {
		
		if (aliases == null || aliases.length != filter.size()) {
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

			for(EntityAttribute attribute : filter) {
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
			
			for(EntityAttribute attribute : filter) {
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

			for(EntityAttribute attribute : filter) {
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
	public AbstractList<EntityAttribute> getFilter() {
		return filter;
	}
}
