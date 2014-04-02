package com.fav24.dataservices.service.security.impl;

import java.io.InputStream;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.domain.security.AccessPolicy;
import com.fav24.dataservices.domain.security.AccessPolicyFiles;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.generic.GenericService;
import com.fav24.dataservices.service.security.LoadAccessPolicyService;
import com.fav24.dataservices.xml.security.AccessPolicyDOM;


/**
 * Implementación del servicio de carga de las políticas de acceso. 
 * 
 * @author Fav24
 */
@Component
@Scope("prototype")
public class LoadAccessPolicyServiceImpl implements LoadAccessPolicyService {

	@Autowired
	protected GenericService genericService;


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dropAccessPolicies() throws ServerException {
		AccessPolicy.resetAccessPolicies();

		genericService.resetAccessPoliciesInformationAgainstDataSource();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void loadDefaultAccessPolicy() throws ServerException {

		AccessPolicy.resetAccessPolicies();

		try {

			AccessPolicy.loadDefaultAccessPolicies();
			genericService.checkAndGatherAccessPoliciesInformationAgainstDataSource(AccessPolicy.getCurrentAccesPolicy());
		}
		catch(ServerException e) {

			AccessPolicy.resetAccessPolicies();
			genericService.resetAccessPoliciesInformationAgainstDataSource();

			throw e;
		}
	}

	/**
	 * {@inheritDocs}
	 */
	@Override
	public AccessPolicyFiles loadAccessPolicy(AccessPolicyFiles accessPolicyFiles) throws ServerException {

		if (accessPolicyFiles == null || accessPolicyFiles.getURLs() == null || accessPolicyFiles.getURLs().length == 0) {

			loadDefaultAccessPolicy();
		}
		else {

			for(URL url : accessPolicyFiles.getURLs()) {

				AccessPolicy accessPolicy = new AccessPolicyDOM(url);
				
				genericService.checkAndGatherAccessPoliciesInformationAgainstDataSource(accessPolicy);
				
				AccessPolicy.mergeCurrentAccesPolicy(accessPolicy);
			}
		}

		return accessPolicyFiles;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AccessPolicy loadAccessPolicy(InputStream accessPolicyStream) throws ServerException {

		AccessPolicy accessPolicy = new AccessPolicyDOM(accessPolicyStream);

		genericService.checkAndGatherAccessPoliciesInformationAgainstDataSource(accessPolicy);

		AccessPolicy.mergeCurrentAccesPolicy(accessPolicy);

		return accessPolicy;
	}
}
