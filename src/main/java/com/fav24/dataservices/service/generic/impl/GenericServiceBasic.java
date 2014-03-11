package com.fav24.dataservices.service.generic.impl;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.ehcache.Element;

import com.fav24.dataservices.domain.Requestor;
import com.fav24.dataservices.domain.cache.Cache;
import com.fav24.dataservices.domain.generic.Generic;
import com.fav24.dataservices.domain.generic.Operation;
import com.fav24.dataservices.domain.security.AccessPolicy;
import com.fav24.dataservices.domain.security.EntityAccessPolicy;
import com.fav24.dataservices.domain.security.EntityAttribute;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.generic.GenericService;


/**
 * Implementación base de la interfaz de servicio Generic. 
 * 
 * Nota: Esta clase es thread safe, pero no sus instancias.
 * 
 * @author Fav24
 */
public abstract class GenericServiceBasic implements GenericService {


	/**
	 * Establece el inicio de transtacción en la que se resolverán las distintas operaciones.
	 * 
	 * @return true o false en función de si fué o no posible establecer un inicio de transacción.
	 */
	protected abstract boolean startTransaction();

	/**
	 * Establece el fin de transtacción en la que se han resuelto las distintas operaciones.
	 * 
	 * @param commit True o false en función de si el fin de transacción implica una confirmación de las operaciones realizadas
	 * 					dentro de la transacción, o estas deben ser revocadas.
	 * 
	 * @return true o false en función de si fué o no posible establecer un fin de transacción.
	 */
	protected abstract boolean endTransaction(boolean commit);

	/**
	 * {@inheritDoc}
	 */
	public Generic processGeneric(Generic generic) throws ServerException {

		if (startTransaction()) {

			try {
				for (Operation operation : generic.getOperations()) {
					processOperation(generic.getRequestor(), operation);
				}
			} catch (ServerException e) {
				endTransaction(false);
				throw e;
			}

			if (!endTransaction(true)) {
				endTransaction(false);

				throw new ServerException(ERROR_END_TRANSACTION, ERROR_END_TRANSACTION_MESSAGE);
			}
		}
		else {
			throw new ServerException(ERROR_START_TRANSACTION, ERROR_START_TRANSACTION_MESSAGE);
		}

		generic.getRequestor().setTime(System.currentTimeMillis());

		return generic;
	}

	/**
	 * Procesa en contenido de una estructura Operation.
	 * 
	 * @param requestor Solicitante de la operación.
	 * @param operation Estructura de la operación a procesar.
	 * 
	 * @return estructura operation de entrada, enriquecida con los resultados de la salida.
	 */
	protected Operation processOperation(Requestor requestor, Operation operation) throws ServerException {

		EntityAccessPolicy entityAccessPolicy = AccessPolicy.getCurrentAccesPolicy().getEntityPolicy(operation.getMetadata().getEntity());

		if (operation.getMetadata().hasKey()) {

			if (!entityAccessPolicy.containsKey(operation.getMetadata().getKey())) {
				throw new ServerException(ERROR_INVALID_REQUEST_KEY, String.format(ERROR_INVALID_REQUEST_KEY_MESSAGE, operation.getMetadata().getEntity()));
			}
		}
		else {

			if (entityAccessPolicy.getOnlyByKey()) {
				throw new ServerException(ERROR_INVALID_REQUEST_NO_KEY, String.format(ERROR_INVALID_REQUEST_NO_KEY_MESSAGE, operation.getMetadata().getEntity()));
			}

			if (operation.getMetadata().hasFilter()) {

				if (entityAccessPolicy.getOnlySpecifiedFilters() && !entityAccessPolicy.containsFilter(operation.getMetadata().getFilter())) {
					throw new ServerException(ERROR_INVALID_REQUEST_FILTER, String.format(ERROR_INVALID_REQUEST_FILTER_MESSAGE, operation.getMetadata().getEntity()));
				}
			}
			else if (entityAccessPolicy.getOnlySpecifiedFilters()) {
				throw new ServerException(ERROR_INVALID_REQUEST_NO_FILTER, String.format(ERROR_INVALID_REQUEST_NO_FILTER_MESSAGE, operation.getMetadata().getEntity()));
			}
		}

		switch(operation.getMetadata().getOperation()) {

		case CREATE:
			return create(requestor, operation);
		case UPDATE:
			return update(requestor, operation);
		case RETRIEVE:
			
			net.sf.ehcache.Cache cache = Cache.getSystemCache().getCache(operation.getMetadata().getEntity());
			
			if (cache == null) {
				return retreave(requestor, operation);
			}
			
			//Para garantizar que dos operaciones equivalentes, tiene la misma forma y representación.
			String contentKey = operation.organizeContent(new StringBuilder()).toString();
			
			Element cachedElement = cache.get(contentKey);
			if (cachedElement == null) {
				
				operation = retreave(requestor, operation);
				
				cachedElement = new Element(contentKey, operation);
				cache.put(cachedElement);
				
				return operation;
			}
			else {
				
				return (Operation) cachedElement.getObjectValue();
			}
			
		case DELETE:
			return delete(requestor, operation);
		case UPDATE_CREATE:
			return updateCreate(requestor, operation);
		}

		return operation;
	}

	/**
	 * Operatión de creación de ítems.
	 * 
	 * @param requestor Solicitante de la operación.
	 * @param operation Operación a procesar.
	 * 
	 * @return operación de entrada, enriquecida con los resultados de su ejecución.
	 */
	protected Operation create(Requestor requestor, Operation operation) throws ServerException {
		throw new ServerException(ERROR_OPERATION_NOT_AVAILABLE, String.format(ERROR_OPERATION_NOT_AVAILABLE_MESSAGE, operation.getMetadata().getOperation().getOperationType().toUpperCase()));
	}

	/**
	 * Operatión de modificación de ítems.
	 * 
	 * @param requestor Solicitante de la operación.
	 * @param operation Operación a procesar.
	 * 
	 * @return operación de entrada, enriquecida con los resultados de su ejecución.
	 */
	protected Operation update(Requestor requestor, Operation operation) throws ServerException {
		throw new ServerException(ERROR_OPERATION_NOT_AVAILABLE, String.format(ERROR_OPERATION_NOT_AVAILABLE_MESSAGE, operation.getMetadata().getOperation().getOperationType().toUpperCase()));
	}

	/**
	 * Operatión de retorno de ítems.
	 * 
	 * @param requestor Solicitante de la operación.
	 * @param operation Operación a procesar.
	 * 
	 * @return operación de entrada, enriquecida con los resultados de su ejecución.
	 */
	protected Operation retreave(Requestor requestor, Operation operation) throws ServerException {
		throw new ServerException(ERROR_OPERATION_NOT_AVAILABLE, String.format(ERROR_OPERATION_NOT_AVAILABLE_MESSAGE, operation.getMetadata().getOperation().getOperationType().toUpperCase()));
	}

	/**
	 * Operatión de eliminación de ítems.
	 * 
	 * @param requestor Solicitante de la operación.
	 * @param operation Operación a procesar.
	 * 
	 * @return operación de entrada, enriquecida con los resultados de su ejecución.
	 */
	protected Operation delete(Requestor requestor, Operation operation) throws ServerException {
		throw new ServerException(ERROR_OPERATION_NOT_AVAILABLE, String.format(ERROR_OPERATION_NOT_AVAILABLE_MESSAGE, operation.getMetadata().getOperation().getOperationType().toUpperCase()));
	}

	/**
	 * Operatión de modificación o creación de un ítem.
	 * 
	 * @param requestor Solicitante de la operación.
	 * @param operation Operación a procesar.
	 * 
	 * @return operación de entrada, enriquecida con los resultados de su ejecución.
	 */
	protected Operation updateCreate(Requestor requestor, Operation operation) throws ServerException {
		throw new ServerException(ERROR_OPERATION_NOT_AVAILABLE, String.format(ERROR_OPERATION_NOT_AVAILABLE_MESSAGE, operation.getMetadata().getOperation().getOperationType().toUpperCase()));
	}

	/**
	 * {@inheritDoc}
	 */
	public void checkAndGatherAccessPoliciesInformationAgainstDataSource(AccessPolicy accessPolicy) throws ServerException {
		throw new ServerException(GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED, GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE);
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
}