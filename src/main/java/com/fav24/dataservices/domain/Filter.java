package com.fav24.dataservices.domain;

import java.util.AbstractList;
import java.util.ArrayList;


/**
 * Clase que contiene la estructura de un conjunto de filtros.
 * 
 * @author Fav24
 */
public class Filter {

	/**
	 * Enumeración interna que de los tipos de nexos que existen. 
	 */
	public enum NexusType {
		AND("and"),
		OR("or");

		private final String nexusType;

		NexusType(String nexusType) {
			this.nexusType = nexusType;
		}

		public String getNexusType() {
			return nexusType;
		}
	}


	private NexusType nexus;
	private AbstractList<FilterItem> filterItems;
	private AbstractList<Filter> filter;


	/**
	 * Constructor por defecto.
	 */
	public Filter() {
		this(NexusType.AND, null, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param nexus Tipo de nexo.
	 * @param filterItems Array de filtros.
	 * @param filters Array de conjuntos de filtros.
	 */
	public Filter(NexusType nexus, FilterItem[] filterItems, Filter[] filters) {
		this.nexus = nexus;

		if (filterItems != null) {
			this.filterItems = new ArrayList<FilterItem>(filterItems.length);
			for (FilterItem filter : filterItems) {
				this.filterItems.add(filter);
			}
		}
		else {
			this.filterItems = null;
		}
		
		if (filters != null) {
			this.filter = new ArrayList<Filter>(filters.length);
			for (Filter filterSet : filters) {
				this.filter.add(filterSet);
			}
		}
		else {
			this.filter = null;
		}
	}

	/**
	 * Retorna el tipo de nexo.
	 * 
	 * @return el tipo de nexo.
	 */
	public NexusType getNexus() {
		return nexus;
	}

	/**
	 * Asigna el tipo de nexo.
	 * 
	 * @param nexus El nexo a asignar.
	 */
	public void setNexus(NexusType nexus) {
		this.nexus = nexus;
	}

	/**
	 * Retorna la lista de filtros para este conjunto de filtrado.
	 * 
	 * @return la lista de filtros para este conjunto de filtrado.
	 */
	public AbstractList<FilterItem> getFilterItems() {
		return filterItems;
	}

	/**
	 * Asigna la lista de filtros para este conjunto de filtrado.
	 * 
	 * @param filterItems La lista de filtros a asignar.
	 */
	public void setFilters(AbstractList<FilterItem> filterItems) {
		this.filterItems = filterItems;
	}

	/**
	 * Retorna la lista de conjuntos de filtrado, para este conjunto de filtrado.
	 * 
	 * @return la lista de conjuntos de filtrado, para este conjunto de filtrado.
	 */
	public AbstractList<Filter> getFilterSets() {
		return filter;
	}

	/**
	 * Asigna la lista de conjuntos de filtrado, para este conjunto de filtrado.
	 * 
	 * @param filterSets La lista de conjuntos de filtrado a asignar.
	 */
	public void setFilterSets(AbstractList<Filter> filterSets) {
		this.filter = filterSets;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((filter == null) ? 0 : filter.hashCode());
		result = prime * result + ((filterItems == null) ? 0 : filterItems.hashCode());
		result = prime * result + ((nexus == null) ? 0 : nexus.hashCode());
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
		Filter other = (Filter) obj;
		if (filter == null) {
			if (other.filter != null)
				return false;
		} else if (!filter.equals(other.filter))
			return false;
		if (filterItems == null) {
			if (other.filterItems != null)
				return false;
		} else if (!filterItems.equals(other.filterItems))
			return false;
		if (nexus != other.nexus)
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "FilterSet [nexus=" + nexus + ", filters=" + filterItems
				+ ", filterSets=" + filter + "]";
	}
}
