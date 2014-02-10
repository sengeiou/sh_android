package com.fav24.dataservices.security;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * Clase que define la sección Filters de la definición de las políticas de acceso de una entidad.
 * 
 * @author Fav24
 */
public class EntityFilters {

	private AbstractList<EntityFilter> entityFilters;


	/**
	 * Contructor por defecto.
	 */
	public EntityFilters() {
		entityFilters = new ArrayList<EntityFilter>();
	}

	/**
	 * Constructor de copia.
	 * 
	 * @param entityFilters Objeto referencia a copiar.
	 */
	public EntityFilters(EntityFilters entityFilters) {

		if (entityFilters.entityFilters != null) {
			this.entityFilters = new ArrayList<EntityFilter>();

			for (EntityFilter entityFilter : entityFilters.entityFilters) {
				this.entityFilters.add(new EntityFilter(entityFilter));	
			}
		}
		else {
			this.entityFilters = null;
		}
	}

	/**
	 * Retorna true o false en función de si la lista de alias corresponde al 100% con alguno de los filtros.
	 * 
	 * @param aliases Aliases a comprobar.
	 * 
	 * @return true o false en función de si la lista de alias corresponde al 100% con alguno de los filtros.
	 */
	public boolean containsFilter(String[] aliases) {

		if (aliases != null) {

			for(EntityFilter filter : entityFilters) {
				if (filter.corresponds(aliases)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Retorna la estructura que contiene el conjunto de filtros.
	 * 
	 * @return la estructura que contiene el conjunto de filtros.
	 */
	public AbstractList<EntityFilter> getFilters() {
		return entityFilters;
	}

	/**
	 * Retorna el primer elemento de un filtro con el alias indicado.
	 *  
	 * @param alias El alias a usar.
	 * 
	 * @return el primer elemento de un filtro con el alias indicado.
	 */
	public EntityAttribute getFirstFilterAttributeByAlias(String alias) {

		if (entityFilters != null && alias != null) {

			for(EntityFilter filter : entityFilters) {

				EntityAttribute keyItem = filter.getAttribute(alias);

				if (keyItem != null) {
					return keyItem;
				}
			}
		}

		return null;
	}

	/**
	 * Retorna el conjunto de attributos implicados en los filtros.
	 * 
	 * @param attributes Set de attributos a poblar. (En caso de der <code>null</code>, se retorna un nuevo conjunto).
	 * 
	 * @return el conjunto de attributos implicados en los filtros.
	 */
	public Set<EntityAttribute> getAllFiltersAttributes(Set<EntityAttribute> attributes) {

		if (attributes == null) {
			attributes = new HashSet<EntityAttribute>();
		}

		for(EntityFilter entityFilter : entityFilters) {

			if (entityFilter.getFilter() != null) {

				attributes.addAll(entityFilter.getFilter());
			}
		}

		return attributes;
	}
}
