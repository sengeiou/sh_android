package com.fav24.dataservices.service.generic.impl;

import java.sql.Timestamp;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.ehcache.Element;

import org.springframework.beans.factory.annotation.Autowired;

import com.fav24.dataservices.domain.Requestor;
import com.fav24.dataservices.domain.cache.Cache;
import com.fav24.dataservices.domain.generic.Generic;
import com.fav24.dataservices.domain.generic.Operation;
import com.fav24.dataservices.domain.security.AccessPolicy;
import com.fav24.dataservices.domain.security.EntityAccessPolicy;
import com.fav24.dataservices.domain.security.EntityAccessPolicy.OperationType;
import com.fav24.dataservices.domain.security.EntityAttribute;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.generic.GenericService;
import com.fav24.dataservices.service.security.AccessPolicyService;
import com.fav24.dataservices.service.system.SystemService;


/**
 * Implementación base de la interfaz de servicio Generic. 
 * 
 * Nota: Esta clase es thread safe, pero no sus instancias.
 */
public abstract class GenericServiceBasic implements GenericService {

	@Autowired
	protected SystemService systemService;


	/**
	 * Establece el inicio de transtacción en la que se resolverán las distintas operaciones.
	 * 
	 * @throws ServerException 
	 */
	protected abstract void startTransaction() throws ServerException;

	/**
	 * Establece el fin de transtacción en la que se han resuelto las distintas operaciones.
	 * 
	 * @param commit True o false en función de si el fin de transacción implica una confirmación de las operaciones realizadas
	 * 					dentro de la transacción, o estas deben ser revocadas.
	 * 
	 * @throws ServerException 
	 */
	protected abstract void endTransaction(boolean commit) throws ServerException;

	/**
	 * {@inheritDoc}
	 */
	public Generic processGeneric(Generic generic) throws ServerException {

		systemService.getWorkloadMeter().incTotalIncommingRequests();

		if (AccessPolicy.getCurrentAccesPolicy() == null) {

			systemService.getWorkloadMeter().incTotalIncommingRequestsErrors();

			throw new ServerException(AccessPolicyService.ERROR_NO_CURRENT_POLICY_DEFINED, AccessPolicyService.ERROR_NO_CURRENT_POLICY_DEFINED_MESSAGE);	
		}

		startTransaction();

		try {

			for (Operation operation : generic.getOperations()) {
				processOperation(generic.getRequestor(), operation);
			}

		} catch (ServerException e) {

			endTransaction(false);

			throw e;
		}

		endTransaction(true);

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
	protected final Operation processOperation(Requestor requestor, Operation operation) throws ServerException {

		if (operation.getMetadata() == null) {

			throw new ServerException(ERROR_MALFORMED_REQUEST, String.format(ERROR_MALFORMED_REQUEST_MESSAGE, "metadata"));
		}

		EntityAccessPolicy entityAccessPolicy = AccessPolicy.getEntityPolicy(operation.getMetadata().getEntity());

		if (entityAccessPolicy == null) {
			throw new ServerException(ERROR_MALFORMED_REQUEST, String.format(ERROR_MALFORMED_REQUEST_MESSAGE, "entity"));
		}

		if (!entityAccessPolicy.getAllowedOperations().contains(operation.getMetadata().getOperation())) {
			throw new ServerException(ERROR_OPERATION_NOT_AVAILABLE, String.format(ERROR_OPERATION_NOT_AVAILABLE_MESSAGE, operation.getMetadata().getOperation().getOperationType(), 
					operation.getMetadata().getEntity()));
		}

		try {
			systemService.getWorkloadMeter().incTotalIncommingOperations();

			if (operation.getMetadata().getOperation() != OperationType.CREATE) {

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
			}
		}
		catch (ServerException e) {
			systemService.getWorkloadMeter().incTotalIncommingOperationsErrors();

			throw e;
		}

		switch(operation.getMetadata().getOperation()) {

		case CREATE:
			return create(requestor, operation);
		case UPDATE:
			return update(requestor, operation);
		case RETRIEVE:

			if (Cache.getSystemCache() != null) {

				net.sf.ehcache.Cache cache = Cache.getSystemCache().getCache(operation.getMetadata().getEntity());

				if (cache != null) {

					//Para garantizar que dos operaciones equivalentes, tiene la misma forma y representación.
					String contentKey = operation.organizeContent(new StringBuilder()).toString();

					Element cachedElement = cache.get(contentKey);
					if (cachedElement == null) {

						systemService.getWorkloadMeter().incTotalSubsystemOutcommingOperations();

						try {

							operation = retreave(requestor, operation);
						}
						catch (ServerException e) {
							systemService.getWorkloadMeter().incTotalSubsystemOutcommingOpertionsErrors();

							throw e;
						}

						cachedElement = new Element(contentKey, operation);
						cache.put(cachedElement);

						return operation;
					}
					else {

						Operation recycledOperation = (Operation) cachedElement.getObjectValue(); 

						operation.setMetadata(recycledOperation.getMetadata());
						operation.setData(recycledOperation.getData());

						return operation;
					}
				}
			}

			try {

				systemService.getWorkloadMeter().incTotalSubsystemOutcommingOperations();

				return retreave(requestor, operation);
			}
			catch (ServerException e) {
				systemService.getWorkloadMeter().incTotalSubsystemOutcommingOpertionsErrors();

				throw e;
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
		throw new ServerException(ERROR_OPERATION_NOT_AVAILABLE, String.format(ERROR_OPERATION_NOT_AVAILABLE_MESSAGE, operation.getMetadata().getOperation().getOperationType(), "*"));
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
		throw new ServerException(ERROR_OPERATION_NOT_AVAILABLE, String.format(ERROR_OPERATION_NOT_AVAILABLE_MESSAGE, operation.getMetadata().getOperation().getOperationType(), "*"));
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
		throw new ServerException(ERROR_OPERATION_NOT_AVAILABLE, String.format(ERROR_OPERATION_NOT_AVAILABLE_MESSAGE, operation.getMetadata().getOperation().getOperationType(), "*"));
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
		throw new ServerException(ERROR_OPERATION_NOT_AVAILABLE, String.format(ERROR_OPERATION_NOT_AVAILABLE_MESSAGE, operation.getMetadata().getOperation().getOperationType(), "*"));
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
		throw new ServerException(ERROR_OPERATION_NOT_AVAILABLE, String.format(ERROR_OPERATION_NOT_AVAILABLE_MESSAGE, operation.getMetadata().getOperation().getOperationType(), "*"));

	}

	/**
	 * Retorna true o false en función de si el registro entrante prevalece o no sobre el existente.
	 * 
	 * @param localModified Fecha de última modificación del registro existente.
	 * @param remoteModified Fecha de última modificación del registro entrante.
	 * @param localRevision Número de revisión del registro existente.
	 * @param remoteRevision Número de revisión del registro entrante.
	 * @param positiveRevisionThreshold Margen a sumar a la revisión de un elemento entrante antes de su comparación con el existente, 
	 * 									en caso de que el elemento entrante tenga una fecha de modificación posterior al existente.
	 * @param negativeRevisionThreshold Margen a restar a la revisión de un elemento entrante antes de su comparación con el existente, 
	 * 									en caso de que el elemento entrante tenga una fecha de modificación anterior al existente.
	 * 
	 * @return true o false en función de si el registro entrante prevalece o no sobre el existente.
	 */
	protected boolean incommingItemWins(Timestamp localModified, Timestamp remoteModified, Long localRevision, Long remoteRevision, 
			Long positiveRevisionThreshold, Long negativeRevisionThreshold) {

		if (remoteModified.after(localModified)) {

			return ((remoteRevision + positiveRevisionThreshold) >= localRevision);
		}
		else if (remoteModified.before(localModified)) {

			return ((remoteRevision - negativeRevisionThreshold) >= localRevision);
		}

		return (remoteRevision > localRevision);
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