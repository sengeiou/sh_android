package com.fav24.dataservices.security;


/**
 * Clase que define un atributo que hace referencia a un dato de una entidad. 
 * 
 * @author Fav24
 */
public class EntityDataAttribute extends EntityAttribute {

	/**
	 * Enumeración de las direcciones permitidas para un determinado atributo. 
	 */
	public enum Direction {
		INPUT("input"),
		OUTPUT("ouput"),
		BOTH("both");

		private final String direction;

		Direction(String direction) {
			this.direction = direction;
		}

		public String getDirection() {
			return direction;
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
