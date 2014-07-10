package com.fav24.dataservices.domain.generic;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;

import com.fav24.dataservices.domain.cache.Organizable;


/**
 * Clase que contiene la estructura de un conjunto de filtros.
 */
public class Filter implements Organizable, Serializable {

	private static final long serialVersionUID = 9148049633943226676L;

	/**
	 * Enumeración interna que de los tipos de nexos que existen. 
	 */
	public enum NexusType {
		AND("and"),
		OR("or");

		private final String nexusType;


		/**
		 * Constructor privado del tipo de nexo.
		 * 
		 * @param nexusType Cadena de texto aue identifica el tipo de nexo.
		 */
		NexusType(String nexusType) {
			this.nexusType = nexusType;
		}

		/**
		 * Retorna la cadena de texto que identifica este tipo de nexo.
		 * 
		 * @return la cadena de texto que identifica este tipo de nexo.
		 */
		public String getNexusType() {
			return nexusType;
		}

		/**
		 * Retorna el tipo de nexo a partir de la cadena de texto indicada.
		 * 
		 * @param text Cadena de texto a partir de la que se deduce el tipo de nexo.
		 * 
		 * @return el tipo de nexo a partir de la cadena de texto indicada.
		 */
		public static NexusType fromString(String text) {
			if (text != null) {
				for (NexusType nexusType : NexusType.values()) {
					if (text.equalsIgnoreCase(nexusType.nexusType)) {
						return nexusType;
					}
				}
			}

			return null;
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
	public void setFilterItems(AbstractList<FilterItem> filterItems) {
		this.filterItems = filterItems;
	}

	/**
	 * Retorna la lista de conjuntos de filtrado, para este conjunto de filtrado.
	 * 
	 * @return la lista de conjuntos de filtrado, para este conjunto de filtrado.
	 */
	public AbstractList<Filter> getFilters() {
		return filter;
	}

	/**
	 * Asigna la lista de conjuntos de filtrado, para este conjunto de filtrado.
	 * 
	 * @param filterSets La lista de conjuntos de filtrado a asignar.
	 */
	public void setFilters(AbstractList<Filter> filterSets) {
		this.filter = filterSets;
	}

	/**
	 * Retorna true o false en función de si se trata o no de una estructura de filtro válida.
	 * 
	 * @return true o false en función de si se trata o no de una estructura de filtro válida.
	 */
	public boolean isValidFilter() {

		if (filter != null && filter.size() > 0) {

			for (Filter currentFilter : filter) {
				if (currentFilter != null && currentFilter.isValidFilter()) {
					return true;
				}
			}
		}

		if (filterItems != null && filterItems.size() > 0) {
			return true;
		}

		return false;
	}

	/**
	 * Retorna una lista con todos los alias de todos los atributos implicados en el filtro.
	 * 
	 * @return una lista con todos los alias de todos los atributos implicados en el filtro.
	 */
	public AbstractList<String> getFilterAliases() {

		AbstractList<String> aliases = new ArrayList<String>();

		if (filter != null && filter.size() > 0) {

			for (Filter currentFilter : filter) {

				if (currentFilter != null) {
					aliases.addAll(currentFilter.getFilterAliases());
				}
			}
		}

		if (filterItems != null) {

			for (FilterItem filterItem : filterItems) {

				aliases.add(filterItem.getName());
			}
		}

		return aliases;
	}


	/**
	 * {@inheritDoc}
	 */
	public StringBuilder organizeContent(StringBuilder contentKey) {

		if (contentKey == null) {
			contentKey = new StringBuilder();
		}

		contentKey.append("fi[");
		if (filterItems != null && filterItems.size() > 0) {
			boolean firstItem = true;
			for (FilterItem filterItem : filterItems) {

				if (!firstItem) {
					contentKey.append(ELEMENT_SEPARATOR);
				}

				filterItem.organizeContent(contentKey);

				firstItem = false;
			}
		}
		contentKey.append(']');

		contentKey.append(ELEMENT_SEPARATOR);

		contentKey.append("f[");
		if (filter != null && filter.size() > 0) {
			boolean firstItem = true;
			for (Filter currentFilter : filter) {

				if (!firstItem) {
					contentKey.append(ELEMENT_SEPARATOR);
				}

				currentFilter.organizeContent(contentKey);

				firstItem = false;
			}
		}
		contentKey.append(']');

		return contentKey;
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

		try {
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
		}
		catch(ClassCastException e) {
			return false;
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public Filter clone() {

		Filter clone = new Filter();

		clone.nexus = nexus;

		if (filterItems != null) {
			clone.filterItems = new ArrayList<FilterItem>(filterItems.size());

			for (FilterItem filterItem : filterItems) {

				clone.filterItems.add(filterItem.clone());
			}
		}
		else {
			clone.filterItems = null;
		}

		if (filter != null) {

			clone.filter = new ArrayList<Filter>(filter.size());

			for (Filter filter : this.filter) {

				clone.filter.add(filter.clone());
			}
		}
		else {
			clone.filter = null;
		}

		return clone;
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
