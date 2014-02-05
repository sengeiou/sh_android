package com.fav24.dataservices.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonUnwrapped;


/**
 * Clase que contiene la estructura de un item de una entidad.
 * 
 * @author Fav24
 */
public class DataItemDto {
	
	@JsonUnwrapped(enabled=true)
	private Map<String, Object> attributes;


	/**
	 * Constructor por defecto.
	 */
	public DataItemDto() {
		attributes = null;
	}
	
	/**
	 * Retorna los atributos del item en formato clave-valor.
	 * 
	 * @return los atributos del item en formato clave-valor.
	 */
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	/**
	 * Asigna los atributos del item en formato clave-valor.
	 * 
	 * @param attributes Los atributos a asignar.
	 */
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
}
