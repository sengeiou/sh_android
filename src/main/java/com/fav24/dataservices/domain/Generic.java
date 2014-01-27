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
	 * @param requestor Solicitante.
	 * @param operations Lista de operaciones solicitadas.
	 */
	public Generic(Requestor requestor, AbstractList<Operation> operations) {
		this.requestor = requestor;
		this.operations = operations;
	}

	/**
	 * Retorna el solicitante.
	 * 
	 * @return el solicitante.
	 */
	public Requestor getRequestor() {
		return requestor;
	}

	/**
	 * Asigna el solicitante.
	 * 
	 * @param requestor El solicitante a asignar.
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
		return true;
	}


}
