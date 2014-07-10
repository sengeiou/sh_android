package com.fav24.dataservices.domain.policy;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Clase que define un filtro de la sección Filters de la definición de las políticas de acceso de una entidad.
 */
public class EntityFilter {

	private AbstractList<EntityAttribute> filter;
	private Map<String, EntityAttribute> entityFilterByAlias;

	/**
	 * Contructor por defecto.
	 */
	public EntityFilter() {
		filter = new ArrayList<EntityAttribute>();
		entityFilterByAlias = new HashMap<String, EntityAttribute>();
	}

	/**
	 * Constructor de copia.
	 * 
	 * @param entityFilter Objeto referencia a copiar.
	 */
	public EntityFilter(EntityFilter entityFilter) {

		if (entityFilter.filter != null) {

			this.filter = new ArrayList<EntityAttribute>();
			this.entityFilterByAlias = new HashMap<String, EntityAttribute>();

			for (EntityAttribute attribute : entityFilter.filter) {

				EntityAttribute attributeCopy = new EntityAttribute(attribute);

				this.filter.add(attributeCopy);	
				this.entityFilterByAlias.put(attributeCopy.getAlias(), attributeCopy);	
			}
		}
		else {
			this.filter = null;
			this.entityFilterByAlias = null;
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

			return entityFilterByAlias.containsKey(alias);
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

			return entityFilterByAlias.get(alias);
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
	 * Retorna la estructura que contiene el conjunto de attributos de un filtro.
	 * 
	 * Nota: no usar la lista de retorno para añadir atributos.
	 * @see #addFilterAttribute(EntityAttribute)
	 * 
	 * @return la estructura que contiene el conjunto de attributos de un filtro.
	 */
	public final AbstractList<EntityAttribute> getFilter() {
		return filter;
	}
	
	/**
	 * Añade el atributo al filtro.
	 * 
	 * @param filterAttribute Atributo de filtro a añadir.
	 */
	public void addFilterAttribute(EntityAttribute filterAttribute) {
		
		filter.add(filterAttribute);
		entityFilterByAlias.put(filterAttribute.getAlias(), filterAttribute);
	}

	/**
	 * Retorna la lista de nombre de campos que conforman el filtro.
	 * 
	 * @return la lista de nombre de campos que conforman el filtro.
	 */
	public String getFilterNamesString() {

		StringBuilder filterNamesString = new StringBuilder();

		Iterator<EntityAttribute> filterIterator = filter.iterator();

		if (filterIterator.hasNext()) {

			filterNamesString.append(filterIterator.next().getName());

			while(filterIterator.hasNext()) {
				filterNamesString.append(',');
				filterNamesString.append(filterIterator.next().getName());
			}
		}

		return filterNamesString.toString();
	}
}
