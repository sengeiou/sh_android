package com.fav24.dataservices.service.security.impl;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.DataServicesContext;
import com.fav24.dataservices.domain.security.AccessPolicy;
import com.fav24.dataservices.domain.security.EntityAccessPolicy;
import com.fav24.dataservices.domain.security.RemoteFiles;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.generic.impl.GenericServiceDataSourceInfo;
import com.fav24.dataservices.service.generic.impl.GenericServiceDataSourceInfo.EntityDataSourceInfo;
import com.fav24.dataservices.service.security.AccessPolicyConfigurationService;
import com.fav24.dataservices.util.FileUtils;
import com.fav24.dataservices.xml.security.AccessPolicyDOM;


/**
 * Implementación del servicio de carga y consulta de las políticas de acceso. 
 */
@Scope("singleton")
@Component
public class AccessPolicyConfigurationServiceImpl implements AccessPolicyConfigurationService {

	@Autowired
	private GenericServiceDataSourceInfo entitiesDataSourceInfo;


	/**
	 * Constructor por defecto.
	 */
	public AccessPolicyConfigurationServiceImpl() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void dropAccessPolicies() throws ServerException {

		AccessPolicy.resetAccessPolicies();
		entitiesDataSourceInfo.resetAccessPoliciesInformationAgainstDataSource();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void loadDefaultAccessPolicy() throws ServerException {

		String applicationHome = DataServicesContext.getCurrentDataServicesContext().getApplicationHome();

		// Se cargan los archivos de políticas de seguridad existentes.
		File applicationHomeDir = new File(applicationHome);

		if (applicationHomeDir.exists() && applicationHomeDir.isDirectory()) {

			AbstractList<File> policyFiles = FileUtils.getFilesWithSuffix(applicationHome + "/" + POLICY_FILES_RELATIVE_LOCATION, POLICY_FILES_SUFFIX, null);

			if (policyFiles.size() == 0) {

				throw new ServerException(ERROR_NO_DEFAULT_POLICY_FILES_TO_LOAD, ERROR_NO_DEFAULT_POLICY_FILES_TO_LOAD_MESSAGE);
			}
			else {

				AbstractList<AccessPolicy> loadedPolicies = new ArrayList<AccessPolicy>();
				
				// Carga de los ficheros de políticas.
				for(File policyFile : policyFiles) {

					try {

						AccessPolicy loadedPolicy = new AccessPolicyDOM(policyFile.toURI().toURL());
						entitiesDataSourceInfo.checkAndGatherAccessPoliciesInformationAgainstDataSource(loadedPolicy);
						
						loadedPolicies.add(loadedPolicy);
					} 
					catch (MalformedURLException e) {
						
						throw new ServerException(ERROR_INVALID_POLICY_FILE_URL, String.format(ERROR_INVALID_POLICY_FILE_URL_MESSAGE, policyFile.toURI().toString()));
					}
				}
				
				// Aplicación de las políticas cargadas.
				AccessPolicy.resetAccessPolicies();
				
				for (AccessPolicy loadedPolicy : loadedPolicies) {
					AccessPolicy.mergeCurrentAccesPolicy(loadedPolicy);
				}
				
				// Recarga de la información de la fuente de datos.
				Map<String, EntityDataSourceInfo> entitiesInformation = entitiesDataSourceInfo.checkAndGatherAccessPoliciesInformationAgainstDataSource(AccessPolicy.getCurrentAccesPolicy());
				entitiesDataSourceInfo.setAccessPoliciesInformationAgainstDataSource(entitiesInformation);
			}
		}
		else {

			throw new ServerException(DataServicesContext.ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_NOT_DEFINED, 
					DataServicesContext.ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_NOT_DEFINED_MESSAGE);
		}
	}

	/**
	 * {@inheritDocs}
	 */
	@Override
	public synchronized RemoteFiles loadAccessPolicy(RemoteFiles accessPolicyFiles) throws ServerException {

		if (accessPolicyFiles == null || accessPolicyFiles.getURLs() == null || accessPolicyFiles.getURLs().length == 0) {

			loadDefaultAccessPolicy();
		}
		else {
			AbstractList<AccessPolicy> loadedPolicies = new ArrayList<AccessPolicy>();
			
			for(URL url : accessPolicyFiles.getURLs()) {

				AccessPolicy accessPolicy = new AccessPolicyDOM(url);

				entitiesDataSourceInfo.checkAndGatherAccessPoliciesInformationAgainstDataSource(accessPolicy);

				loadedPolicies.add(accessPolicy);
			}
			
			// Aplicación de las nuevas políticas cargadas.
			for (AccessPolicy loadedPolicy : loadedPolicies) {
				AccessPolicy.mergeCurrentAccesPolicy(loadedPolicy);
			}
			
			Map<String, EntityDataSourceInfo> entitiesInformation = entitiesDataSourceInfo.checkAndGatherAccessPoliciesInformationAgainstDataSource(AccessPolicy.getCurrentAccesPolicy());
			entitiesDataSourceInfo.setAccessPoliciesInformationAgainstDataSource(entitiesInformation);
		}

		return accessPolicyFiles;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized AccessPolicy loadAccessPolicy(InputStream accessPolicyStream) throws ServerException {

		// Carga de la nueva política.
		AccessPolicy accessPolicy = new AccessPolicyDOM(accessPolicyStream);

		entitiesDataSourceInfo.checkAndGatherAccessPoliciesInformationAgainstDataSource(accessPolicy);

		// Aplicación de la política cargada.
		AccessPolicy.mergeCurrentAccesPolicy(accessPolicy);
		
		Map<String, EntityDataSourceInfo> entitiesInformation = entitiesDataSourceInfo.checkAndGatherAccessPoliciesInformationAgainstDataSource(AccessPolicy.getCurrentAccesPolicy());
		entitiesDataSourceInfo.setAccessPoliciesInformationAgainstDataSource(entitiesInformation);
		
		return accessPolicy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized AccessPolicy getCurrentAccessPolicy(AccessPolicy accessPolicy) throws ServerException {

		if (AccessPolicy.getCurrentAccesPolicy() == null || 
				AccessPolicy.getCurrentAccesPolicy().getAccessPolicies() == null || 
				AccessPolicy.getCurrentAccesPolicy().getAccessPolicies().isEmpty()) {

			throw new ServerException(ERROR_NO_CURRENT_POLICY_DEFINED, ERROR_NO_CURRENT_POLICY_DEFINED_MESSAGE);
		}

		if (accessPolicy.getAccessPolicies() == null || accessPolicy.getAccessPolicies().size() == 0) {
			accessPolicy.setAccessPolicies(AccessPolicy.getCurrentAccesPolicy().getAccessPolicies());
		}
		else {
			Iterator<EntityAccessPolicy> entityAccessPolicies = accessPolicy.getAccessPolicies().iterator();
			while (entityAccessPolicies.hasNext()) {

				EntityAccessPolicy entityAccessPolicy = entityAccessPolicies.next();
				EntityAccessPolicy currentEntityAccessPolicy = AccessPolicy.getEntityPolicy(entityAccessPolicy.getName().getAlias());

				if (currentEntityAccessPolicy != null) {
					entityAccessPolicy.getName().setName(currentEntityAccessPolicy.getName().getName());
					entityAccessPolicy.setAllowedOperations(currentEntityAccessPolicy.getAllowedOperations());
					entityAccessPolicy.setOnlyByKey(currentEntityAccessPolicy.getOnlyByKey());
					entityAccessPolicy.setOnlySpecifiedFilters(currentEntityAccessPolicy.getOnlySpecifiedFilters());
					entityAccessPolicy.setMaxPageSize(currentEntityAccessPolicy.getMaxPageSize());
					entityAccessPolicy.setData(currentEntityAccessPolicy.getData());
					entityAccessPolicy.setKeys(currentEntityAccessPolicy.getKeys());
					entityAccessPolicy.setFilters(currentEntityAccessPolicy.getFilters());
				}
				else {
					entityAccessPolicies.remove();
				}
			}
		}

		return accessPolicy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized AbstractList<String> getPublicEntities() {

		return AccessPolicy.getCurrentAccesPolicy() != null ? AccessPolicy.getCurrentAccesPolicy().getEnititiesAliases() : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized EntityAccessPolicy getPublicEntityPolicy(String entity) {

		return AccessPolicy.getCurrentAccesPolicy() != null ? AccessPolicy.getEntityPolicy(entity) : null;
	}
}
