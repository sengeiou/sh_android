package com.fav24.dataservices.domain;

import java.util.AbstractList;
import java.util.ArrayList;


/**
 * Clase que contiene la estructura de un conjunto de filtros.
 * 
 * @author Fav24
 */
public class FilterSet {

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
	private AbstractList<Filter> filters;
	private AbstractList<FilterSet> filterSets;


	/**
	 * Constructor por defecto.
	 */
	public FilterSet() {
		this(NexusType.AND, null, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param nexus Tipo de nexo.
	 * @param filters Array de filtros.
	 * @param filterSets Array de conjuntos de filtros.
	 */
	public FilterSet(NexusType nexus, Filter[] filters, FilterSet[] filterSets) {
		this.nexus = nexus;

		if (filters != null) {
			this.filters = new ArrayList<Filter>(filters.length);
			for (Filter filter : filters) {
				this.filters.add(filter);
			}
		}
		else {
			this.filters = null;
		}
		
		if (filterSets != null) {
			this.filterSets = new ArrayList<FilterSet>(filterSets.length);
			for (FilterSet filterSet : filterSets) {
				this.filterSets.add(filterSet);
			}
		}
		else {
			this.filterSets = null;
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
	public AbstractList<Filter> getFilters() {
		return filters;
	}

	/**
	 * Asigna la lista de filtros para este conjunto de filtrado.
	 * 
	 * @param filters La lista de filtros a asignar.
	 */
	public void setFilters(AbstractList<Filter> filters) {
		this.filters = filters;
	}

	/**
	 * Retorna la lista de conjuntos de filtrado, para este conjunto de filtrado.
	 * 
	 * @return la lista de conjuntos de filtrado, para este conjunto de filtrado.
	 */
	public AbstractList<FilterSet> getFilterSets() {
		return filterSets;
	}

	/**
	 * Asigna la lista de conjuntos de filtrado, para este conjunto de filtrado.
	 * 
	 * @param filterSets La lista de conjuntos de filtrado a asignar.
	 */
	public void setFilterSets(AbstractList<FilterSet> filterSets) {
		this.filterSets = filterSets;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((filterSets == null) ? 0 : filterSets.hashCode());
		result = prime * result + ((filters == null) ? 0 : filters.hashCode());
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
		FilterSet other = (FilterSet) obj;
		if (filterSets == null) {
			if (other.filterSets != null)
				return false;
		} else if (!filterSets.equals(other.filterSets))
			return false;
		if (filters == null) {
			if (other.filters != null)
				return false;
		} else if (!filters.equals(other.filters))
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
		return "FilterSet [nexus=" + nexus + ", filters=" + filters
				+ ", filterSets=" + filterSets + "]";
	}
}
