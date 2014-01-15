package com.fav24.dataservices.domain;

import java.util.AbstractList;


/**
 * Clase que contiene la estructura genérica de operaciones sobre entidades.
 * 
 * @author Fav24
 */
public class Generic {

	private Requestor requestor;
	private AbstractList<Operation> operations;

	
	/**
	 * Constructor por defecto.
	 */
	public Generic() {
		this(null, null);
	}
	
	/**
	 * Constructor.
	 * 
	 * @param requestor Solicitante de las operaciones.
	 * @param operations Lista de operacione solicitadas.
	 */
	public Generic(Requestor requestor, AbstractList<Operation> operations) {
		this.requestor = requestor;
		this.operations = operations;
	}
	
	/**
	 * Retorna el solicitante de las operaciones.
	 * 
	 * @return el solicitante de las operaciones.
	 */
	public Requestor getRequestor() {
		return requestor;
	}

	/**
	 * Asigna el solicitante de las operaciones.
	 * 
	 * @param requestor El solicitante de las operaciones a asignar.
	 */
	public void setRequestor(Requestor requestor) {
		this.requestor = requestor;
	}

	/**
	 * Retorna la lista de operaciones solicitada.
	 * 
	 * @return la lista de operaciones solicitada.
	 */
	public AbstractList<Operation> getOperations() {
		return operations;
	}

	/**
	 * Asigna la lista de operaciones solicitadas.
	 * 
	 * @param operations La lista de operaciones solicitadas a asignar.
	 */
	public void setOperations(AbstractList<Operation> operations) {
		this.operations = operations;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((operations == null) ? 0 : operations.hashCode());
		result = prime * result
				+ ((requestor == null) ? 0 : requestor.hashCode());
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
		Generic other = (Generic) obj;
		if (operations == null) {
			if (other.operations != null)
				return false;
		} else if (!operations.equals(other.operations))
			return false;
		if (requestor == null) {
			if (other.requestor != null)
				return false;
		} else if (!requestor.equals(other.requestor))
			return false;
		return true;
	}
	
	
}
