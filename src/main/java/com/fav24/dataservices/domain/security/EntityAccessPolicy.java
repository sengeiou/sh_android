package com.fav24.dataservices.domain.security;

import java.util.AbstractList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.fav24.dataservices.domain.generic.Filter;
import com.fav24.dataservices.domain.generic.KeyItem;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.hook.GenericServiceHook;
import com.fav24.dataservices.service.security.AccessPolicyService;


/**
 * Clase que contiene las políticas de acceso estructura de una entidad.
 */
public class EntityAccessPolicy implements Comparable<EntityAccessPolicy> {

	public static final String ERROR_ACCESS_POLICY_CHECK_FAILED = "AP000";
	public static final String ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE = "Fallo en el chequeo de la definición de las políticas de acceso.";

	/**
	 * Enumeración de los tipos de operaciones que existen. 
	 */
	public enum OperationType {

		CREATE("Create"),
		UPDATE("Update"),
		RETRIEVE("Retrieve"),
		DELETE("Delete"),
		CREATE_UPDATE("CreateUpdate"),
		UPDATE_CREATE("UpdateCreate");

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
	private Boolean isVirtual;
 	private Map<String, GenericServiceHook> hooks; 
	private EntityData data;
	private EntityKeys keys;
	private EntityFilters filters;
	private EntityOrdination ordination;
	private Boolean onlyByKey;
	private Boolean onlySpecifiedFilters;
	private Long maxPageSize;
	private Long positiveRevisionThreshold;
	private Long negativeRevisionThreshold;


	/**
	 * Constructor por defecto.
	 */
	public EntityAccessPolicy() {

		this.name = null;
		this.allowedOperations = new TreeSet<OperationType>();
		this.isVirtual = false;
		this.hooks = new LinkedHashMap<String, GenericServiceHook>();
		this.data = null;
		this.keys = null;
		this.filters = null;
		this.ordination = null;
		this.onlyByKey = true;
		this.onlySpecifiedFilters = true;
		this.maxPageSize = 0L;
		this.positiveRevisionThreshold = 0L;
		this.negativeRevisionThreshold = 0L;
	}

	/**
	 * Constructor de copia.
	 * 
	 * @param entityAccessPolicy Objeto referencia a copiar.
	 */
	public EntityAccessPolicy(EntityAccessPolicy entityAccessPolicy) {

		name = entityAccessPolicy.name == null ? null : new EntityAttribute(entityAccessPolicy.name);
		allowedOperations = entityAccessPolicy.allowedOperations == null ? null : new TreeSet<OperationType>(entityAccessPolicy.allowedOperations);
		isVirtual = entityAccessPolicy.isVirtual == null ? null : entityAccessPolicy.isVirtual;
		hooks = entityAccessPolicy.hooks == null ? null : new LinkedHashMap<String, GenericServiceHook>(entityAccessPolicy.hooks);
		data = entityAccessPolicy.data == null ? null : new EntityData(entityAccessPolicy.data);
		keys = entityAccessPolicy.keys == null ? null : new EntityKeys(entityAccessPolicy.keys);
		filters = entityAccessPolicy.filters == null ? null : new EntityFilters(entityAccessPolicy.filters);
		ordination = entityAccessPolicy.ordination == null ? null : new EntityOrdination(entityAccessPolicy.ordination);
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
	 * @param allowedOperations El conjunto de operaciones permitidas a asignar.
	 */
	public void setAllowedOperations(Set<OperationType> allowedOperations) {
		this.allowedOperations = allowedOperations;
	}
	
	/**
	 * Retorna true o false en función de si se trata o no de una entidad virtual.
	 * Una entidad virtual no se refleja en ninguna de las entidades del subsistema.
	 * 
	 * @return true o false en función de si se trata o no de una entidad virtual.
	 */
	public Boolean isVirtual() {
		return isVirtual;
	}
	
	/**
	 * Asigna true o false en función de si se trata o no de una entidad virtual.
	 * 
	 * @param isVirtual True si se trata de una entidad virtual, false en caso contrario.
	 */
	public void setVirtual(Boolean isVirtual) {
		this.isVirtual = isVirtual;
	}

	/**
	 * Retorna el conjunto de puntos de incorporación sobre esta entidad.
	 * 
	 * @return el conjunto de puntos de incorporación sobre esta entidad.
	 */
	public Map<String, GenericServiceHook> getHooks() {
		return hooks;
	}

	/**
	 * Asigna el conjunto de puntos de incorporación sobre esta entidad.
	 * 
	 * @param hooks El conjunto de puntos de incorporación sobre esta entidad.
	 */
	public void setHooks(Map<String, GenericServiceHook> hooks) {
		this.hooks = hooks;
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
	 * Retorna true o false en función de si los elementos que conforman la clave corresponden al 100% con alguna de las claves.
	 * 
	 * @param keyItems Elementos que conforman la clave a comprobar.
	 * 
	 * @return true o false en función de si los elementos que conforman la clave corresponden al 100% con alguna de las claves.
	 */
	public boolean containsKey(AbstractList<KeyItem> keyItems) {

		if (keys != null) {

			return keys.containsKey(keyItems);
		}

		return false;
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
	 * Retorna la ordenación definida para esta entidad.
	 * 
	 * @return la ordenación definida para esta entidad.
	 */
	public EntityOrdination getOrdination() {
		return ordination;
	}

	/**
	 * Asigna la ordenación definida para esta entidad.
	 * 
	 * @param ordination La ordenación a asignar.
	 */
	public void setOrdination(EntityOrdination ordination) {
		this.ordination = ordination;
	}

	/**
	 * Retorna true o false en función de si el filtro indicado corresponde al 100% con alguno de los filtros.
	 * 
	 * @param filter Filtero a comprobar.
	 * 
	 * @return true o false en función de si el filtro indicado corresponde al 100% con alguno de los filtros.
	 */
	public boolean containsFilter(Filter filter) {

		if (filters != null) {

			return filters.containsFilter(filter);
		}

		return false;
	}

	/**
	 * Retorna el nombre del atributo indicado en la fuente de datos, a partir del alias. 
	 * 
	 * @param attributeAlias Alias del atributo.
	 * 
	 * @return el nombre del atributo indicado en la fuente de datos, a partir del alias.
	 */
	public String getAttributeName(String attributeAlias) {

		if (attributeAlias != null) {

			if (getData() != null) {
				EntityDataAttribute dataAttribute = getData().getAttribute(attributeAlias);

				if (dataAttribute != null) {
					return dataAttribute.getName();
				}
			}

			if (getKeys() != null) {
				EntityAttribute keyAttribute = getKeys().getFirstKeyAttributeByAlias(attributeAlias);

				if (keyAttribute != null) {
					return keyAttribute.getName();
				}
			}

			if (getFilters() != null) {
				EntityAttribute filterAttribute = getFilters().getFirstFilterAttributeByAlias(attributeAlias);

				if (filterAttribute != null) {
					return filterAttribute.getName();
				}
			}
		}

		return null;
	}

	/**
	 * Retorna el alias del atributo indicado en la fuente de datos, a partir de los nombres. 
	 * 
	 * @param attributeName Nombre del atributo.
	 * 
	 * @return el alias del atributo indicado en la fuente de datos, a partir de los nombres.
	 */
	public String getAttributeAlias(String attributeName) {

		if (attributeName != null) {

			if (getData() != null) {
				EntityDataAttribute dataAttribute = getData().getAttributeByName(attributeName);

				if (dataAttribute != null) {
					return dataAttribute.getAlias();
				}
			}

			if (getKeys() != null) {
				EntityAttribute keyAttribute = getKeys().getFirstKeyAttributeByName(attributeName);

				if (keyAttribute != null) {
					return keyAttribute.getAlias();
				}
			}

			if (getFilters() != null) {
				EntityAttribute filterAttribute = getFilters().getFirstFilterAttributeByName(attributeName);

				if (filterAttribute != null) {
					return filterAttribute.getAlias();
				}
			}
		}

		return null;
	}

	/**
	 * Comprueba que la lista de atributos indicada, esta disponible para la entidad.
	 * 
	 * @param attributesAliases Lista de atributos a comprobar.
	 * 
	 * @throws ServerException En caso de que alguno de los atributos no esté accesible.
	 */
	public void checkAttributesAccesibility(AbstractList<String> attributesAliases) throws ServerException {

		StringBuilder notAllowedAttibutes = null;

		for (String attributeAlias : attributesAliases) {

			if (getData().getAttribute(attributeAlias) == null) {

				if (notAllowedAttibutes == null) {
					notAllowedAttibutes = new StringBuilder(attributeAlias);
				}
				else{
					notAllowedAttibutes.append(',').append(attributeAlias);
				}
			}
		}

		if (notAllowedAttibutes != null) {
			throw new ServerException(AccessPolicyService.ERROR_ENTITY_ATTRIBUTES_NOT_ALLOWED, 
					String.format(AccessPolicyService.ERROR_ENTITY_ATTRIBUTES_NOT_ALLOWED_MESSAGE, getName().getAlias(), notAllowedAttibutes));
		}
	}

	/**
	 * Retorna true o false en función de si la entidad permite únicamente ser operada usando anlguna de las claves definidas o no.
	 * 
	 * @return true o false en función de si la entidad permite únicamente ser operada usando anlguna de las claves definidas o no.
	 */
	public Boolean getOnlyByKey() {
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
	 * Retorna el margen a sumar a la revisión de un elemento remoto antes de su comparación con el local, 
	 * en caso de que el elemento remoto tenga una fecha de modificación posterior al local.
	 * 
	 * Este margen asociado a la comparación entre revisiones, hace recaer un mayor peso a la comparación por fecha. 
	 *  
	 * @return margen a sumar a la revisión remota antes de su comparación con la local.
	 */
	public Long getPositiveRevisionThreshold() {
		return positiveRevisionThreshold;
	}

	/**
	 * Asigna el margen a sumar a la revisión de un elemento remoto antes de su comparación con el local, 
	 * en caso de que el elemento remoto tenga una fecha de modificación posterior al local.
	 * 
	 * Este margen asociado a la comparación entre revisiones, hace recaer un mayor peso a la comparación por fecha.
	 * 
	 * @param positiveRevisionThreshold Margen a asignar.
	 */
	public void setPositiveRevisionThreshold(Long positiveRevisionThreshold) {
		this.positiveRevisionThreshold = positiveRevisionThreshold;
	}

	/**
	 * Retorna el margen a restar a la revisión de un elemento remoto antes de su comparación con el local, 
	 * en caso de que el elemento remoto tenga una fecha de modificación anterior al local.
	 * 
	 * Este margen asociado a la comparación entre revisiones, hace recaer un mayor peso a la comparación por revisión. 
	 *  
	 * @return margen a restar a la revisión remota antes de su comparación con la local.
	 */
	public Long getNegativeRevisionThreshold() {
		return negativeRevisionThreshold;
	}

	/**
	 * Asigna el margen a restar a la revisión de un elemento remoto antes de su comparación con el local, 
	 * en caso de que el elemento remoto tenga una fecha de modificación anterior al local.
	 * 
	 * Este margen asociado a la comparación entre revisiones, hace recaer un mayor peso a la comparación por revisión.
	 * 
	 * @param negativeRevisionThreshold Margen a asignar.
	 */
	public void setNegativeRevisionThreshold(Long negativeRevisionThreshold) {
		this.negativeRevisionThreshold = negativeRevisionThreshold;
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
	 * Este método compara estas políticas de acceso con las indicadas por parámtero,
	 * únicamente por el alias de la entidad a la que hacen referencia.
	 * 
	 * @return true o false en función de si el alias de la entidad a la que hacen referencia es o no igual.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof EntityAccessPolicy))
			return false;
		EntityAccessPolicy other = (EntityAccessPolicy) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (name.compareTo(other.name) != 0)
			return false;
		return true;
	}

	/**
	 * Compara el alias de esta entidad con el de la suministrada por parámetro.
	 * 
	 * Este método está pensado para permitir la ordenación de las políticas, por el alias de la entidad.
	 * 
	 * @return un entero negativo, cero, o un entero positivo si este objeto es menor, igual, o mayor que el indicado por parámetro.
	 */
	@Override
	public int compareTo(EntityAccessPolicy o) {

		if (o == null) {
			return 1;	
		}

		if (name == null) {
			if (o.name == null) {
				return 0;
			}
			else {
				return -1;
			}
		}

		return name.compareTo(o.name);
	}
}
