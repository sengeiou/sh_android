package com.fav24.dataservices.domain;



/**
 * Clase que contiene la estructura de un atributo clave.
 * 
 * @author Fav24
 */
public class Key {

	private String name;
	private String value;

	
	/**
	 * Constructor por defecto.
	 */
	public Key() {
		this(null, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param name Nombre del atributo clave.
	 * @param value Valor del atributo.
	 */
	public Key(String name, String value) {
		this.name = name;
		this.value = value;
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
		Key other = (Key) obj;
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
	@Override
	public String toString() {
		return "Key [name=" + name + ", value=" + value + "]";
	}
}
