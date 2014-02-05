package com.fav24.dataservices.domain;

import java.util.AbstractList;

import com.fav24.dataservices.security.EntityAccessPolicy.OperationType;


/**
 * Clase que contiene la estructura de una acción sobre una entidad.
 * 
 * @author Fav24
 */
public class Metadata {

	private OperationType operation;
	private String entity;
	private Long entitySize;
	private Long items;
	private AbstractList<KeyItem> key;
	private Filter filter;


	/**
	 * Constructor por defecto.
	 */
	public Metadata() {
		this.operation = null;
		this.entity = null;
		this.entitySize = null;
		this.items = null;
		this.key = null;
		this.setFilter(null);
	}

	/**
	 * Constructor.
	 * 
	 * @param operation Tipo de operación a realizar.
	 * @param entity Entidad contra la que se realiza la operación.
	 * @param entitySize Número de ítems de la entidad, una vez realizada la operación.
	 * @param items Número de ítems afectados por la operación.
	 * @param key Lista de atributos y valores que identifican el ítem a operar.
	 */
	public Metadata(OperationType operation, String entity, Long entitySize, Long items, AbstractList<KeyItem> key) {
		this.operation = operation;
		this.entity = entity;
		this.entitySize = entitySize;
		this.items = items;
		this.key = key;
	}

	/**
	 * Constructor.
	 * 
	 * @param operation Tipo de operación a realizar.
	 * @param entity Entidad contra la que se realiza la operación.
	 * @param entitySize Número de ítems de la entidad, una vez realizada la operación.
	 * @param items Número de ítems afectados por la operación.
	 * @param filter Estructura de filtrado de los ítems a operar.
	 */
	public Metadata(OperationType operation, String entity, Long entitySize, Long items, Filter filter) {
		this.operation = operation;
		this.entity = entity;
		this.entitySize = entitySize;
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
	 * Retorna el número de ítems de la entidad, después de la operación.
	 * 
	 * @return el número de ítems de la entidad, después de la operación.
	 */
	public Long getEntitySize() {
		return entitySize;
	}

	/**
	 * Asigna el número de ítems de la entidad, después de la operación.
	 * 
	 * @param entitySize Número de ítems a asignar.
	 */
	public void setEntitySize(Long entitySize) {
		this.entitySize = entitySize;
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
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entity == null) ? 0 : entity.hashCode());
		result = prime * result
				+ ((entitySize == null) ? 0 : entitySize.hashCode());
		result = prime * result + ((filter == null) ? 0 : filter.hashCode());
		result = prime * result + ((items == null) ? 0 : items.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result
				+ ((operation == null) ? 0 : operation.hashCode());
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
		Metadata other = (Metadata) obj;
		if (entity == null) {
			if (other.entity != null)
				return false;
		} else if (!entity.equals(other.entity))
			return false;
		if (entitySize == null) {
			if (other.entitySize != null)
				return false;
		} else if (!entitySize.equals(other.entitySize))
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
		if (operation != other.operation)
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Metadata [operation=" + operation + ", entity=" + entity
				+ ", entitySize=" + entitySize + ", items=" + items + ", key="
				+ key + ", filter=" + filter + "]";
	}
}
