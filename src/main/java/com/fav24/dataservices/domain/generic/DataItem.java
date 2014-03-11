package com.fav24.dataservices.domain.generic;

import java.io.Serializable;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.fav24.dataservices.domain.cache.Organizable;


/**
 * Clase que contiene la estructura de un item de una entidad.
 * 
 * @author Fav24
 */
public class DataItem implements Organizable, Comparable<DataItem>, Serializable {

	private static final long serialVersionUID = 7424979410388955842L;

	private NavigableMap<String, Object> attributes;


	/**
	 * Constructor de copia.
	 * 
	 * @param reference Instancia de referencia.
	 */
	public DataItem(DataItem reference) {

		if (reference.attributes.comparator() != null) {

			this.attributes = new TreeMap<String, Object>();
			this.attributes.putAll(reference.attributes);
		}
		else {
			this.attributes = null;
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

		contentKey.append("attributes[");

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
				contentKey.append("]");

				firstItem = false;
			}
		}

		contentKey.append("]");

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
		if (getClass() != obj.getClass())
			return false;
		DataItem other = (DataItem) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
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
