package com.fav24.dataservices.domain;

import java.util.Map;


/**
 * Clase que contiene la estructura de un item de una entidad.
 * 
 * @author Fav24
 */
public class Item {

	private Map<String, Object> attributes;
	private Long revision; // Versión del item. 
	private Long birthDate; // Milisegundos desde epoch correspondientes al momento de la creación del item.
	private Long updateDate; // Milisegundos desde epoch correspondientes al momento de la modificación del item.
	private Long deleteDate; // Milisegundos desde epoch correspondientes al momento de la eliminación del item.


	/**
	 * Retorna los atributos del item en formato clave-valor.
	 * 
	 * @return los atributos del item en formato clave-valor.
	 */
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	/**
	 * Asigna los atributos del item en formato clave-valor.
	 * 
	 * @param attributes Los atributos a asignar.
	 */
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	/**
	 * Retorna el número de revisión en la que se encuentra el item.
	 * 
	 * @return el número de revisión en la que se encuentra el item.
	 */
	public Long getRevision() {
		return revision;
	}

	/**
	 * Asigna el número de revisión en la que se encuentra el item.
	 * 
	 * @param revision El número de revisión a asignar.
	 */
	public void setRevision(Long revision) {
		this.revision = revision;
	}

	/**
	 * Retorna la fecha en milisegundos desde epoch, en la que fué creado el ítem.
	 * 
	 * @return la fecha en milisegundos desde epoch, en la que fué creado el ítem.
	 */
	public Long getBirthDate() {
		return birthDate;
	}

	/**
	 * Asigna la fecha en milisegundos desde epoch, en la que fué creado el ítem.
	 * 
	 * @param birthDate La fecha en milisegundos a asignar.
	 */
	public void setBirthDate(Long birthDate) {
		this.birthDate = birthDate;
	}

	/**
	 * Retorn la fecha en milisegundos desde epoch, en la que fué modificado el ítem, por última vez.
	 * 
	 * @return la fecha en milisegundos desde epoch, en la que fué modificado el ítem, por última vez.
	 */
	public Long getUpdateDate() {
		return updateDate;
	}

	/**
	 * Asigna la fecha en milisegundos desde epoch, en la que fué modificado el ítem, por última vez.
	 * 
	 * @param updateDate La fecha en milisegundos a asignar.
	 */
	public void setUpdateDate(Long updateDate) {
		this.updateDate = updateDate;
	}

	/**
	 * Retorn la fecha en milisegundos desde epoch, en la que fué eliminado el ítem.
	 * 
	 * @return la fecha en milisegundos desde epoch, en la que fué eliminado el ítem.
	 */
	public Long getDeleteDate() {
		return deleteDate;
	}

	/**
	 * Asigna la fecha en milisegundos desde epoch, en la que fué eliminado el ítem.
	 * 
	 * @param deleteDate La fecha en milisegundos a asignar.
	 */
	public void setDeleteDate(Long deleteDate) {
		this.deleteDate = deleteDate;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attributes == null) ? 0 : attributes.hashCode());
		result = prime * result
				+ ((birthDate == null) ? 0 : birthDate.hashCode());
		result = prime * result
				+ ((deleteDate == null) ? 0 : deleteDate.hashCode());
		result = prime * result
				+ ((revision == null) ? 0 : revision.hashCode());
		result = prime * result
				+ ((updateDate == null) ? 0 : updateDate.hashCode());
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
		Item other = (Item) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		if (birthDate == null) {
			if (other.birthDate != null)
				return false;
		} else if (!birthDate.equals(other.birthDate))
			return false;
		if (deleteDate == null) {
			if (other.deleteDate != null)
				return false;
		} else if (!deleteDate.equals(other.deleteDate))
			return false;
		if (revision == null) {
			if (other.revision != null)
				return false;
		} else if (!revision.equals(other.revision))
			return false;
		if (updateDate == null) {
			if (other.updateDate != null)
				return false;
		} else if (!updateDate.equals(other.updateDate))
			return false;
		return true;
	}
}
