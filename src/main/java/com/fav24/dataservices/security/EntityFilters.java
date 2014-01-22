package com.fav24.dataservices.security;

import java.util.AbstractList;
import java.util.ArrayList;


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
}
