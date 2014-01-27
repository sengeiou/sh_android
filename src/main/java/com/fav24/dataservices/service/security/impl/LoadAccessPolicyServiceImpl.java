package com.fav24.dataservices.service.security.impl;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.security.AccessPolicy;
import com.fav24.dataservices.security.AccessPolicyFile;
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
	public AccessPolicyFile loadAccessPolicy(AccessPolicyFile accessPolicyFile) throws ServerException {

		AccessPolicy.mergeCurrentAccesPolicy(new AccessPolicyDOM(accessPolicyFile.getURL()));

		accessPolicyFile.getRequestor().setTime(System.currentTimeMillis());

		return accessPolicyFile;
	}
}
