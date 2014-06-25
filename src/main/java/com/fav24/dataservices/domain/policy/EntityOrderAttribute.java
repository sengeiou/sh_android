package com.fav24.dataservices.domain.policy;



/**
 * Clase que define el sentido de la ordenación de un determinado atributo de una entidad. 
 */
public class EntityOrderAttribute extends EntityAttribute {

	/**
	 * Enumeración de los sentidos de ordenación permitidos para un determinado atributo de ordenación. 
	 */
	public enum Order {
		ASCENDING("Ascending"),
		DESCENDING("Descending");

		private final String order;

		
		/**
		 * Constructor privado del sentido de ordenación.
		 * 
		 * @param order Cadena de texto aue identifica el sentido de ordenación.
		 */
		Order(String order) {
			this.order = order;
		}

		/**
		 * Retorna la cadena de texto aue identifica el sentido de ordenación.
		 * 
		 * @return la cadena de texto aue identifica el sentido de ordenación.
		 */
		public String getOrder() {
			return order;
		}
		
		/**
		 * Retorna el sentido de ordenación a partir de la cadena de texto indicada.
		 * 
		 * @param text Cadena de texto a partir de la que se deduce el sentido de ordenación.
		 * 
		 * @return el sentido de ordenación a partir de la cadena de texto indicada.
		 */
		public static Order fromString(String text) {
			if (text != null) {
				for (Order order : Order.values()) {
					if (text.equalsIgnoreCase(order.order)) {
						return order;
					}
				}
			}
			return null;
		}
	}

	
	private Order order;
	
	
	/**
	 * Constructor por defecto.
	 */
	public EntityOrderAttribute() {
		this(null, null);
	}
	
	/**
	 * Constructor por defecto.
	 * 
	 * @param alias El alias de este atributo.
	 * @param order Sentido de ordenación.
	 */
	public EntityOrderAttribute(String alias, Order order) {
		
		super(alias, null);

		this.order = order;
	}

	/**
	 * Constructor de copia.
	 * 
	 * @param entityOrderAttribute Objeto referencia a copiar.
	 */
	public EntityOrderAttribute(EntityOrderAttribute entityOrderAttribute) {
		
		this(entityOrderAttribute.getAlias(), entityOrderAttribute.order);
	}
	
	/**
	 * Retorna el sentido de ordenación de los datos para este atributo.
	 *  
	 * @return el sentido de ordenación de los datos para este atributo.
	 */
	public Order getOrder() {
		return order;
	}

	/**
	 * Asigna el sentido de ordenación de los datos para este atributo.
	 *  
	 * @param order El sentido de ordenación a asignar.
	 */
	public void setOrder(Order order) {
		this.order = order;
	}
}
