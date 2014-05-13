package com.fav24.dataservices.domain.security;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.fav24.dataservices.domain.generic.Filter;
import com.fav24.dataservices.domain.generic.KeyItem;


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
	private EntityOrdination ordination;
	private Boolean onlyByKey;
	private Boolean onlySpecifiedFilters;
	private Long maxPageSize;


	/**
	 * Constructor por defecto.
	 */
	public EntityAccessPolicy() {

		this.name = null;
		this.allowedOperations = new TreeSet<OperationType>();
		this.data = null;
		this.keys = null;
		this.filters = null;
		this.ordination = null;
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
		allowedOperations = entityAccessPolicy.allowedOperations == null ? null : new TreeSet<OperationType>(entityAccessPolicy.allowedOperations);
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
	 * Retorna los detalles de esta entidad en forma de cadena de texto.
	 * 
	 * @param inHtml True o false en función de si se desea o no que el texto esté formateado en XHTML.
	 * 
	 * @return los detalles de esta entidad en forma de cadena de texto.
	 */
	public String getDetails(boolean inXhtml) {
		StringBuilder policyDetails = new StringBuilder();

		if (inXhtml) {
			policyDetails.append("<p>");	
			policyDetails.append("<b>");	
		}
		policyDetails.append("Entidad ");
		if (inXhtml) {
			policyDetails.append("</b>");	
		}
		policyDetails.append(name.getAlias());
		policyDetails.append(":");
		if (inXhtml) {
			policyDetails.append("</p>");	
		}
		else {
			policyDetails.append('\n');
		}

		// Operaciones permitidas.
		if (inXhtml) {
			policyDetails.append("<p>");	
			policyDetails.append("<b>");	
		}
		policyDetails.append("Operaciones permitidas: ");
		if (inXhtml) {
			policyDetails.append("</b>");	
		}

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
		if (inXhtml) {
			policyDetails.append("</p>");	
		}
		else {
			policyDetails.append('\n');
		}

		// Dirección de los atributos.
		if (inXhtml) {
			policyDetails.append("<p>");	
			policyDetails.append("<b>");	
		}
		policyDetails.append("Atributos: ");
		if (inXhtml) {
			policyDetails.append("</b>");	
		}

		Iterator<EntityDataAttribute> attributeIterator = data.getData().iterator();

		if (attributeIterator.hasNext()) {

			while (attributeIterator.hasNext()) {
				EntityDataAttribute  attribute = attributeIterator.next();
				if (inXhtml) {
					policyDetails.append("<br/>");	
				}
				else {
					policyDetails.append('\n');
				}
				policyDetails.append(attribute.getAlias()).append(": ").append(attribute.getDirection());
			}
		}
		else {
			policyDetails.append("ninguno.");
		}

		if (inXhtml) {
			policyDetails.append("</p>");	
		}
		else {
			policyDetails.append('\n');
		}

		// Keys disponibles.
		if (inXhtml) {
			policyDetails.append("<p>");	
			policyDetails.append("<b>");	
		}
		policyDetails.append("Claves: ");
		if (inXhtml) {
			policyDetails.append("</b>");	
		}

		if (keys != null && keys.getKeys().size() > 0) {

			Iterator<EntityKey> keysIterator = keys.getKeys().iterator();

			while (keysIterator.hasNext()) {
				Iterator<EntityAttribute> keyIterator = keysIterator.next().getKey().iterator();
				if (inXhtml) {
					policyDetails.append("<br/>");	
				}
				else {
					policyDetails.append('\n');
				}

				if (keyIterator.hasNext()) {
					policyDetails.append(keyIterator.next().getAlias());
					while (keyIterator.hasNext()) {
						policyDetails.append(',').append(keyIterator.next().getAlias());
					}
				}
			}
		}
		else {
			policyDetails.append("ninguna.");
		}

		if (onlyByKey) {
			if (inXhtml) {
				policyDetails.append("<br/><b>");	
			}
			else {
				policyDetails.append('\n');
			}
			policyDetails.append("Nota: ");
			if (inXhtml) {
				policyDetails.append("</b>");
			}
			policyDetails.append("esta entidad únicamente es accesible mediate el uso de una clave.");	
		}

		if (inXhtml) {
			policyDetails.append("</p>");	
		}
		else {
			policyDetails.append('\n');
		}


		// Filtros disponibles.
		if (inXhtml) {
			policyDetails.append("<p>");	
			policyDetails.append("<b>");	
		}
		policyDetails.append("Filtros: ");
		if (inXhtml) {
			policyDetails.append("</b>");	
		}

		if (filters != null && filters.getFilters().size() > 0) {

			Iterator<EntityFilter> filtersIterator = filters.getFilters().iterator();

			while (filtersIterator.hasNext()) {
				Iterator<EntityAttribute> filterIterator = filtersIterator.next().getFilter().iterator();
				if (inXhtml) {
					policyDetails.append("<br/>");	
				}
				else {
					policyDetails.append('\n');
				}

				if (filterIterator.hasNext()) {
					policyDetails.append(filterIterator.next().getAlias());
					while (filterIterator.hasNext()) {
						policyDetails.append(',').append(filterIterator.next().getAlias());
					}
				}
			}
		}
		else {
			policyDetails.append("ninguno.");
		}

		if (onlySpecifiedFilters) {
			if (inXhtml) {
				policyDetails.append("<br/><b>");
			}
			else {
				policyDetails.append('\n');
			}
			policyDetails.append("Nota: ");
			if (inXhtml) {
				policyDetails.append("</b>");
			}
			policyDetails.append("esta entidad únicamente es accesible mediate el uso de alguno de los filtros especificados.");	
		}

		if (inXhtml) {
			policyDetails.append("</p>");	
		}
		else {
			policyDetails.append('\n');
		}

		if (inXhtml) {
			policyDetails.append("<br/><b>");
		}
		else {
			policyDetails.append('\n');
		}
		policyDetails.append("Tamaño máximo de página: ");
		if (inXhtml) {
			policyDetails.append("</b>");
		}
		policyDetails.append(maxPageSize);

		String result = policyDetails.toString();

		if (inXhtml) {
			result.replace("á", "&aacute;");
			result.replace("é", "&eacute;");
			result.replace("í", "&iacute;");
			result.replace("ó", "&oacute;");
			result.replace("ú", "&uacute;");
		}

		return result;
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
