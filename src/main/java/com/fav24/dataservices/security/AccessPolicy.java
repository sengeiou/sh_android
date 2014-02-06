package com.fav24.dataservices.security;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fav24.dataservices.domain.Requestor;


/**
 * Clase que contiene la estructura genérica de políticas de acceso sobre entidades.
 * 
 * @author Fav24
 */
public class AccessPolicy {

	private static AccessPolicy currentAccesPolicy;

	private Requestor requestor;
	private Set<EntityAccessPolicy> accessPolicies;
	private Map<String, EntityAccessPolicy> accessPoliciesByAlias;


	/**
	 * Constructor por defecto.
	 */
	public AccessPolicy() {

		this((Set<EntityAccessPolicy>)null);
	}

	/**
	 * Constructor.
	 * 
	 * @param accessPolicies Lista de políticas solicitadas.
	 */
	public AccessPolicy(Set<EntityAccessPolicy> accessPolicies) {

		this.accessPolicies = accessPolicies;

		if (accessPolicies != null) {
			this.accessPoliciesByAlias = new HashMap<String, EntityAccessPolicy>();

			for (EntityAccessPolicy entityAccessPolicy : accessPolicies) {
				this.accessPoliciesByAlias.put(entityAccessPolicy.getName().getAlias(), entityAccessPolicy);
			}
		}
	}

	/**
	 * Constructor de copia.
	 * 
	 * @param accesPolicy Objeto referencia a copiar.
	 */
	public AccessPolicy(AccessPolicy accesPolicy) {

		if (accesPolicy.requestor != null) {

			requestor = new Requestor(accesPolicy.requestor);
		}
		else {

			requestor = null;
		}

		if (accesPolicy.accessPolicies != null) {

			accessPolicies = new HashSet<EntityAccessPolicy>();
			accessPoliciesByAlias = new HashMap<String, EntityAccessPolicy>();
			for (EntityAccessPolicy entityAccessPolicy : accesPolicy.accessPolicies) {

				EntityAccessPolicy entityAccessPolicyCopy = new EntityAccessPolicy(entityAccessPolicy);

				accessPolicies.add(entityAccessPolicyCopy);
				accessPoliciesByAlias.put(entityAccessPolicyCopy.getName().getAlias(), entityAccessPolicyCopy);
			}
		}
		else {
			accessPolicies = null;
			accessPoliciesByAlias = null;
		}
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
	 * Retorna el conjunto de políticas solicitada.
	 * 
	 * @return el conjunto de políticas solicitada.
	 */
	public Set<EntityAccessPolicy> getAccessPolicies() {
		return accessPolicies;
	}

	/**
	 * Asigna el conjunto de políticas solicitadas.
	 * 
	 * @param accessPolicies El conjunto de políticas solicitadas.
	 */
	public void setAccessPolicies(Set<EntityAccessPolicy> accessPolicies) {
		this.accessPolicies = accessPolicies;

		if (accessPolicies != null) {
			this.accessPoliciesByAlias = new HashMap<String, EntityAccessPolicy>();

			for (EntityAccessPolicy entityAccessPolicy : accessPolicies) {
				this.accessPoliciesByAlias.put(entityAccessPolicy.getName().getAlias(), entityAccessPolicy);
			}
		}
		else {
			this.accessPoliciesByAlias = null;
		}
	}

	/**
	 * Retorna las políticas de la entidad con el alias indicado.
	 * 
	 * @param alias Alias de la entidad de la que se desea obtener sus políticas.
	 * 
	 * @return las políticas de la entidad con el alias indicado.
	 */
	public EntityAccessPolicy getEntityPolicy(String alias) {

		if (accessPoliciesByAlias != null && alias != null) {

			accessPoliciesByAlias.get(alias);
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((accessPolicies == null) ? 0 : accessPolicies.hashCode());
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
		AccessPolicy other = (AccessPolicy) obj;
		if (accessPolicies == null) {
			if (other.accessPolicies != null)
				return false;
		} else if (!accessPolicies.equals(other.accessPolicies))
			return false;
		return true;
	}

	/**
	 * Retorna las políticas de acceso efectivas en este momento.
	 * 
	 * @return las políticas de acceso efectivas en este momento.
	 */
	public static final AccessPolicy getCurrentAccesPolicy() {
		return currentAccesPolicy;
	}

	/**
	 * Asigna las políticas de acceso efectivas a partir de este momento.
	 * 
	 * @param accessPolicy Nuevas políticas a asignar.
	 */
	public static final void setCurrentAccesPolicy(final AccessPolicy accessPolicy) {

		synchronized(AccessPolicy.class) {
			currentAccesPolicy = accessPolicy;
		}
	}

	/**
	 * Modifica las políticas de acceso, sustituyendo las existentes coincidentes por las indicadas por parámetro.
	 * 
	 * @param accessPolicy Políticas a añadir/sustituir.
	 */
	public void mergeAccesPolicy(final AccessPolicy accessPolicy) {

		mergeAccesPolicy(accessPolicy.accessPolicies);
	}

	/**
	 * Modifica las políticas de acceso, sustituyendo las existentes coincidentes por las indicadas por parámetro.
	 * 
	 * @param accessPolicy Políticas a añadir/sustituir.
	 */
	public void mergeAccesPolicy(final Set<EntityAccessPolicy> accessPolicies) {

		synchronized(this) {

			if (accessPolicies != null) {

				if (this.accessPolicies == null) {

					this.accessPolicies = new HashSet<EntityAccessPolicy>();
					this.accessPolicies.addAll(accessPolicies);

					this.accessPoliciesByAlias = new HashMap<String, EntityAccessPolicy>();
					for (EntityAccessPolicy entityAccessPolicy : accessPolicies) {
						this.accessPoliciesByAlias.put(entityAccessPolicy.getName().getAlias(), entityAccessPolicy);
					}
				}
				else {

					for (EntityAccessPolicy entityAccessPolicy : accessPolicies) {

						if (!this.accessPolicies.add(entityAccessPolicy)) {

							this.accessPolicies.remove(entityAccessPolicy);
							this.accessPolicies.add(entityAccessPolicy);
							this.accessPoliciesByAlias.put(entityAccessPolicy.getName().getAlias(), entityAccessPolicy);
						}
					}
				}
			}
		}
	}

	/**
	 * Modifica las políticas de acceso efectivas a partir de este momento.
	 * 
	 * En el caso de coincidir entidades, sustituye las existentes por las indicadas por parámetro.
	 * 
	 * @param accessPolicy Políticas a añadir/sustituir.
	 */
	public static final void mergeCurrentAccesPolicy(final AccessPolicy accessPolicy) {

		synchronized(AccessPolicy.class) {

			if (currentAccesPolicy == null) {
				currentAccesPolicy = new AccessPolicy(accessPolicy);
			}
			else {
				currentAccesPolicy.mergeAccesPolicy(accessPolicy);
			}
		}
	}

	/**
	 * Retorna el nombre del atributo indicado de la entidad indicada en la fuente de datos, a partir de los alias. 
	 * 
	 * @param entityAlias Alias de la entidad.
	 * @param attributeAlias Alias del atributo.
	 * 
	 * @return el nombre del atributo indicado de la entidad indicada en la fuente de datos, a partir de los alias.
	 */
	public static final String getAttributeName(String entityAlias, String attributeAlias) {

		if (currentAccesPolicy.accessPoliciesByAlias != null && entityAlias != null && attributeAlias != null) {
			EntityAccessPolicy entityAccessPolicy = currentAccesPolicy.accessPoliciesByAlias.get(entityAlias);

			if (entityAccessPolicy != null && entityAccessPolicy.getData() != null) {
				EntityDataAttribute dataAttribute = entityAccessPolicy.getData().getAttribute(attributeAlias);
				
				if (dataAttribute != null) {
					return dataAttribute.getName();
				}
				else {
					EntityAttribute keyAttribute = entityAccessPolicy.getKeys().getFirstKeyAttributeByAlias(attributeAlias);
					
					if (keyAttribute != null) {
						return keyAttribute.getName();
					}
					else {
						EntityAttribute filterAttribute = entityAccessPolicy.getFilters().getFirstFilterAttributeByAlias(attributeAlias);
						
						if (filterAttribute != null) {
							return filterAttribute.getName();
						}
					}
				}
			}
		}
		
		return null;
	}

	/**
	 * Retorna el nombre de la entidad indicada en la fuente de datos, a partir de los alias. 
	 * 
	 * @param entityAlias Alias de la entidad.
	 * 
	 * @return el nombre de la entidad indicada en la fuente de datos, a partir de los alias.
	 */
	public static final String getEntityName(String entityAlias) {
		
		if (currentAccesPolicy.accessPoliciesByAlias != null && entityAlias != null) {
			EntityAccessPolicy entityAccessPolicy = currentAccesPolicy.accessPoliciesByAlias.get(entityAlias);
			
			if (entityAccessPolicy != null) {
				return entityAccessPolicy.getName().getName();
			}
		}
		
		return null;
	}
}
