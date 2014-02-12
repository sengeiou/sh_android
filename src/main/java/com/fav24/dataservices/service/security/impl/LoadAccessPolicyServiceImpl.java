package com.fav24.dataservices.service.security.impl;

import java.net.URL;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.security.AccessPolicy;
import com.fav24.dataservices.security.AccessPolicyFiles;
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


	/**
	 * {@inheritDocs}
	 */
	public AccessPolicyFiles loadAccessPolicy(AccessPolicyFiles accessPolicyFiles) throws ServerException {

		if (accessPolicyFiles.getURLs() == null || accessPolicyFiles.getURLs().length == 0) {

			AccessPolicy.loadDefaultAccessPolicies();
		}
		else {

			for(URL url : accessPolicyFiles.getURLs()) {

				AccessPolicy.mergeCurrentAccesPolicy(new AccessPolicyDOM(url));
			}
		}

		return accessPolicyFiles;
	}
}
