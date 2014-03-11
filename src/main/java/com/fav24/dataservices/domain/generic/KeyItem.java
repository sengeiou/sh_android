package com.fav24.dataservices.domain.generic;

import java.io.Serializable;



/**
 * Clase que contiene la estructura de un atributo clave.
 * 
 * @author Fav24
 */
public class KeyItem implements Serializable {

	private static final long serialVersionUID = 7534571398179647141L;

	private String name;
	private Object value;


	/**
	 * Constructor por defecto.
	 */
	public KeyItem() {
		this(null, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param name Nombre del atributo clave.
	 * @param value Valor del atributo.
	 */
	public KeyItem(String name, Object value) {
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
		KeyItem other = (KeyItem) obj;
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
	public KeyItem clone() {

		KeyItem clone = new KeyItem();

		clone.name = name;
		clone.value = value;

		return clone;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Key [name=" + name + ", value=" + value + "]";
	}
}
