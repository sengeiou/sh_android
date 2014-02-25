package com.fav24.dataservices.service.security.impl;

import java.io.InputStream;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.domain.security.AccessPolicy;
import com.fav24.dataservices.domain.security.AccessPolicyFiles;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.GenericService;
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
	@Qualifier(value="GenericServiceJDBC")
	protected GenericService genericService;


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resetAccessPolicies() throws ServerException {
		AccessPolicy.resetAccessPolicies();

		applyCurrentAccessPolicy();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void loadDefaultAccessPolicy() throws ServerException {

		AccessPolicy.resetAccessPolicies();
		AccessPolicy.loadDefaultAccessPolicies();

		applyCurrentAccessPolicy();
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

		applyCurrentAccessPolicy();

		return accessPolicyFiles;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AccessPolicy loadAccessPolicy(InputStream accessPolicyStream) throws ServerException {

		AccessPolicy accessPolicy = new AccessPolicyDOM(accessPolicyStream);

		AccessPolicy.mergeCurrentAccesPolicy(accessPolicy);

		applyCurrentAccessPolicy();

		return accessPolicy;
	}

	/**
	 * Comprueba y aplica la configuración actual de políticas de acceso. 
	 * 
	 * @throws ServerException
	 */
	private void applyCurrentAccessPolicy() throws ServerException {

		if (AccessPolicy.getCurrentAccesPolicy() != null) {

			genericService.checkAndGatherAccessPoliciesInformationAgainstDataSource(AccessPolicy.getCurrentAccesPolicy());		
		}
		else {

			genericService.resetAccessPoliciesInformationAgainstDataSource();
		}
	}
}
