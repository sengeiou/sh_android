package com.fav24.dataservices.dto.security;

import java.util.Map;


/**
 * Clase que contiene la estructura de una petición de consulta de
 * políticas de acceso
 * 
 * @author Fav24
 */
public class EntityAccessPolicyDtoElement {

	private String entity;
	private String[] allowedOperations;
	private Map<String, String> attributes;
	private String[][] keys;
	private String[][] filters;
	private Boolean onlyByKey;
	private Boolean onlySpecifiedFilters;
	private Long maxPageSize;


	/**
	 * Constructor por defecto.
	 */
	public EntityAccessPolicyDtoElement() {
		
		entity = null;
		allowedOperations = null;
		attributes = null;
		keys = null;
		filters = null;
		onlyByKey = null;
		onlySpecifiedFilters = null;
		maxPageSize = null;
	}
	
	/**
	 * Constructor con parámetro. 
	 *  
	 * @param entity Entidad 
	 */
	public EntityAccessPolicyDtoElement(String entity) {
		this.entity = entity;
	}
	
	/**
	 * Retorna el nombre de la entidad.
	 * 
	 * @return el nombre de la entidad.
	 */
	public String getEntityAlias() {
		return entity;
	}

	/**
	 * Asigna el array de poperaciones permitidas para esta entidad.
	 * 
	 * @param allowedOperations El array de operaciones a asignar.
	 */
	public void setAllowedOperations(String[] allowedOperations) {
		this.allowedOperations = allowedOperations;
	}
	
	/**
	 * Retorna el array de operaciones permitidas para esta entidad.
	 * 
	 * @return el array de operaciones permitidas para esta entidad.
	 */
	public String[] getAllowedOperations() {
		return allowedOperations;
	}
	
	/**
	 * Retorna el conjunto de atributos disponibles y sus direcciones, para esta entidad.
	 * 
	 * @return el conjunto de atributos disponibles y sus direcciones, para esta entidad.
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}

	/**
	 * Asigna el conjunto de atributos disponibles y sus direcciones, para esta entidad.
	 * 
	 * @param attributes El conjunto de atributos y direcciones a asignar.
	 */
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	/**
	 * Retorna el conjunto de claves mediante las que se permite accceder a la información de esta entidad.
	 * 
	 * @return el conjunto de claves mediante las que se permite accceder a la información de esta entidad.
	 */
	public String[][] getKeys() {
		return keys;
	}

	/**
	 * Asigna el conjunto de claves mediante las que se permite accceder a la información de esta entidad.
	 * 
	 * @param keys El conjunto de claves a asignar.
	 */
	public void setKeys(String[][] keys) {
		this.keys = keys;
	}

	/**
	 * Retorn el conjunto de filtros mediante las que se permite accceder a la información de esta entidad.
	 * 
	 * @return el conjunto de filtros mediante las que se permite accceder a la información de esta entidad.
	 */
	public String[][] getFilters() {
		return filters;
	}

	/**
	 * Asigna el conjunto de filtros mediante las que se permite accceder a la información de esta entidad.
	 * 
	 * @param filters El conjunto de filtros asignar.
	 */
	public void setFilters(String[][] filters) {
		this.filters = filters;
	}

	/**
	 * Retorna true o false en función de si esta endidad es accesible únicamente mediante claves.
	 * 
	 * @return true o false en función de si esta endidad es accesible únicamente mediante claves.
	 */
	public Boolean getOnlyByKey() {
		return onlyByKey;
	}

	/**
	 * Asigna true o false en función de si esta endidad es accesible únicamente mediante claves.
	 * 
	 * @param onlyByKey True o false en función de si esta endidad es accesible únicamente mediante claves.
	 */
	public void setOnlyByKey(Boolean onlyByKey) {
		this.onlyByKey = onlyByKey;
	}

	/**
	 * Retorna true o false en función de si esta endidad es accesible únicamente mediante alguno de los filtros especificados.
	 *  
	 * @return true o false en función de si esta endidad es accesible únicamente mediante alguno de los filtros especificados.
	 */
	public Boolean getOnlySpecifiedFilters() {
		return onlySpecifiedFilters;
	}

	/**
	 * Asigna true o false en función de si esta endidad es accesible únicamente mediante alguno de los filtros especificados.
	 * 
	 * @param onlySpecifiedFilters True o false en función de si esta endidad es accesible únicamente mediante alguno de los filtros especificados.
	 */
	public void setOnlySpecifiedFilters(Boolean onlySpecifiedFilters) {
		this.onlySpecifiedFilters = onlySpecifiedFilters;
	}

	/**
	 * Retorna el número máximo de elementos accesible mediante una operación de consulta.
	 * 
	 * @return el número máximo de elementos accesible mediante una operación de consulta.
	 */
	public Long getMaxPageSize() {
		return maxPageSize;
	}

	/**
	 * Asigna el número máximo de elementos accesible mediante una operación de consulta.
	 * 
	 * @param maxPageSize El número máximo de elementos a asignar.
	 */
	public void setMaxPageSize(Long maxPageSize) {
		this.maxPageSize = maxPageSize;
	}
}
