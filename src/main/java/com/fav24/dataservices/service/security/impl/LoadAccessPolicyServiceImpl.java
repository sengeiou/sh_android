package com.fav24.dataservices.service.security.impl;

import java.net.URL;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.dto.security.AccessPolicyFileDto;
import com.fav24.dataservices.dto.security.AccessPolicyFileResultDto;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.security.AccessPolicy;
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
	public AccessPolicyFileResultDto loadAccessPolicy(AccessPolicyFileDto accessPolicyFile) {
		
		AccessPolicyFileResultDto result = null;
		
		try {
			URL accessPolicyFileURL = accessPolicyFile.getURL();
			
			AccessPolicy.mergeCurrentAccesPolicy(new AccessPolicyDOM(accessPolicyFileURL));
			
			result = new AccessPolicyFileResultDto();
			result.setRequestor(accessPolicyFile.getRequestor());
			result.setURL(accessPolicyFileURL);
			
		} catch (ServerException e) {
			e.printStackTrace();
			
			result = new AccessPolicyFileResultDto(e);
		}
		
		return result;
	}
}
