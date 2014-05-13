package com.fav24.dataservices.domain.security;



/**
 * Clase que define un atributo que hace referencia a un dato de una entidad. 
 */
public class EntityDataAttribute extends EntityAttribute {

	public static final long DEFAFULT_REVISION = 0;

	/**
	 * Enumeración de los campos de sincronización de una entidad. 
	 */
	public enum SynchronizationField {
		REVISION("revision"),
		BIRTH("birth"),
		MODIFIED("modified"),
		DELETED("deleted");

		private final String synchronizationField;


		/**
		 * Constructor privado del campo de sincronización.
		 * 
		 * @param synchronizationField Cadena de texto aue identifica el campo.
		 */
		SynchronizationField(String synchronizationField) {
			this.synchronizationField = synchronizationField;
		}

		/**
		 * Retorna la cadena de texto que identifica este campo de sincronización.
		 * 
		 * @return la cadena de texto que identifica este campo de sincronización.
		 */
		public String getSynchronizationField() {
			return synchronizationField;
		}

		/**
		 * Retorna el campo de sincronización a partir de la cadena de texto indicada.
		 * 
		 * @param text Cadena de texto a partir de la que se deduce el campo de sincronización.
		 * 
		 * @return el campo de sincronización a partir de la cadena de texto indicada.
		 */
		public static SynchronizationField fromString(String text) {
			if (text != null) {
				for (SynchronizationField synchronizationField : SynchronizationField.values()) {
					if (text.equalsIgnoreCase(synchronizationField.synchronizationField)) {
						return synchronizationField;
					}
				}
			}
			return null;
		}

		/**
		 * Retorna true o false en función de si el nombre suministrado se corresponde o no 
		 * con un campo de auditoría para la sincronización.
		 * 
		 * @param field Nombre a comparar.
		 * 
		 * @return true o false en función de si el nombre suministrado se corresponde o no 
		 * con un campo de auditoría para la sincronización.
		 */
		public static boolean isSynchronizationField(String field) {

			if (REVISION.getSynchronizationField().equals(field)) {
				return true;
			}

			if (BIRTH.getSynchronizationField().equals(field)) {
				return true;
			}

			if (MODIFIED.getSynchronizationField().equals(field)) {
				return true;
			}

			if (DELETED.getSynchronizationField().equals(field)) {
				return true;
			}

			return false;
		}
	}

	/**
	 * Enumeración de las direcciones permitidas para un determinado atributo. 
	 */
	public enum Direction {
		INPUT("input"),
		OUTPUT("output"),
		BOTH("both");

		private final String direction;


		/**
		 * Constructor privado de la dirección.
		 * 
		 * @param direction Cadena de texto aue identifica la dirección.
		 */
		Direction(String direction) {
			this.direction = direction;
		}

		/**
		 * Retorna la cadena de texto que identifica esta dirección.
		 * 
		 * @return la cadena de texto que identifica esta dirección.
		 */
		public String getDirection() {
			return direction;
		}

		/**
		 * Retorna la dirección a partir de la cadena de texto indicada.
		 * 
		 * @param text Cadena de texto a partir de la que se deduce la dirección.
		 * 
		 * @return la dirección a partir de la cadena de texto indicada.
		 */
		public static Direction fromString(String text) {
			if (text != null) {
				for (Direction direction : Direction.values()) {
					if (text.equalsIgnoreCase(direction.direction)) {
						return direction;
					}
				}
			}
			return null;
		}
	}


	private Direction direction;


	/**
	 * Constructor por defecto.
	 */
	public EntityDataAttribute() {
		this(null, null, null);
	}

	/**
	 * Constructor por defecto.
	 * 
	 * @param alias El alias de este atributo.
	 * @param name El nombre de este atributo.
	 * @param direction Dirección del flujo de información.
	 */
	public EntityDataAttribute(String alias, String name, Direction direction) {

		super(alias, name);

		this.direction = direction;
	}

	/**
	 * Constructor de copia.
	 * 
	 * @param entityDataAttribute Objeto referencia a copiar.
	 */
	public EntityDataAttribute(EntityDataAttribute entityDataAttribute) {

		this(entityDataAttribute.getAlias(), entityDataAttribute.getName(), entityDataAttribute.direction);
	}

	/**
	 * Retorna el sentido en el que pueden viajar los datos para este atributo.
	 *  
	 * @return el sentido en el que pueden viajar los datos para este atributo.
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * Asigna el sentido en el que pueden viajar los datos para este atributo.
	 *  
	 * @param direction El sentido a asignar.
	 */
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
}
