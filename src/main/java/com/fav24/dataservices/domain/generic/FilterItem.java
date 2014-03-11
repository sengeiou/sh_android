package com.fav24.dataservices.domain.generic;

import java.io.Serializable;



/**
 * Clase que contiene la estructura de un filtro.
 * 
 * @author Fav24
 */
public class FilterItem implements Serializable {

	private static final long serialVersionUID = 6861658494009450768L;

	/**
	 * Enumeración interna que de los tipos de comparadores que existen. 
	 */
	public static enum ComparatorType {
		EQ("eq"),
		NE("ne"),
		GT("gt"),
		GE("ge"),
		LT("lt"),
		LE("le");

		private final String comparatorType;

		/**
		 * Constructor privado del tipo de comparador.
		 * 
		 * @param comparatorType Cadena de texto aue identifica el tipo de comparador.
		 */
		ComparatorType(String comparatorType) {
			this.comparatorType = comparatorType;
		}

		/**
		 * Retorna la cadena de texto que identifica este tipo de comparador.
		 * 
		 * @return la cadena de texto que identifica este tipo de comparador.
		 */
		public String getComparatorType() {
			return comparatorType;
		}

		/**
		 * Retorna el tipo de comparador a partir de la cadena de texto indicada.
		 * 
		 * @param text Cadena de texto a partir de la que se deduce el tipo de comparador.
		 * 
		 * @return el tipo de comparador a partir de la cadena de texto indicada.
		 */
		public static ComparatorType fromString(String text) {
			if (text != null) {
				for (ComparatorType comparatorType : ComparatorType.values()) {
					if (text.equalsIgnoreCase(comparatorType.comparatorType)) {
						return comparatorType;
					}
				}
			}
			return null;
		}
	}


	private ComparatorType comparator;
	private String name;
	private Object value;


	/**
	 * Constructor por defecto.
	 */
	public FilterItem() {
		this(ComparatorType.EQ, null, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param comparator Tipo de comparador.
	 * @param name Nombre del atributo clave.
	 * @param value Valor del atributo.
	 */
	public FilterItem(ComparatorType comparator, String name, Object value) {
		this.comparator = comparator;
		this.name = name;
		this.value = value;
	}

	/**
	 * Retorna el tipo de comparador.
	 * 
	 * @return el tipo de comparador.
	 */
	public ComparatorType getComparator() {
		return comparator;
	}

	/**
	 * Asigna el tipo de comparador.
	 * 
	 * @param comparator El comparado a asignar.
	 */
	public void setComparator(ComparatorType comparator) {
		this.comparator = comparator;
	}

	/**
	 * Retorna el nombre del atributo.
	 * 
	 * @return el nombre del atributo.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Asigna el nombre del atributo.
	 * 
	 * @param name El nombre a asignar.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retorna el valor del atributo.
	 * 
	 * @return el valor del atributo.
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Asigna el valor del atributo.
	 * 
	 * @param value El valor a asignar.
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((comparator == null) ? 0 : comparator.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		FilterItem other = (FilterItem) obj;
		if (comparator != other.comparator)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public FilterItem clone() {

		FilterItem clone = new FilterItem();

		clone.comparator = comparator;
		clone.name = name;
		clone.value = value;

		return clone;
	}
}
