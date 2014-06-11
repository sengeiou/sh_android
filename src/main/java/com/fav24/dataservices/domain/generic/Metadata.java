package com.fav24.dataservices.domain.generic;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.Collections;

import com.fav24.dataservices.domain.cache.Organizable;
import com.fav24.dataservices.domain.security.EntityAccessPolicy.OperationType;


/**
 * Clase que contiene la estructura de una acción sobre una entidad.
 */
public class Metadata implements Organizable, Serializable {

	private static final long serialVersionUID = -8445866091695050501L;

	private OperationType operation;
	private String entity;
	private Boolean includeDeleted;
	private Long totalItems;
	private Long offset;
	private Long items;
	private AbstractList<KeyItem> key;
	private Filter filter;


	/**
	 * Constructor por defecto.
	 */
	public Metadata() {

		this.operation = null;
		this.entity = null;
		this.includeDeleted = null;
		this.totalItems = null;
		this.offset = null;
		this.items = null;
		this.key = null;
		this.filter = null;
	}

	/**
	 * Constructor.
	 * 
	 * @param operation Tipo de operación a realizar.
	 * @param entity Entidad contra la que se realiza la operación.
	 * @param includeDeleted Flag donde se indica si se deben o no incluir registros que satisfacen las condiciones de criba, con el atributo "deleted".
	 * @param totalItems Número de ítems afectados por la operación.
	 * @param offset Número del último ítem a partir del que se desea que esta operación aplique.
	 * @param items Número de ítems afectados por la operación.
	 * @param key Lista de atributos y valores que identifican el ítem a operar.
	 */
	public Metadata(OperationType operation, String entity, Boolean includeDeleted, Long totalItems, Long offset, Long items, AbstractList<KeyItem> key) {

		this.operation = operation;
		this.entity = entity;
		this.includeDeleted = includeDeleted;
		this.totalItems = totalItems;
		this.offset = offset;
		this.items = items;
		this.key = key;
	}

	/**
	 * Constructor.
	 * 
	 * @param operation Tipo de operación a realizar.
	 * @param entity Entidad contra la que se realiza la operación.
	 * @param includeDeleted Flag donde se indica si se deben o no incluir registros que satisfacen las condiciones de criba, con el atributo "deleted".
	 * @param totalItems Número de ítems afectados por la operación.
	 * @param offset Número del último ítem a partir del que se desea que esta operación aplique.
	 * @param items Número de ítems afectados por la operación.
	 * @param filter Estructura de filtrado de los ítems a operar.
	 */
	public Metadata(OperationType operation, String entity, Boolean includeDeleted, Long totalItems, Long offset, Long items, Filter filter) {

		this.operation = operation;
		this.entity = entity;
		this.includeDeleted = includeDeleted;
		this.totalItems = totalItems;
		this.offset = offset;
		this.items = items;
		this.setFilter(filter);
	}

	/**
	 * Retorna el tipo de operación a realizar.
	 * 
	 * @return el tipo de operación a realizar.
	 */
	public OperationType getOperation() {
		return operation;
	}

	/**
	 * Asigna el tipo de operación a realizar.
	 * 
	 * @param operation Tipo de operación a asignar.
	 */
	public void setOperation(OperationType operation) {
		this.operation = operation;
	}

	/**
	 * Retorna el nombre de la entidad contra la que se aplicará la operación.
	 * 
	 * @return el nombre de la entidad contra la que se aplicará la operación.
	 */
	public String getEntity() {
		return entity;
	}

	/**
	 * Asigna el nombre de la entidad contra la que se aplicará la operación.
	 * 
	 * @param entity El nombre de la entidad a asignar.
	 */
	public void setEntity(String entity) {
		this.entity = entity;
	}

	/**
	 * Retorna true o false en función de si se deben o no incluir registros que satisfacen las condiciones de criba, con el atributo "deleted".
	 *  
	 * @return true o false en función de si se deben o no incluir registros que satisfacen las condiciones de criba, con el atributo "deleted".
	 */
	public Boolean getIncludeDeleted() {
		return includeDeleted;
	}

	/**
	 * Asigna si se deben o no incluir registros que satisfacen las condiciones de criba, con el atributo "deleted".
	 * 
	 * @param includeDeleted True o false en función de si se deben o no incluir registros con el atributo "deleted".
	 */
	public void setIncludeDeleted(Boolean includeDeleted) {
		this.includeDeleted = includeDeleted;
	}

	/**
	 * Retorna el número de ítems afectados por la operación.
	 * 
	 * @return el número de ítems afectados por la operación.
	 */
	public Long getTotalItems() {
		return totalItems;
	}

	/**
	 * Asigna el número de ítems afectados por la operación.
	 * 
	 * @param totalItems Número de ítems a asignar.
	 */
	public void setTotalItems(Long totalItems) {
		this.totalItems = totalItems;
	}

	/**
	 * Retorna el número del último ítem a partir del que se desea que esta operación aplique.
	 * 
	 * @return el número del último ítem a partir del que se desea que esta operación aplique.
	 */
	public Long getOffset() {
		return offset;
	}

	/**
	 * Asigna el número del último ítem a partir del que se desea que esta operación aplique.
	 * 
	 * @param offset Número del último ítem a partir del que se desea que esta operación aplique.
	 */
	public void setOffset(Long offset) {
		this.offset = offset;
	}

	/**
	 * Retorna el número de ítems operados.
	 * 
	 * @return el número de ítems operados.
	 */
	public Long getItems() {
		return items;
	}

	/**
	 * Asigna el número de ítems operados.
	 * 
	 * @param items Número de ítems operados a asignar.
	 */
	public void setItems(Long items) {
		this.items = items;
	}

	/**
	 * Retorna la lista de atributos y valores, que conforma la clave del elemento a localizar para la operación. 
	 * 
	 * @return la lista de atributos y valores, que conforma la clave del elemento a localizar para la operación.
	 */
	public AbstractList<KeyItem> getKey() {
		return key;
	}

	/**
	 * Asigna la lista de atributos y valores, que conforma la clave del elemento a localizar para la operación.
	 * 
	 * @param key La lista a asignar.
	 */
	public void setKey(AbstractList<KeyItem> key) {
		this.key = key;
	}

	/**
	 * Retorna la estructura de filtrado de los ítems a operar.
	 * 
	 * @return la estructura de filtrado de los ítems a operar.
	 */
	public Filter getFilter() {
		return filter;
	}

	/**
	 * Asigna la estructura de filtrado de los ítems a operar.
	 * 
	 * @param filter La estructura de filtrado a asignar.
	 */
	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	/**
	 * Retorna true o false en función de si tiene o no una clave definida.
	 * 
	 * @return true o false en función de si tiene o no una clave definida.
	 */
	public boolean hasKey() {
		return key != null && key.size() > 0;
	}

	/**
	 * Retorna true o false en función de si tiene o no un filtro definido.
	 * 
	 * @return true o false en función de si tiene o no un filtro definido.
	 */
	public boolean hasFilter() {

		return filter != null && filter.isValidFilter();
	}

	/**
	 * {@inheritDoc}
	 */
	public StringBuilder organizeContent(StringBuilder contentKey) {

		if (contentKey == null) {
			contentKey = new StringBuilder();
		}

		// Paginación.
		contentKey.append("p[");
		if (offset != null) {
			contentKey.append(offset);
		}
		contentKey.append('|');
		if (items != null) {
			contentKey.append(items);
		}
		contentKey.append(']');
		
		contentKey.append(ELEMENT_SEPARATOR);
		
		// Claves.
		contentKey.append("k[");
		if (key != null && key.size() > 0) {
			Collections.sort(key);

			boolean firstItem = true;
			for(KeyItem keyItem : key) {

				if (firstItem) {
					keyItem.organizeContent(contentKey);
				}
				else {
					contentKey.append(ELEMENT_SEPARATOR);
					keyItem.organizeContent(contentKey);
				}

				firstItem = false;
			}
		}
		contentKey.append(']');

		contentKey.append(ELEMENT_SEPARATOR);

		// Filtros.
		contentKey.append("f[");
		if (filter != null) {
			filter.organizeContent(contentKey);
		}
		contentKey.append(']');

		contentKey.append(ELEMENT_SEPARATOR);

		// Borrados.
		contentKey.append("d[");
		if (includeDeleted != null) {
			contentKey.append(includeDeleted);
		}
		else {
			contentKey.append(Boolean.FALSE);
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
		result = prime * result + ((entity == null) ? 0 : entity.hashCode());
		result = prime * result + ((includeDeleted == null) ? 0 : includeDeleted.hashCode());
		result = prime * result + ((filter == null) ? 0 : filter.hashCode());
		result = prime * result + ((items == null) ? 0 : items.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((offset == null) ? 0 : offset.hashCode());
		result = prime * result + ((operation == null) ? 0 : operation.hashCode());
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

			Metadata other = (Metadata) obj;

			if (entity == null) {
				if (other.entity != null)
					return false;
			} else if (!entity.equals(other.entity))
				return false;

			if (includeDeleted == null) {
				if (other.includeDeleted != null)
					return false;
			} else if (!includeDeleted.equals(other.includeDeleted))
				return false;

			if (filter == null) {
				if (other.filter != null)
					return false;
			} else if (!filter.equals(other.filter))
				return false;

			if (items == null) {
				if (other.items != null)
					return false;
			} else if (!items.equals(other.items))
				return false;

			if (key == null) {
				if (other.key != null)
					return false;
			} else if (!key.equals(other.key))
				return false;

			if (offset == null) {
				if (other.offset != null)
					return false;
			} else if (!offset.equals(other.offset))
				return false;

			if (operation != other.operation)
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
	@Override
	public String toString() {
		return "Metadata [operation=" + operation 
				+ ", entity=" + entity
				+ ", includeDeleted=" + includeDeleted
				+ ", entitySize=" + totalItems + ", offset=" + offset
				+ ", items=" + items + ", key=" + key + ", filter=" + filter
				+ "]";
	}
}
