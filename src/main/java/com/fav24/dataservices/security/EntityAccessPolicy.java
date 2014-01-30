package com.fav24.dataservices.security;

import java.util.HashSet;
import java.util.Iterator;
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

		/**
		 * Constructor privado del tipo de operación.
		 * 
		 * @param operationType Cadena de texto aue identifica el tipo de operación.
		 */
		OperationType(String operationType) {
			this.operationType = operationType;
		}

		/**
		 * Retorna la cadena de texto que identifica este tipo de operación.
		 * 
		 * @return la cadena de texto que identifica este tipo de operación.
		 */
		public String getOperationType() {
			return operationType;
		}

		/**
		 * Retorna el tipo de operación a partir de la cadena de texto indicada.
		 * 
		 * @param text Cadena de texto a partir de la que se deduce el tipo de operación.
		 * 
		 * @return el tipo de operación a partir de la cadena de texto indicada.
		 */
		public static OperationType fromString(String text) {
			if (text != null) {
				for (OperationType operationType : OperationType.values()) {
					if (text.equalsIgnoreCase(operationType.operationType)) {
						return operationType;
					}
				}
			}
			return null;
		}
	}

	private EntityAttribute name;
	private Set<OperationType> allowedOperations; 
	private EntityData data;
	private EntityKeys keys;
	private EntityFilters filters;
	private Boolean onlyByKey;
	private Boolean onlySpecifiedFilters;
	private Long maxPageSize;


	/**
	 * Constructor por defecto.
	 */
	public EntityAccessPolicy() {

		this.name = null;
		this.allowedOperations = new HashSet<OperationType>();
		this.data = null;
		this.keys = null;
		this.filters = null;
		this.onlyByKey = true;
		this.onlySpecifiedFilters = true;
		this.maxPageSize = 0L;
	}

	/**
	 * Constructor de copia.
	 * 
	 * @param entityAccessPolicy Objeto referencia a copiar.
	 */
	public EntityAccessPolicy(EntityAccessPolicy entityAccessPolicy) {

		name = entityAccessPolicy.name == null ? null : new EntityAttribute(entityAccessPolicy.name);
		allowedOperations = entityAccessPolicy.allowedOperations == null ? null : new HashSet<OperationType>(entityAccessPolicy.allowedOperations);
		data = entityAccessPolicy.data == null ? null : new EntityData(entityAccessPolicy.data);
		keys = entityAccessPolicy.keys == null ? null : new EntityKeys(entityAccessPolicy.keys);
		filters = entityAccessPolicy.filters == null ? null : new EntityFilters(entityAccessPolicy.filters);
		onlyByKey = entityAccessPolicy.onlyByKey == null ? null : entityAccessPolicy.onlyByKey;
		onlySpecifiedFilters = entityAccessPolicy.onlySpecifiedFilters == null ? null : entityAccessPolicy.onlySpecifiedFilters;
		maxPageSize = entityAccessPolicy.maxPageSize == null ? null : entityAccessPolicy.maxPageSize;
	}

	/**
	 * Retorna la entidad a la que aplican las políticas.
	 * 
	 * @return la entidad a la que aplican las políticas.
	 */
	public EntityAttribute getName() {
		return name;
	}

	/**
	 * Asigna la entidad a la que aplican las políticas.
	 * 
	 * @param entity Entidad a asignar.
	 */
	public void setName(EntityAttribute name) {
		this.name = name;
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
	 * Retorna la sección data de esta entidad.
	 * 
	 * @return la sección data de esta entidad.
	 */
	public EntityData getData() {
		return data;
	}

	/**
	 * Asigna la sección data de esta entidad.
	 * 
	 * @param attributes La sección data de a asignar.
	 */
	public void setData(EntityData data) {
		this.data = data;
	}

	/**
	 * Retorna las posibles claves únicas definidas para esta entidad.
	 * 
	 * @return las posibles claves únicas definidas para esta entidad.
	 */
	public EntityKeys getKeys() {
		return keys;
	}

	/**
	 * Asigna las posibles claves únicas definidas para esta entidad.
	 * 
	 * @param keys Las posibles claves únicas a asignar.
	 */
	public void setKeys(EntityKeys keys) {
		this.keys = keys;
	}

	/**
	 * Retorna los posibles filtros definidos para esta entidad.
	 * 
	 * @return las posibles claves únicas definidas para esta entidad.
	 */
	public EntityFilters getFilters() {
		return filters;
	}

	/**
	 * Asigna las posibles claves únicas definidas para esta entidad.
	 * 
	 * @param keys Las posibles claves únicas a asignar.
	 */
	public void setFilters(EntityFilters filters) {
		this.filters = filters;
	}

	/**
	 * Retorna true o false en función de si la entidad permite únicamente ser operada usando anlguna de las claves definidas o no.
	 * 
	 * @return true o false en función de si la entidad permite únicamente ser operada usando anlguna de las claves definidas o no.
	 */
	public Boolean setOnlyByKey() {
		return onlyByKey;
	}

	/**
	 * Asigna true o false en función de si la entidad permite únicamente ser operada usando anlguna de las claves definidas o no.
	 * 
	 * @param onlyByKey Define si la entidad permite únicamente ser operada usando anlguna de las claves definidas o no.
	 */
	public void setOnlyByKey(Boolean onlyByKey) {
		this.onlyByKey = onlyByKey;
	}

	/**
	 * Retorna true o false en función de si la entidad permite únicamente ser operada usando anlguno de los filtros definidas o no.
	 * 
	 * @return true o false en función de si la entidad permite únicamente ser operada usando anlguno de los filtros definidas o no.
	 */
	public Boolean getOnlySpecifiedFilters() {
		return onlySpecifiedFilters;
	}

	/**
	 * Asigna true o false en función de si la entidad permite únicamente ser operada usando anlguna de los filtros definidas o no.
	 * 
	 * @param onlySpecifiedFilters Define si la entidad permite únicamente ser operada usando anlguna de los filtros definidas o no.
	 */
	public void setOnlySpecifiedFilters(Boolean onlySpecifiedFilters) {
		this.onlySpecifiedFilters = onlySpecifiedFilters;
	}

	/**
	 * Retorna el tamaño máximo de página para la entidad.
	 *   
	 * @return el tamaño máximo de página para la entidad.
	 */
	public Long getMaxPageSize() {
		return maxPageSize;
	}

	/**
	 * Asgina el tamaño máximo de página para la entidad.
	 *   
	 * @param pageSize Tamaño máximo de página a signar.
	 */
	public void setMaxPageSize(Long pageSize) {
		this.maxPageSize = pageSize;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDetails() {
		StringBuilder policyDetails = new StringBuilder();

		policyDetails.append("Entidad ").append(name).append(":\n");

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

		Iterator<EntityDataAttribute> attributeIterator = data.getData().iterator();

		if (attributeIterator.hasNext()) {

			while (operations.hasNext()) {
				EntityDataAttribute  attribute = attributeIterator.next();
				policyDetails.append('\n').append(attribute.getAlias()).append(": ").append(attribute.getDirection());
			}
		}
		else {
			policyDetails.append(" ninguno");
		}

		// Keys disponibles.
		policyDetails.append('\n');
		policyDetails.append("Claves:");

		Iterator<EntityKey> keysIterator = keys.getKeys().iterator();

		if (keysIterator.hasNext()) {

			while (keysIterator.hasNext()) {
				Iterator<EntityAttribute> keyIterator = keysIterator.next().getKey().iterator();
				policyDetails.append('\n');

				if (keyIterator.hasNext()) {
					policyDetails.append(keyIterator.next().getAlias());
					while (keyIterator.hasNext()) {
						policyDetails.append(',').append(keyIterator.next().getAlias());
					}
				}
			}
		}
		else {
			policyDetails.append(" ninguna");
		}

		if (onlyByKey) {
			policyDetails.append('\n').append(" Nota: esta entidad únicamente es accesible mediate el uso de una clave.");	
		}

		// Filtros disponibles.
		policyDetails.append('\n');
		policyDetails.append("Filtros:");

		Iterator<EntityFilter> filtersIterator = filters.getFilters().iterator();

		if (filtersIterator.hasNext()) {

			while (filtersIterator.hasNext()) {
				Iterator<EntityAttribute> filterIterator = filtersIterator.next().getFilter().iterator();
				policyDetails.append('\n');

				if (filterIterator.hasNext()) {
					policyDetails.append(filterIterator.next().getAlias());
					while (filterIterator.hasNext()) {
						policyDetails.append(',').append(filterIterator.next().getAlias());
					}
				}
			}
		}
		else {
			policyDetails.append(" ninguno");
		}

		if (onlySpecifiedFilters) {
			policyDetails.append('\n').append(" Nota: esta entidad únicamente es accesible mediate el uso de alguno de los filtros especificados.");	
		}

		policyDetails.append('\n').append("Tamaño máximo de página: ").append(maxPageSize);

		return policyDetails.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		EntityAccessPolicy other = (EntityAccessPolicy) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
