package com.fav24.dataservices.domain.security;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



/**
 * Clase que define la sección Ordination de la definición de las políticas de acceso de una entidad.
 */
public class EntityOrdination {

	private AbstractList<EntityOrderAttribute> order;
	private Map<String, EntityOrderAttribute> entityOrderByAlias;


	/**
	 * Contructor por defecto.
	 */
	public EntityOrdination() {
		order = new ArrayList<EntityOrderAttribute>();
		entityOrderByAlias = new HashMap<String, EntityOrderAttribute>();
	}

	/**
	 * Constructor de copia.
	 * 
	 * @param ordination Objeto referencia a copiar.
	 */
	public EntityOrdination(EntityOrdination ordination) {

		if (ordination.order != null) {

			this.order = new ArrayList<EntityOrderAttribute>();
			this.entityOrderByAlias = new HashMap<String, EntityOrderAttribute>();

			for (EntityOrderAttribute attribute : ordination.order) {

				EntityOrderAttribute attributeCopy = new EntityOrderAttribute(attribute);

				this.order.add(attributeCopy);	
				this.entityOrderByAlias.put(attributeCopy.getAlias(), attributeCopy);	
			}
		}
		else {
			this.order = null;
			this.entityOrderByAlias = null;
		}
	}

	/**
	 * Retorna true o false en función de si existe o no el atributo correspondiente al alias indicado.
	 * 
	 * @param alias Alias a comprobar.
	 * 
	 * @return true o false en función de si existe o no el atributo correspondiente al alias indicado.
	 */
	public boolean hasAttribute(String alias) {

		if (alias != null) {

			return entityOrderByAlias.containsKey(alias);
		}

		return false;
	}

	/**
	 * Retorna el atributo correspondiente al alias indicado.
	 * 
	 * @param alias Alias del atributo a solicitar.
	 * 
	 * @return el atributo correspondiente al alias indicado.
	 */
	public EntityOrderAttribute getAttribute(String alias) {

		if (alias != null) {

			return entityOrderByAlias.get(alias);
		}

		return null;
	}

	/**
	 * Retorna el atributo correspondiente al nombre indicado.
	 * 
	 * @param alias Alias del atributo a solicitar.
	 * 
	 * @return el atributo correspondiente al alias indicado.
	 */
	public EntityOrderAttribute getAttributeByName(String name) {

		if (name != null) {

			for(EntityOrderAttribute attribute : order) {
				if (name.equals(attribute.getName())) {
					return attribute;
				}
			}
		}

		return null;
	}

	/**
	 * Retorna la estructura que contiene el conjunto de attributos de una clave.
	 * 
	 * Nota: no usar la lista de retorno para añadir atributos.
	 * @see #addOrderAttribute(EntityAttribute)
	 * 
	 * @return la estructura que contiene el conjunto de attributos de una clave.
	 */
	public final AbstractList<EntityOrderAttribute> getOrder() {
		return order;
	}

	/**
	 * Añade el atributo al conjunto de attributos de ordenación.
	 * 
	 * @param orderAttribute Atributo de ordenación a añadir.
	 */
	public void addOrderAttribute(EntityOrderAttribute orderAttribute) {

		order.add(orderAttribute);
		entityOrderByAlias.put(orderAttribute.getAlias(), orderAttribute);
	}
}
