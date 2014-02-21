package com.fav24.dataservices.service.security.impl;

import java.io.InputStream;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.security.AccessPolicy;
import com.fav24.dataservices.security.AccessPolicyFiles;
import com.fav24.dataservices.service.impl.GenericServiceJDBC;
import com.fav24.dataservices.service.security.LoadAccessPolicyService;
import com.fav24.dataservices.xml.AccessPolicyDOM;


/**
 * Implementación del servicio de carga de las políticas de acceso. 
 * 
 * @author Fav24
 */
@Component
@Scope("prototype")
public class LoadAccessPolicyServiceImpl implements LoadAccessPolicyService {

	@Autowired
	protected GenericServiceJDBC genericServiceJDBC;


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resetAccessPolicies() {
		AccessPolicy.resetAccessPolicies();
		
		genericServiceJDBC.resetAccessPoliciesInformationAgainstDataSource();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void loadDefaultAccessPolicy() throws ServerException {

		AccessPolicy.resetAccessPolicies();
		AccessPolicy.loadDefaultAccessPolicies();

		genericServiceJDBC.checkAndGatherAccessPoliciesInformationAgainstDataSource(AccessPolicy.getCurrentAccesPolicy());
	}

	/**
	 * {@inheritDocs}
	 */
	@Override
	public AccessPolicyFiles loadAccessPolicy(AccessPolicyFiles accessPolicyFiles) throws ServerException {

		if (accessPolicyFiles == null || accessPolicyFiles.getURLs() == null || accessPolicyFiles.getURLs().length == 0) {

			AccessPolicy.resetAccessPolicies();
			AccessPolicy.loadDefaultAccessPolicies();
		}
		else {

			for(URL url : accessPolicyFiles.getURLs()) {

				AccessPolicy.mergeCurrentAccesPolicy(new AccessPolicyDOM(url));
			}
		}

		genericServiceJDBC.checkAndGatherAccessPoliciesInformationAgainstDataSource(AccessPolicy.getCurrentAccesPolicy());

		return accessPolicyFiles;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AccessPolicy loadAccessPolicy(InputStream accessPolicyStream) throws ServerException {

		AccessPolicy accessPolicy = new AccessPolicyDOM(accessPolicyStream);

		AccessPolicy.mergeCurrentAccesPolicy(accessPolicy);

		genericServiceJDBC.checkAndGatherAccessPoliciesInformationAgainstDataSource(AccessPolicy.getCurrentAccesPolicy());

		return accessPolicy;
	}
}
