package com.fav24.dataservices.services.impl;

import com.fav24.dataservices.domain.Generic;
import com.fav24.dataservices.domain.Operation;
import com.fav24.dataservices.domain.Requestor;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.services.GenericService;
import com.fav24.dataservices.to.GenericResultTO;
import com.fav24.dataservices.to.GenericTO;


/**
 * Implementación base de la interfaz de servicio Generic. 
 * 
 * Nota: Esta clase es thread safe, pero no sus instancias.
 * 
 * @author Fav24
 */
public abstract class GenericServiceBasic implements GenericService {

	public static final String ERROR_OPERATION_NOT_AVAILABLE = "G000";
	public static final String ERROR_START_TRANSACTION = "G001";
	public static final String ERROR_END_TRANSACTION = "G002";
	public static final String ERROR_UNCOMPLETE_REQUEST = "G003";

	/**
	 * {@inheritDoc}
	 */
	public GenericResultTO processGeneric(GenericTO generic) {

		GenericResultTO resultTO = new GenericResultTO();

		resultTO.setRequestor(generic.getRequestor());
		try {
			resultTO.setGeneric(processGeneric(generic.getRequestor(), generic.getGeneric()));
		} catch (ServerException e) {
			resultTO.setGeneric(generic.getGeneric());
			resultTO.setErrorCode(e.getErrorCode());
			resultTO.setMessage(e.getMessage());
		}

		return resultTO;
	}

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
	 * Procesa en contenido de una estructura Generic.
	 * 
	 * @param requestor Solicitante de la operación.
	 * @param generic Estructura generica de operaciones a procesar.
	 * 
	 * @return estructura generic de entrada, enriquecida con los resultados de la salida.
	 */
	protected Generic processGeneric(Requestor requestor, Generic generic) throws ServerException {

		if (startTransaction()) {

			try {
				for (Operation operation : generic.getOperations()) {
					processOperation(requestor, operation);
				}
			} catch (ServerException e) {
				endTransaction(false);
				throw e;
			}

			if (!endTransaction(true)) {
				endTransaction(false);

				throw new ServerException(ERROR_END_TRANSACTION, "Error al finalizar la transacción.");
			}
		}
		else {
			throw new ServerException(ERROR_START_TRANSACTION, "Error al iniciar la transacción.");
		}

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

		switch(operation.getMetadata().getOperation()) {

		case CREATE:
			return create(requestor, operation);
		case UPDATE:
			return update(requestor, operation);
		case RETRIEVE:
			return retreave(requestor, operation);
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
		throw new ServerException(ERROR_OPERATION_NOT_AVAILABLE, "La opreación CREATE no está disponible.");
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
		throw new ServerException(ERROR_OPERATION_NOT_AVAILABLE, "La opreación UPDATE no está disponible.");
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
		throw new ServerException(ERROR_OPERATION_NOT_AVAILABLE, "La opreación RETREAVE no está disponible.");
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
		throw new ServerException(ERROR_OPERATION_NOT_AVAILABLE, "La opreación DELETE no está disponible.");
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
		throw new ServerException(ERROR_OPERATION_NOT_AVAILABLE, "La opreación UPDATE no está disponible.");
	}
}
