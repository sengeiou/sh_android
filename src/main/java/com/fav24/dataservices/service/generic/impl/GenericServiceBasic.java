package com.fav24.dataservices.service.generic.impl;

import java.sql.Timestamp;

import net.sf.ehcache.Element;

import org.springframework.beans.factory.annotation.Autowired;

import com.fav24.dataservices.domain.Requestor;
import com.fav24.dataservices.domain.cache.Cache;
import com.fav24.dataservices.domain.generic.Generic;
import com.fav24.dataservices.domain.generic.Operation;
import com.fav24.dataservices.domain.security.AccessPolicy;
import com.fav24.dataservices.domain.security.EntityAccessPolicy;
import com.fav24.dataservices.domain.security.EntityAccessPolicy.OperationType;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.generic.GenericService;
import com.fav24.dataservices.service.security.AccessPolicyService;
import com.fav24.dataservices.service.system.SystemService;


/**
 * Implementación base de la interfaz de servicio Generic. 
 * 
 * @param T Tipo de conexión. 
 * 
 * Nota: Esta clase es thread safe, pero no garantiza sus descendientes.
 */
public abstract class GenericServiceBasic<T> implements GenericService {

	@Autowired
	protected SystemService systemService;


	/**
	 * Establece el inicio de transtacción en la que se resolverán las distintas operaciones.
	 * 
	 * @return la conexión con la que se abrió la transacción.
	 * 
	 * @throws ServerException 
	 */
	protected abstract T startTransaction() throws ServerException;

	/**
	 * Establece el fin de transtacción en la que se han resuelto las distintas operaciones.
	 * 
	 * @param connection Conexión con la que se inició la transacción.
	 * @param commit True o false en función de si el fin de transacción implica una confirmación de las operaciones realizadas
	 * 					dentro de la transacción, o estas deben ser revocadas.
	 * 
	 * @throws ServerException 
	 */
	protected abstract void endTransaction(T connection, boolean commit) throws ServerException;

	/**
	 * {@inheritDoc}
	 */
	public Generic processGeneric(Generic generic) throws ServerException {

		systemService.getWorkloadMeter().incTotalIncommingRequests();

		if (AccessPolicy.getCurrentAccesPolicy() == null) {

			systemService.getWorkloadMeter().incTotalIncommingRequestsErrors();

			throw new ServerException(AccessPolicyService.ERROR_NO_CURRENT_POLICY_DEFINED, AccessPolicyService.ERROR_NO_CURRENT_POLICY_DEFINED_MESSAGE);	
		}

		T connecion = startTransaction();

		try {

			for (Operation operation : generic.getOperations()) {
				processOperation(connecion, generic.getRequestor(), operation);
			}

		} catch (ServerException e) {

			endTransaction(connecion, false);

			throw e;
		}

		endTransaction(connecion, true);

		generic.getRequestor().setTime(System.currentTimeMillis());

		return generic;
	}

	/**
	 * Procesa en contenido de una estructura Operation.
	 * 
	 * @param connection Conexión con la que se inició la transacción.
	 * @param requestor Solicitante de la operación.
	 * @param operation Estructura de la operación a procesar.
	 * 
	 * @return estructura operation de entrada, enriquecida con los resultados de la salida.
	 */
	protected final Operation processOperation(T connection, Requestor requestor, Operation operation) throws ServerException {

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
			return create(connection, requestor, operation);
		case UPDATE:
			return update(connection, requestor, operation);
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

							operation = retreave(connection, requestor, operation);
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

				return retreave(connection, requestor, operation);
			}
			catch (ServerException e) {
				systemService.getWorkloadMeter().incTotalSubsystemOutcommingOpertionsErrors();

				throw e;
			}

		case DELETE:
			return delete(connection, requestor, operation);
		case UPDATE_CREATE:
			return updateCreate(connection, requestor, operation);
		}

		return operation;
	}

	/**
	 * Operatión de creación de ítems.
	 * 
	 * @param connection Conexión con la que se inició la transacción.
	 * @param requestor Solicitante de la operación.
	 * @param operation Operación a procesar.
	 * 
	 * @return operación de entrada, enriquecida con los resultados de su ejecución.
	 */
	protected Operation create(T connection, Requestor requestor, Operation operation) throws ServerException {
		throw new ServerException(ERROR_OPERATION_NOT_AVAILABLE, String.format(ERROR_OPERATION_NOT_AVAILABLE_MESSAGE, operation.getMetadata().getOperation().getOperationType(), "*"));
	}

	/**
	 * Operatión de modificación de ítems.
	 * 
	 * @param connection Conexión con la que se inició la transacción.
	 * @param requestor Solicitante de la operación.
	 * @param operation Operación a procesar.
	 * 
	 * @return operación de entrada, enriquecida con los resultados de su ejecución.
	 */
	protected Operation update(T connection, Requestor requestor, Operation operation) throws ServerException {
		throw new ServerException(ERROR_OPERATION_NOT_AVAILABLE, String.format(ERROR_OPERATION_NOT_AVAILABLE_MESSAGE, operation.getMetadata().getOperation().getOperationType(), "*"));
	}

	/**
	 * Operatión de retorno de ítems.
	 * 
	 * @param connection Conexión con la que se inició la transacción.
	 * @param requestor Solicitante de la operación.
	 * @param operation Operación a procesar.
	 * 
	 * @return operación de entrada, enriquecida con los resultados de su ejecución.
	 */
	protected Operation retreave(T connection, Requestor requestor, Operation operation) throws ServerException {
		throw new ServerException(ERROR_OPERATION_NOT_AVAILABLE, String.format(ERROR_OPERATION_NOT_AVAILABLE_MESSAGE, operation.getMetadata().getOperation().getOperationType(), "*"));
	}

	/**
	 * Operatión de eliminación de ítems.
	 * 
	 * @param connection Conexión con la que se inició la transacción.
	 * @param requestor Solicitante de la operación.
	 * @param operation Operación a procesar.
	 * 
	 * @return operación de entrada, enriquecida con los resultados de su ejecución.
	 */
	protected Operation delete(T connection, Requestor requestor, Operation operation) throws ServerException {
		throw new ServerException(ERROR_OPERATION_NOT_AVAILABLE, String.format(ERROR_OPERATION_NOT_AVAILABLE_MESSAGE, operation.getMetadata().getOperation().getOperationType(), "*"));
	}

	/**
	 * Operatión de modificación o creación de un ítem.
	 * 
	 * @param connection Conexión con la que se inició la transacción.
	 * @param requestor Solicitante de la operación.
	 * @param operation Operación a procesar.
	 * 
	 * @return operación de entrada, enriquecida con los resultados de su ejecución.
	 */
	protected Operation updateCreate(T connection, Requestor requestor, Operation operation) throws ServerException {
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
}