package com.fav24.dataservices.dto;


/**
 * Clase que contiene la estructura de un filtro.
 * 
 * @author Fav24
 */
public class FilterItemDto {
	
	private String comparator;
	private String name;
	private String value;

	
	/**
	 * Constructor.
	 * 
	 * @param ComparatorType comparator Tipo de comparador.
	 * @param name Nombre del atributo clave.
	 * @param value Valor del atributo.
	 */
	public FilterItemDto(String comparator, String name, String value) {
		this.comparator = comparator;
		this.name = name;
		this.value = value;
	}

	/**
	 * Retorna el tipo de comparador.
	 * 
	 * @return el tipo de comparador.
	 */
	public String getComparator() {
		return comparator;
	}

	/**
	 * Asigna el tipo de comparador.
	 * 
	 * @param comparator El comparado a asignar.
	 */
	public void setComparator(String comparator) {
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
}
