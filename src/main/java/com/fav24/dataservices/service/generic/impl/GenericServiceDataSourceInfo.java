package com.fav24.dataservices.service.generic.impl;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.fav24.dataservices.domain.security.AccessPolicy;
import com.fav24.dataservices.domain.security.EntityAttribute;
import com.fav24.dataservices.exception.ServerException;


public abstract class GenericServiceDataSourceInfo {

	public static final String ERROR_ACCESS_POLICY_CHECK_FAILED = "G000";
	public static final String ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE = "Fallo en el chequeo de las políticas de acceso contra la fuente de datos.";

	public static class EntityDataSourceInfo {

		protected String name;
		protected String catalog;
		protected String schema;
		protected Boolean isView;
		protected Map<String, Integer> dataFields;
		protected Map<String, Object> dataFieldsDefaults;
		protected Map<String, Set<String>> keys;
		protected Map<String, Integer> primaryKey;
		protected Map<String, Set<String>> indexes;
		protected Map<String, Integer> keyFields;
		protected Map<String, Integer> filterFields;
		protected Set<String> generatedData;
	}

	private Map<String, EntityDataSourceInfo> entitiesInformation;
	private int product;

	/**
	 * Retorna el identificador de producto.
	 * 
	 * @return el identificador de producto.
	 */
	public int getProduct() {
		return product;
	}

	/**
	 * Asigna el identificador de producto.
	 * 
	 * @param product Identificador de producto a asignar.
	 */
	protected void setProduct(int product) {
		this.product = product;
	}

	/**
	 * Retorna la información de la entidad en la fuente de datos.
	 * 
	 * @param entity Entidad de la que se desea obtener la información.
	 *
	 * @return la información de la entidad en la fuente de datos.
	 */
	public EntityDataSourceInfo getEntity(String entity) {
		return entitiesInformation != null ? entitiesInformation.get(entity) : null;
	}
	
	/**
	 * Retorna true o false en función de si la colección indicada tiene o no una equivalente en el mapa suministrado.
	 * 
	 * Nota: La comparación se realiza:
	 * 	- Sin tener en cuenta mayúsculas o minúsculas.
	 *  - Sin tener en cuenta el orden de los elementos.
	 * 
	 * @param collections Conjunto de colecciones a comparar. 
	 * @param attributeCollection Colección a localizar.
	 * 
	 * @return true o false en función de si la colección indicada tiene o no una equivalente en el mapa suministrado.
	 */
	protected boolean hasEquivalentAttributeCollection(Map<String, Set<String>> collections, AbstractList<EntityAttribute> attributeCollection) {

		for(Entry<String, Set<String>> collectionEntry : collections.entrySet()) {

			Collection<String> collection = collectionEntry.getValue();

			if (attributeCollection.size() == collection.size()) {

				boolean collectionFound = true;

				for(EntityAttribute attributeElement : attributeCollection) {

					boolean elementFound = false;
					for(String element : collection) {
						if (element.compareToIgnoreCase(attributeElement.getName()) == 0) {
							elementFound = true;
							break;
						}
					}

					if (!elementFound) {
						collectionFound = false;
						break;
					}
				}

				if (collectionFound) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Chequea las políticas de acceso definidas, contra la fuente de datos a la que ataca el servicio.
	 * Obtiene toda la información necesaria de la fuente de datos, para resolver las peticiones.
	 * 
	 * Los chequeos que realiza son:
	 * 	- Que las entidades existen en la fuente de datos.
	 *  - Que los attributos de cada una de las entidades, están disponibles en la fuente de datos.
	 *  - Que el conjunto de claves especificado en las políticas de acceso, existe en la fuente de datos.
	 *  - Que el conjunto de filtros especificado en las políticas de acceso, se corresponde con su correspondiente conjunto de índices en la fuente de datos.
	 *  
	 * Nota: En este punto, es posible aprovechar el acceso a los metadatos de las entidades de la fuente de datos, para obtener el tipo de dato asociado a cada atributo de dato, clave o filtro.
	 * 
	 * @param accessPolicy Política de acceso a chequear.
	 * 
	 * @return toda la información necesaria de la fuente de datos, para resolver las peticiones.
	 *  
	 * @throws ServerException
	 */
	public abstract Map<String, EntityDataSourceInfo> checkAndGatherAccessPoliciesInformationAgainstDataSource(AccessPolicy accessPolicy) throws ServerException;

	/**
	 * Asigna la información de la fuente de datos, usada para resolver las peticiones. 
	 * 
	 * @param entitiesInformation La información de la fuente de datos, usada para resolver las peticiones a asignar.
	 */
	public synchronized void setAccessPoliciesInformationAgainstDataSource(Map<String, EntityDataSourceInfo> entitiesInformation) {
		
		this.entitiesInformation = entitiesInformation;
	}
	
	/**
	 * Elimina toda la información de la fuente de datos, usada para resolver las peticiones.
	 * 
	 * @see #checkAndGatherAccessPoliciesInformationAgainstDataSource(AccessPolicy)
	 */
	public synchronized void resetAccessPoliciesInformationAgainstDataSource() {

		entitiesInformation = null;
	}
}
