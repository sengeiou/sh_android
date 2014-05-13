package com.fav24.dataservices.domain.generic;

import java.util.AbstractList;

import com.fav24.dataservices.domain.BaseDomain;
import com.fav24.dataservices.domain.Requestor;


/**
 * Clase que contiene la estructura genérica de operaciones sobre entidades.
 * 
 * @author Fav24
 */
public class Generic extends BaseDomain {

	private AbstractList<Operation> operations;


	/**
	 * Constructor por defecto.
	 */
	public Generic() {
		this(null, null, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param alias Alias de la petición.
	 * @param requestor Solicitante.
	 * @param operations Lista de operaciones solicitadas.
	 */
	public Generic(String alias, Requestor requestor, AbstractList<Operation> operations) {

		super(alias, requestor);

		this.operations = operations;
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

		try {
			Generic other = (Generic) obj;
			if (operations == null) {
				if (other.operations != null)
					return false;
			} else if (!operations.equals(other.operations))
				return false;
		}
		catch(ClassCastException e) {
			return false;
		}

		return true;
	}
}
