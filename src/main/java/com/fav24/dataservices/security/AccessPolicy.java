package com.fav24.dataservices.security;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fav24.dataservices.domain.Requestor;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.listener.ContextRefreshedListener;
import com.fav24.dataservices.service.security.AccessPolicyService;
import com.fav24.dataservices.xml.AccessPolicyDOM;


/**
 * Clase que contiene la estructura genérica de políticas de acceso sobre entidades.
 * 
 * @author Fav24
 */
public class AccessPolicy {

	public static final String APPLICATION_POLICY_FILES_SUFIX = ".policy.xml";

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

		EntityAccessPolicy entityAccessPolicy = null;

		if (accessPoliciesByAlias != null && alias != null) {

			entityAccessPolicy = accessPoliciesByAlias.get(alias);
		}

		return entityAccessPolicy;
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
	 * Elimina todas las políticas de acceso disponibles hasta el momento.
	 */
	public static final void resetAccessPolicies() {

		synchronized(AccessPolicy.class) {

			currentAccesPolicy = null;
		}
	}

	/**
	 * Carga las políticas de acceso contenidas en el directorio base de la aplicación
	 * definido en el parámetro: "dataservices.home".
	 * 
	 * @throws ServerException 
	 */
	public static final void loadDefaultAccessPolicies() throws ServerException {

		String applicationHome = ContextRefreshedListener.getApplicationHome();

		// Se cargan los archivos de políticas de seguridad existentes.
		File applicationHomeDir = new File(applicationHome);

		if (applicationHomeDir.exists() && applicationHomeDir.isDirectory()) {

			FilenameFilter fileNameFilter = new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {

					if (name.endsWith(APPLICATION_POLICY_FILES_SUFIX)) {
						return true;
					}

					return false;
				}
			};

			File[] policyFiles = applicationHomeDir.listFiles(fileNameFilter);

			if (policyFiles == null || policyFiles.length == 0) {

				throw new ServerException(AccessPolicyService.ERROR_NO_DEFAULT_POLICY_FILES_TO_LOAD, 
						AccessPolicyService.ERROR_NO_DEFAULT_POLICY_FILES_TO_LOAD_MESSAGE);
			}
			else {

				for(File policyFile : policyFiles) {

					try {

						mergeCurrentAccesPolicy(new AccessPolicyDOM(policyFile.toURI().toURL()));
					} 
					catch (MalformedURLException e) {
						throw new ServerException(AccessPolicyService.ERROR_INVALID_POLICY_FILE_URL, 
								String.format(AccessPolicyService.ERROR_INVALID_POLICY_FILE_URL_MESSAGE, policyFile.toURI().toString()));
					}
				}
			}
		}
		else {

			throw new ServerException(ContextRefreshedListener.ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_NOT_DEFINED, 
					ContextRefreshedListener.ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_NOT_DEFINED_MESSAGE);
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

			if (entityAccessPolicy != null) {

				if (entityAccessPolicy.getData() != null) {
					EntityDataAttribute dataAttribute = entityAccessPolicy.getData().getAttribute(attributeAlias);

					if (dataAttribute != null) {
						return dataAttribute.getName();
					}
				}

				if (entityAccessPolicy.getKeys() != null) {
					EntityAttribute keyAttribute = entityAccessPolicy.getKeys().getFirstKeyAttributeByAlias(attributeAlias);

					if (keyAttribute != null) {
						return keyAttribute.getName();
					}
				}

				if (entityAccessPolicy.getFilters() != null) {
					EntityAttribute filterAttribute = entityAccessPolicy.getFilters().getFirstFilterAttributeByAlias(attributeAlias);

					if (filterAttribute != null) {
						return filterAttribute.getName();
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

	/**
	 * Comprueba que la lista de atributos indicada, esta disponible para la entidad.
	 * 
	 * @param entityAlias Entidad a la que pertenecen los atributos.
	 * @param attributesAliases Lista de atributos a comprobar.
	 * 
	 * @throws ServerException En caso de que alguno de los atributos no esté accesible.
	 */
	public static void checkAttributesAccesibility(String entityAlias, AbstractList<String> attributesAliases) throws ServerException {

		StringBuilder notAllowedAttibutes = null;

		EntityAccessPolicy entityAccessPolicy = currentAccesPolicy.accessPoliciesByAlias.get(entityAlias);

		for (String attributeAlias : attributesAliases) {

			if (entityAccessPolicy.getData().getAttribute(attributeAlias) == null) {

				if (notAllowedAttibutes == null) {
					notAllowedAttibutes = new StringBuilder(attributeAlias);
				}
				else{
					notAllowedAttibutes.append(',').append(attributeAlias);
				}
			}
		}

		if (notAllowedAttibutes != null) {
			throw new ServerException(AccessPolicyService.ERROR_ENTITY_ATTRIBUTES_NOT_ALLOWED, 
					String.format(AccessPolicyService.ERROR_ENTITY_ATTRIBUTES_NOT_ALLOWED_MESSAGE, entityAlias, notAllowedAttibutes));
		}
	}
	
	/**
	 * Retorna una lista con los alias de las entidades disponibles.
	 *  
	 * @return una lista con los alias de las entidades disponibles.
	 */
	public AbstractList<String> getEnititiesAliases() {
		
		AbstractList<String> enititiesAliases = new ArrayList<String>(accessPolicies.size());

		for (EntityAccessPolicy accessPolicy : accessPolicies) {
			
			enititiesAliases.add(accessPolicy.getName().getAlias());
		}
		
		return enititiesAliases;
	}
}
