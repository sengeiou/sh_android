package com.fav24.dataservices.domain;


/**
 * Clase que contiene la estructura de un filtro.
 * 
 * @author Fav24
 */
public class Filter {

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

		ComparatorType(String comparatorType) {
			this.comparatorType = comparatorType;
		}

		public String getComparatorType() {
			return comparatorType;
		}
	}
	
	
	private ComparatorType comparator;
	private String name;
	private String value;

	
	/**
	 * Constructor por defecto.
	 */
	public Filter() {
		this(ComparatorType.EQ, null, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param ComparatorType comparator Tipo de comparador.
	 * @param name Nombre del atributo clave.
	 * @param value Valor del atributo.
	 */
	public Filter(ComparatorType comparator, String name, String value) {
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
	public String getValue() {
		return value;
	}

	/**
	 * Asigna el valor del atributo.
	 * 
	 * @param value El valor a asignar.
	 */
	public void setValue(String value) {
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
		Filter other = (Filter) obj;
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
}
