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

	public static final String ERROR_LOADING_POLICY_FILES = "PS000";


	/**
	 * {@inheritDocs}
	 */
	public AccessPolicyFiles loadAccessPolicy(AccessPolicyFiles accessPolicyFiles) throws ServerException {

		if (accessPolicyFiles.getURLs() == null) {

			ServerException exception = new ServerException(ERROR_LOADING_POLICY_FILES, "No se indicó ninguna URL de ningún fichero de definición de políticas de acceso.");

			logger.error(exception.getMessage());

			throw exception;
		}

		for(URL url : accessPolicyFiles.getURLs()) {
			AccessPolicy.mergeCurrentAccesPolicy(new AccessPolicyDOM(url));
		}

		accessPolicyFiles.getRequestor().setTime(System.currentTimeMillis());

		return accessPolicyFiles;
	}
}
