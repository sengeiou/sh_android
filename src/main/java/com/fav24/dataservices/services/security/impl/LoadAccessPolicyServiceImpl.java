package com.fav24.dataservices.services.security.impl;

import java.net.URL;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.security.AccessPolicy;
import com.fav24.dataservices.services.security.LoadAccessPolicyService;
import com.fav24.dataservices.to.security.AccessPolicyFileResultTO;
import com.fav24.dataservices.to.security.AccessPolicyFileTO;
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
	public AccessPolicyFileResultTO loadAccessPolicy(AccessPolicyFileTO accessPolicyFile) throws ServerException {
		
		AccessPolicyFileResultTO resultTO = new AccessPolicyFileResultTO();
		
		resultTO.setRequestor(accessPolicyFile.getRequestor());
		
		URL accessPolicyURL = accessPolicyFile.getURL();

		AccessPolicy.mergeCurrentAccesPolicy(new AccessPolicyDOM(accessPolicyURL));
		
		return resultTO;
	}
}
