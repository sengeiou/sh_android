package com.fav24.dataservices.domain;

import java.util.NavigableMap;
import java.util.TreeMap;


/**
 * Clase que contiene la estructura de un item de una entidad.
 * 
 * @author Fav24
 */
public class DataItem {

	/**
	 * Enumeración del conjunto de atributos interno y que debe tener toda entidad operable.
	 */
	public enum InternalAttribute {
		REVISION("revision"),
		BIRTH("birth"),
		MODIFIED("modified"),
		DELETED("deleted");

		private final String internalAttribute;

		/**
		 * Constructor privado del atributo interno.
		 * 
		 * @param internalAttribute Cadena de texto aue identifica el atributo interno.
		 */
		private InternalAttribute(String internalAttribute) {
			this.internalAttribute = internalAttribute;
		}

		/**
		 * Retorna el nombre con el que se identifica el atributo.
		 * 
		 * @return el nombre con el que se identifica el atributo.
		 */
		public String getInternalAttribute() {
			return internalAttribute;
		}

		/**
		 * Retorna true o false en función de si el atributo indicado tiene o no representación en esta enumeración.
		 * 
		 * @param internalAttribute Atributo a comprobar.
		 * 
		 * @return true o false en función de si el atributo indicado tiene o no representación en esta enumeración.
		 */
		public static boolean contains(String internalAttribute) {

			for (InternalAttribute choice : InternalAttribute.values()) {
				if (choice.name().equals(internalAttribute)) {
					return true;
				}
			}

			return false;
		}

		/**
		 * Retorna el atributo interno a partir de la cadena de texto indicada.
		 * 
		 * @param text Cadena de texto a partir de la que se deduce el atributo interno.
		 * 
		 * @return el atributo interno a partir de la cadena de texto indicada.
		 */
		public static InternalAttribute fromString(String text) {
			if (text != null) {
				for (InternalAttribute internalAttribute : InternalAttribute.values()) {
					if (text.equalsIgnoreCase(internalAttribute.internalAttribute)) {
						return internalAttribute;
					}
				}
			}

			return null;
		}
	}

	private NavigableMap<String, Object> attributes;
	private Long revision; // Versión del item. 
	private Long birth; // Milisegundos desde epoch correspondientes al momento de la creación del item.
	private Long modified; // Milisegundos desde epoch correspondientes al momento de la modificación del item.
	private Long deleted; // Milisegundos desde epoch correspondientes al momento de la eliminación del item.


	/**
	 * Constructor de copia.
	 * 
	 * @param reference Instancia de referencia.
	 */
	public DataItem(DataItem reference) {
		
		this.attributes = new TreeMap<String, Object>();
		this.attributes.putAll(reference.attributes);
		this.revision = reference.revision; 
		this.birth = reference.birth;
		this.modified = reference.modified;
		this.deleted = reference.deleted;
	}
	
	/**
	 * Constructor con parámetros.
	 * 
	 * @param attributes Atributos que contendrá este elemento.
	 */
	public DataItem(NavigableMap<String, Object> attributes) {
		setAttributes(attributes);
	}
	
	/**
	 * Retorna los atributos del item en formato clave-valor.
	 * 
	 * @return los atributos del item en formato clave-valor.
	 */
	public NavigableMap<String, Object> getAttributes() {
		
		return attributes;
	}

	/**
	 * Asigna los atributos del item en formato clave-valor.
	 * 
	 * @param attributes Los atributos a asignar.
	 */
	public void setAttributes(NavigableMap<String, Object> attributes) {
		this.attributes = attributes;

		if (attributes != null) {

			setRevision((Long) attributes.get(InternalAttribute.REVISION.getInternalAttribute()));
			setBirth((Long) attributes.get(InternalAttribute.BIRTH.getInternalAttribute()));
			setModified((Long) attributes.get(InternalAttribute.MODIFIED.getInternalAttribute()));
			setDeleted((Long) attributes.get(InternalAttribute.DELETED.getInternalAttribute()));
		}
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

		attributes.put(InternalAttribute.REVISION.getInternalAttribute(), revision);
	}

	/**
	 * Retorna la fecha en milisegundos desde epoch, en la que fué creado el ítem.
	 * 
	 * @return la fecha en milisegundos desde epoch, en la que fué creado el ítem.
	 */
	public Long getBirth() {
		return birth;
	}

	/**
	 * Asigna la fecha en milisegundos desde epoch, en la que fué creado el ítem.
	 * 
	 * @param birth La fecha en milisegundos a asignar.
	 */
	public void setBirth(Long birth) {
		this.birth = birth;

		attributes.put(InternalAttribute.BIRTH.getInternalAttribute(), birth);
	}

	/**
	 * Retorn la fecha en milisegundos desde epoch, en la que fué modificado el ítem, por última vez.
	 * 
	 * @return la fecha en milisegundos desde epoch, en la que fué modificado el ítem, por última vez.
	 */
	public Long getModified() {
		return modified;
	}

	/**
	 * Asigna la fecha en milisegundos desde epoch, en la que fué modificado el ítem, por última vez.
	 * 
	 * @param modified La fecha en milisegundos a asignar.
	 */
	public void setModified(Long modified) {
		this.modified = modified;

		attributes.put(InternalAttribute.MODIFIED.getInternalAttribute(), modified);
	}

	/**
	 * Retorn la fecha en milisegundos desde epoch, en la que fué eliminado el ítem.
	 * 
	 * @return la fecha en milisegundos desde epoch, en la que fué eliminado el ítem.
	 */
	public Long getDeleted() {
		return deleted;
	}

	/**
	 * Asigna la fecha en milisegundos desde epoch, en la que fué eliminado el ítem.
	 * 
	 * @param deleted La fecha en milisegundos a asignar.
	 */
	public void setDeleted(Long deleted) {
		this.deleted = deleted;

		attributes.put(InternalAttribute.DELETED.getInternalAttribute(), deleted);
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
				+ ((birth == null) ? 0 : birth.hashCode());
		result = prime * result
				+ ((deleted == null) ? 0 : deleted.hashCode());
		result = prime * result
				+ ((revision == null) ? 0 : revision.hashCode());
		result = prime * result
				+ ((modified == null) ? 0 : modified.hashCode());
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
		DataItem other = (DataItem) obj;
		if (attributes == null) {
			if (other.attributes != null)
				return false;
		} else if (!attributes.equals(other.attributes))
			return false;
		if (birth == null) {
			if (other.birth != null)
				return false;
		} else if (!birth.equals(other.birth))
			return false;
		if (deleted == null) {
			if (other.deleted != null)
				return false;
		} else if (!deleted.equals(other.deleted))
			return false;
		if (revision == null) {
			if (other.revision != null)
				return false;
		} else if (!revision.equals(other.revision))
			return false;
		if (modified == null) {
			if (other.modified != null)
				return false;
		} else if (!modified.equals(other.modified))
			return false;
		return true;
	}
}
