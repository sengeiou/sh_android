package com.fav24.dataservices.security;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;


/**
 * Clase que contiene la estructura genérica de políticas de acceso sobre entidades.
 * 
 * @author Fav24
 */
public class AccessPolicy {

	private static AccessPolicy currentAccesPolicy;

	private URL accessPolicyURL;
	private Set<EntityAccessPolicy> accessPolicies;


	/**
	 * Constructor por defecto.
	 */
	public AccessPolicy() {
		this(null, null);
	}

	/**
	 * Constructor por defecto.
	 * 
	 * @param accessPolicyURL URL de la que se obtuvieron las políticas de acceso.
	 */
	public AccessPolicy(URL accessPolicyURL) {
		this(accessPolicyURL, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param accessPolicyURL URL de la que se obtuvieron las políticas de acceso.
	 * @param accessPolicies Lista de políticas solicitadas.
	 */
	public AccessPolicy(URL accessPolicyURL, Set<EntityAccessPolicy> accessPolicies) {
		this.accessPolicyURL = accessPolicyURL;
		this.accessPolicies = accessPolicies;
	}

	/**
	 * Constructor de copia.
	 * 
	 * @param accesPolicy Objeto referencia a copiar.
	 */
	public AccessPolicy(AccessPolicy accesPolicy) {
		accessPolicyURL = accesPolicy.accessPolicyURL;

		if (accesPolicy.accessPolicies != null) {

			accessPolicies = new HashSet<EntityAccessPolicy>();
			for (EntityAccessPolicy entityAccessPolicy : accesPolicy.accessPolicies) {
				accessPolicies.add(new EntityAccessPolicy(entityAccessPolicy));
			}
		}
		else {
			accessPolicies = null;
		}
	}
	
	/**
	 * Retorna la URL del fichero que contiene estas políticas de acceso.
	 * 
	 * @return la URL del fichero que contiene estas políticas de acceso.
	 */
	public URL getURL() {
		return accessPolicyURL;
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
	}

	/**
	 * Retorna las políticas de la entidad con el alias indicado.
	 * 
	 * @param alias Alias de la entidad de la que se desea obtener sus políticas.
	 * 
	 * @return las políticas de la entidad con el alias indicado.
	 */
	public EntityAccessPolicy getEntityPolicy(String alias) {

		if (accessPolicies != null && alias != null) {

			for (EntityAccessPolicy entityAccessPolicy : accessPolicies) {

				if (alias.equals(entityAccessPolicy.getName().getAlias())) {
					return entityAccessPolicy;
				}
			}
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
			
			if (this.accessPolicies == null) {
				
				this.accessPolicies = new HashSet<EntityAccessPolicy>();
				this.accessPolicies.addAll(accessPolicies);
			}
			else {
				for (EntityAccessPolicy entityAccessPolicy : accessPolicies) {
					if (!this.accessPolicies.add(entityAccessPolicy)) {
						
						this.accessPolicies.remove(entityAccessPolicy);
						this.accessPolicies.add(entityAccessPolicy);
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
}
