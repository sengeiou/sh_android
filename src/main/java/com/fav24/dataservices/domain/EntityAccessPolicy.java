package com.fav24.dataservices.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


/**
 * Clase que contiene las políticas de acceso estructura de una entidad.
 * 
 * @author Fav24
 */
public class EntityAccessPolicy {

	/**
	 * Enumeración de los tipos de operaciones que existen. 
	 */
	public enum OperationType {
		CREATE("create"),
		UPDATE("update"),
		RETRIEVE("retrieve"),
		DELETE("delete"),
		UPDATE_CREATE("updateCreate");

		private final String operationType;

		OperationType(String operationType) {
			this.operationType = operationType;
		}

		public String getOperationType() {
			return operationType;
		}
	}

	/**
	 * Enumeración de las direcciones permitidas para un determinado atributo. 
	 */
	public enum Direction {
		INPUT("input"),
		OUTPUT("ouput"),
		BOTH("both");

		private final String direction;

		Direction(String direction) {
			this.direction = direction;
		}

		public String getDirection() {
			return direction;
		}
	}

	private String entity;
	private Set<OperationType> allowedOperations; 
	private Map<String, Direction> attributes;
	private Set<Set<String>> keys;
	private Boolean onlyKeyAccess;
	private Long pageSize;


	/**
	 * Constructor por defecto.
	 */
	public EntityAccessPolicy() {

		this.entity = null;
		this.allowedOperations = new HashSet<OperationType>();
		this.attributes = new HashMap<String, Direction>();
		this.keys = new HashSet<Set<String>>();
		this.onlyKeyAccess = true;
		this.pageSize = 0L;
	}

	/**
	 * Retorna la entidad a la que aplican las políticas.
	 * 
	 * @return la entidad a la que aplican las políticas.
	 */
	public String getEntity() {
		return entity;
	}

	/**
	 * Asigna la entidad a la que aplican las políticas.
	 * 
	 * @param entity Entidad a asignar.
	 */
	public void setEntity(String entity) {
		this.entity = entity;
	}

	/**
	 * Retorna los atributos y flujos permitidos.
	 * 
	 * @return los atributos y flujos permitidos.
	 */
	public Map<String, Direction> getAttributes() {
		return attributes;
	}

	/**
	 * Asigna los atributos y flujos permitidos.
	 * 
	 * @param attributes Los atributos y flujos a asignar.
	 */
	public void setAttributes(Map<String, Direction> attributes) {
		this.attributes = attributes;
	}

	/**
	 * Retorna el conjunto de operaciones permitidas sobre esta entidad.
	 * 
	 * @return el conjunto de operaciones permitidas sobre esta entidad.
	 */
	public Set<OperationType> getAllowedOperations() {
		return allowedOperations;
	}

	/**
	 * Asigna el conjunto de operaciones permitidas sobre esta entidad.
	 * 
	 * allowedOperations El conjunto de operaciones permitidas a asignar.
	 */
	public void setAllowedOperations(Set<OperationType> allowedOperations) {
		this.allowedOperations = allowedOperations;
	}

	/**
	 * Retorna la lista de atributos que conforman las posibles claves únicas definidas para esta entidad.
	 * 
	 * @return la lista de atributos que conforman las posibles claves únicas definidas para esta entidad.
	 */
	public Set<Set<String>> getKeys() {
		return keys;
	}

	/**
	 * Asigna la lista de atributos que conforman las posibles claves únicas definidas para esta entidad.
	 * 
	 * @param keys La lista de atributos que conforman las posibles claves únicas a asignar.
	 */
	public void setKeys(Set<Set<String>> keys) {
		this.keys = keys;
	}

	/**
	 * Retorna true o false en función de si la entidad permite únicamente ser operada usando anlguna de las claves definidas o no.
	 * 
	 * @return true o false en función de si la entidad permite únicamente ser operada usando anlguna de las claves definidas o no.
	 */
	public Boolean getOnlyKeyAccess() {
		return onlyKeyAccess;
	}

	/**
	 * Asigna true o false en función de si la entidad permite únicamente ser operada usando anlguna de las claves definidas o no.
	 * 
	 * @param onlyKeyAccess Define si la entidad permite únicamente ser operada usando anlguna de las claves definidas o no.
	 */
	public void setOnlyKeyAccess(Boolean onlyKeyAccess) {
		this.onlyKeyAccess = onlyKeyAccess;
	}

	/**
	 * Retorna el tamaño de página para la entidad.
	 *   
	 * @return el tamaño de página para la entidad.
	 */
	public Long getPageSize() {
		return pageSize;
	}

	/**
	 * Asgina el tamaño de página para la entidad.
	 *   
	 * @param pageSize Tamaño de página a signar.
	 */
	public void setPageSize(Long pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDetails() {
		StringBuilder policyDetails = new StringBuilder();

		policyDetails.append("Entidad ").append(entity).append(":\n");

		// Operaciones permitidas.
		policyDetails.append("Operaciones permitidas: ");

		Iterator<OperationType> operations = allowedOperations.iterator();

		if (operations.hasNext()) {
			policyDetails.append(operations.next());
			while (operations.hasNext()) {
				policyDetails.append(',');
				policyDetails.append(operations.next());
			}
		}
		else {
			policyDetails.append("ninguna");
		}

		// Dirección de los atributos.
		policyDetails.append('\n');
		policyDetails.append("Atributos:");

		Iterator<Entry<String, Direction>> attributesDirections = attributes.entrySet().iterator();

		if (attributesDirections.hasNext()) {

			while (operations.hasNext()) {
				Entry<String, Direction>  attributeDirection = attributesDirections.next();
				policyDetails.append('\n').append(attributeDirection.getKey()).append(": ").append(attributeDirection.getValue());
			}
		}
		else {
			policyDetails.append(" ninguno");
		}

		// Keys disponibles.
		policyDetails.append('\n');
		policyDetails.append("Claves:");

		Iterator<Set<String>> keysSets = keys.iterator();

		if (keysSets.hasNext()) {

			while (keysSets.hasNext()) {
				Iterator<String> keysSet = keysSets.next().iterator();
				policyDetails.append('\n');

				if (keysSet.hasNext()) {
					policyDetails.append(keysSet.next());
					while (keysSet.hasNext()) {
						policyDetails.append(',').append(keysSet.next());
					}
				}
			}
		}
		else {
			policyDetails.append(" ninguna");
		}

		if (onlyKeyAccess) {
			policyDetails.append('\n').append(" Nota: esta entidad únicamente es accesible mediate el uso de una clave.");	
		}
		
		policyDetails.append('\n').append("Tamaño de página: ").append(pageSize);

		return policyDetails.toString();
	}
}
