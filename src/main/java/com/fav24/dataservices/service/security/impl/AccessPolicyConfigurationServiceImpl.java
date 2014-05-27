package com.fav24.dataservices.service.security.impl;

import java.io.InputStream;
import java.net.URL;
import java.util.AbstractList;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.domain.security.AccessPolicy;
import com.fav24.dataservices.domain.security.EntityAccessPolicy;
import com.fav24.dataservices.domain.security.RemoteFiles;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.generic.impl.GenericServiceJDBCInformation;
import com.fav24.dataservices.service.security.AccessPolicyConfigurationService;
import com.fav24.dataservices.service.security.AccessPolicyService;
import com.fav24.dataservices.xml.security.AccessPolicyDOM;


/**
 * Implementación del servicio de carga y consulta de las políticas de acceso. 
 */
@Scope("singleton")
@Component
public class AccessPolicyConfigurationServiceImpl implements AccessPolicyConfigurationService {

	@Autowired
	private GenericServiceJDBCInformation entitiesInformation;


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
		entitiesInformation.resetAccessPoliciesInformationAgainstDataSource();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void loadDefaultAccessPolicy() throws ServerException {

		AccessPolicy.resetAccessPolicies();

		try {

			AccessPolicy.loadDefaultAccessPolicies();
			entitiesInformation.checkAndGatherAccessPoliciesInformationAgainstDataSource(AccessPolicy.getCurrentAccesPolicy());
		}
		catch(ServerException e) {

			AccessPolicy.resetAccessPolicies();
			entitiesInformation.resetAccessPoliciesInformationAgainstDataSource();

			throw e;
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

			for(URL url : accessPolicyFiles.getURLs()) {

				AccessPolicy accessPolicy = new AccessPolicyDOM(url);

				entitiesInformation.checkAndGatherAccessPoliciesInformationAgainstDataSource(accessPolicy);

				AccessPolicy.mergeCurrentAccesPolicy(accessPolicy);
			}
		}

		return accessPolicyFiles;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized AccessPolicy loadAccessPolicy(InputStream accessPolicyStream) throws ServerException {

		AccessPolicy accessPolicy = new AccessPolicyDOM(accessPolicyStream);

		entitiesInformation.checkAndGatherAccessPoliciesInformationAgainstDataSource(accessPolicy);

		AccessPolicy.mergeCurrentAccesPolicy(accessPolicy);

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

			throw new ServerException(AccessPolicyService.ERROR_NO_CURRENT_POLICY_DEFINED, AccessPolicyService.ERROR_NO_CURRENT_POLICY_DEFINED_MESSAGE);
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
