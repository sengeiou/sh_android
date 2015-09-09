package com.shootr.android.service.dataservice.generic;

/**
 * Clase que contiene la estructura de un conjunto de filtros.
 * 
 * @author Fav24
 */
public class FilterDto {

	private String nexus;
	private FilterItemDto[] filterItems;
	private FilterDto[] filters;


	/**
	 * Constructor por defecto.
	 */
	public FilterDto() {
		nexus = null;
		filterItems = null;
		filters = null;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param nexus Tipo de nexo.
	 * @param filterItems Array de filtros.
	 * @param filters Array de conjuntos de filtros.
	 */
	public FilterDto(String nexus, FilterItemDto[] filterItems, FilterDto[] filters) {
		this.nexus = nexus;

		if (filterItems != null) {
			this.filterItems = new FilterItemDto[filterItems.length];
			System.arraycopy(filterItems, 0, this.filterItems, 0, filterItems.length);
		} else {
			this.filterItems = null;
		}
		
		if (filters != null) {
			this.filters = new FilterDto[filters.length];
			System.arraycopy(filters, 0, this.filters, 0, filters.length);
		} else {
			this.filters = null;
		}
	}

	/**
	 * Retorna el tipo de nexo.
	 * 
	 * @return el tipo de nexo.
	 */
	public String getNexus() {
		return nexus;
	}

	/**
	 * Asigna el tipo de nexo.
	 * 
	 * @param nexus El nexo a asignar.
	 */
	public void setNexus(String nexus) {
		this.nexus = nexus;
	}

	/**
	 * Retorna el array de elementos de filtrado para este conjunto de filtrado.
	 * 
	 * @return el array de elementos de filtrado para este conjunto de filtrado.
	 */
	public FilterItemDto[] getFilterItems() {
		return filterItems;
	}

	/**
	 * Asigna el array de elementos de filtrado para este conjunto de filtrado.
	 * 
	 * @param filterItems El array de elementos de filtrado a asignar.
	 */
	public void setFilterItems(FilterItemDto[] filterItems) {
		this.filterItems = filterItems;
	}

	/**
	 * Retorna el array de conjuntos de filtrado, para este conjunto de filtrado.
	 * 
	 * @return el array de conjuntos de filtrado, para este conjunto de filtrado.
	 */
	public FilterDto[] getFilters() {
		return filters;
	}

	/**
	 * Asigna el array de conjuntos de filtrado, para este conjunto de filtrado.
	 * 
	 * @param filters El array de conjuntos de filtrado a asignar.
	 */
	public void setFilters(FilterDto[] filters) {
		this.filters = filters;
	}
}
