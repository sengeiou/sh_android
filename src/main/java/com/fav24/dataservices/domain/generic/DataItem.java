package com.fav24.dataservices.domain.generic;

import java.util.NavigableMap;
import java.util.TreeMap;


/**
 * Clase que contiene la estructura de un item de una entidad.
 * 
 * @author Fav24
 */
public class DataItem {

	private NavigableMap<String, Object> attributes;


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
