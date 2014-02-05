package com.fav24.dataservices.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * Clase que contiene la estructura de una acción sobre una entidad.
 * 
 * @author Fav24
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonAutoDetect(
creatorVisibility = JsonAutoDetect.Visibility.ANY,
fieldVisibility = JsonAutoDetect.Visibility.ANY, 
getterVisibility = JsonAutoDetect.Visibility.NONE, 
isGetterVisibility = JsonAutoDetect.Visibility.NONE, 
setterVisibility = JsonAutoDetect.Visibility.NONE)
public class MetadataDto {

	private String operation;
	private String entity;
	private Long entitySize;
	private Long items;
	private Map<String, String> key;
	private FilterDto filter;


	/**
	 * Constructor por defecto.
	 */
	public MetadataDto() {
		this.operation = null;
		this.entity = null;
		this.entitySize = null;
		this.items = null;
		this.key = null;
		this.setFilter(null);
	}

	/**
	 * Constructor.
	 * 
	 * @param operation Tipo de operación a realizar.
	 * @param entity Entidad contra la que se realiza la operación.
	 * @param entitySize Número de ítems de la entidad, una vez realizada la operación.
	 * @param items Número de ítems afectados por la operación.
	 * @param key Lista de atributos y valores que identifican el ítem a operar.
	 */
	public MetadataDto(String operation, String entity, Long entitySize, Long items, Map<String, String> key) {
		this.operation = operation;
		this.entity = entity;
		this.entitySize = entitySize;
		this.items = items;
		this.key = key;
	}

	/**
	 * Constructor.
	 * 
	 * @param operation Tipo de operación a realizar.
	 * @param entity Entidad contra la que se realiza la operación.
	 * @param entitySize Número de ítems de la entidad, una vez realizada la operación.
	 * @param items Número de ítems afectados por la operación.
	 * @param filter Estructura de filtrado de los ítems a operar.
	 */
	public MetadataDto(String operation, String entity, Long entitySize, Long items, FilterDto filter) {
		this.operation = operation;
		this.entity = entity;
		this.entitySize = entitySize;
		this.items = items;
		this.setFilter(filter);
	}

	/**
	 * Retorna el tipo de operación a realizar.
	 * 
	 * @return el tipo de operación a realizar.
	 */
	public String getOperation() {
		return operation;
	}

	/**
	 * Asigna el tipo de operación a realizar.
	 * 
	 * @param operation Tipo de operación a asignar.
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}

	/**
	 * Retorna el nombre de la entidad contra la que se aplicará la operación.
	 * 
	 * @return el nombre de la entidad contra la que se aplicará la operación.
	 */
	public String getEntity() {
		return entity;
	}

	/**
	 * Asigna el nombre de la entidad contra la que se aplicará la operación.
	 * 
	 * @param entity El nombre de la entidad a asignar.
	 */
	public void setEntity(String entity) {
		this.entity = entity;
	}

	/**
	 * Retorna el número de ítems de la entidad, después de la operación.
	 * 
	 * @return el número de ítems de la entidad, después de la operación.
	 */
	public Long getEntitySize() {
		return entitySize;
	}

	/**
	 * Asigna el número de ítems de la entidad, después de la operación.
	 * 
	 * @param entitySize Número de ítems a asignar.
	 */
	public void setEntitySize(Long entitySize) {
		this.entitySize = entitySize;
	}

	/**
	 * Retorna el número de ítems operados.
	 * 
	 * @return el número de ítems operados.
	 */
	public Long getItems() {
		return items;
	}

	/**
	 * Asigna el número de ítems operados.
	 * 
	 * @param items Número de ítems operados a asignar.
	 */
	public void setItems(Long items) {
		this.items = items;
	}

	/**
	 * Retorna la lista de atributos y valores, que conforma la clave del elemento a localizar para la operación. 
	 * 
	 * @return la lista de atributos y valores, que conforma la clave del elemento a localizar para la operación.
	 */
	public Map<String, String> getKey() {
		return key;
	}

	/**
	 * Asigna la lista de atributos y valores, que conforma la clave del elemento a localizar para la operación.
	 * 
	 * @param key La lista a asignar.
	 */
	public void setKey(Map<String, String> key) {
		this.key = key;
	}

	/**
	 * Retorna la estructura de filtrado de los ítems a operar.
	 * 
	 * @return la estructura de filtrado de los ítems a operar.
	 */
	public FilterDto getFilter() {
		return filter;
	}

	/**
	 * Asigna la estructura de filtrado de los ítems a operar.
	 * 
	 * @param filter La estructura de filtrado a asignar.
	 */
	public void setFilter(FilterDto filter) {
		this.filter = filter;
	}
}
