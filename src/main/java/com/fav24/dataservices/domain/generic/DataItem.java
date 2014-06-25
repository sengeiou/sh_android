package com.fav24.dataservices.domain.generic;

import java.io.Serializable;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.fav24.dataservices.domain.cache.Organizable;
import com.fav24.dataservices.domain.policy.EntityDataAttribute.SynchronizationField;


/**
 * Clase que contiene la estructura de un item de una entidad.
 */
public class DataItem implements Organizable, Comparable<DataItem>, Serializable {

	private static final long serialVersionUID = 7424979410388955842L;

	private NavigableMap<String, Object> attributes;
	private NavigableMap<String, Object> internalAttributes;


	/**
	 * Constructor por defecto.
	 */
	public DataItem() {

		this.attributes = null;
		this.internalAttributes = null;
	}
	
	/**
	 * Constructor de copia.
	 * 
	 * @param reference Instancia de referencia.
	 */
	public DataItem(DataItem reference) {
		
		if (reference.attributes != null) {
			
			this.attributes = new TreeMap<String, Object>();
			this.attributes.putAll(reference.attributes);
		}
		else {
			this.attributes = null;
		}
		
		if (reference.internalAttributes != null) {
			
			this.internalAttributes = new TreeMap<String, Object>();
			this.internalAttributes.putAll(reference.internalAttributes);
		}
		else {
			this.internalAttributes = null;
		}
	}

	/**
	 * Constructor con parámetros.
	 * 
	 * @param attributes Atributos que contendrá este elemento.
	 */
	public DataItem(NavigableMap<String, Object> attributes) {
		setAttributes(attributes);
	}

	/**
	 * Retorna los atributos del item en formato clave-valor.
	 * 
	 * @return los atributos del item en formato clave-valor.
	 */
	public NavigableMap<String, Object> getAttributes() {

		return attributes;
	}
	
	/**
	 * Retorna los atributos insternos asociados al item en formato clave-valor.
	 * 
	 * @return los atributos internos del item en formato clave-valor.
	 */
	public NavigableMap<String, Object> getInternalAttributes() {
		
		if (internalAttributes == null) {
			
			internalAttributes = new TreeMap<String, Object>();
		}
		
		return internalAttributes;
	}

	/**
	 * Retorna los atributos del item, eliminando préviamente los de sistema, en formato clave-valor.
	 * 
	 * @return los atributos del item, eliminando préviamente los de sistema, en formato clave-valor.
	 */
	public NavigableMap<String, Object> getNonSystemAttributes() {

		if (attributes == null) {
			return null;
		}

		NavigableMap<String, Object> nonSystemAttributes = new TreeMap<String, Object>();

		for (Entry<String, Object> attribute : attributes.entrySet()) {

			if (!SynchronizationField.isSynchronizationField(attribute.getKey())) {
				nonSystemAttributes.put(attribute.getKey(), attribute.getValue());
			}
		}

		return nonSystemAttributes;
	}

	/**
	 * Asigna los atributos del item en formato clave-valor.
	 * 
	 * @param attributes Los atributos a asignar.
	 */
	public void setAttributes(NavigableMap<String, Object> attributes) {

		this.attributes = attributes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(DataItem o) {

		if (attributes == o.attributes) {
			return 0;
		}

		if (attributes == null && o.attributes != null) {
			return -1;
		}

		if (attributes != null && o.attributes == null) {
			return 1;
		}

		if (attributes.size() < o.attributes.size()) {
			return -1;
		}

		if (attributes.size() > o.attributes.size()) {
			return 1;
		}

		for (Entry<String, Object> entry : attributes.entrySet()) {

			Object local = entry.getValue();
			Object other = o.attributes.get(entry.getKey());

			if (local == other) {
				continue;
			}

			if (local == null && other != null) {
				return -1;
			}

			if (local != null && other == null) {
				return 1;
			}

			int comparison = String.valueOf(local).compareTo(String.valueOf(other));

			if (comparison != 0) {
				return comparison;
			}
		}

		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public StringBuilder organizeContent(StringBuilder contentKey) {

		if (contentKey == null) {
			contentKey = new StringBuilder();
		}

		contentKey.append("a[");

		if (attributes != null && attributes.size() > 0) {
			boolean firstItem = true;
			for (Entry<String, Object> attribute : attributes.entrySet()) {

				if (!firstItem) {
					contentKey.append(ELEMENT_SEPARATOR);
				}

				contentKey.append("[");
				contentKey.append(attribute.getKey());
				contentKey.append(",");
				contentKey.append(attribute.getValue());
				contentKey.append(']');

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
				+ ((attributes == null) ? 0 : attributes.hashCode());
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
			DataItem other = (DataItem) obj;
			if (attributes == null) {
				if (other.attributes != null)
					return false;
			} else if (!attributes.equals(other.attributes))
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
		return "DataItem [attributes=" + attributes + "]";
	}
}
