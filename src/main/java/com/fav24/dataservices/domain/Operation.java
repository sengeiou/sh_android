package com.fav24.dataservices.domain;

import java.util.AbstractList;


/**
 * Estructura de una acción sobre una entidad.
 * 
 * @author Fav24
 */
public class Operation {

	private Metadata metadata;
	private AbstractList<DataItem> data;
	
	/**
	 * Retorna el conjunto de metadatos de la operación. 
	 * 
	 * @return el conjunto de metadatos de la operación.
	 */
	public Metadata getMetadata() {
		return metadata;
	}
	
	/**
	 * Asigna el conjunto de metadatos de la operación.
	 * 
	 * @param metadata El conjunto de metadatos a asoignar.
	 */
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}
	
	/**
	 * Retorna el conjunto de elementos implicados en la operación.
	 * 
	 * @return el conjunto de elementos implicados en la operación.
	 */
	public AbstractList<DataItem> getData() {
		return data;
	}
	
	/**
	 * Asigna el conjunto de elementos implicados en la operación.
	 * 
	 * @param data Conjunto de elementos a asignar.
	 */
	public void setData(AbstractList<DataItem> data) {
		this.data = data;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result
				+ ((metadata == null) ? 0 : metadata.hashCode());
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
		Operation other = (Operation) obj;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (metadata == null) {
			if (other.metadata != null)
				return false;
		} else if (!metadata.equals(other.metadata))
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Operation [metadata=" + metadata + ", data=" + data + "]";
	}
}
