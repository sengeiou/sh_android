package com.fav24.dataservices.services.impl;

import java.util.AbstractList;
import java.util.ArrayList;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.domain.AccessPolicy;
import com.fav24.dataservices.domain.EntityAccessPolicy;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.services.AccessPolicyService;
import com.fav24.dataservices.to.AccessPolicyResultTO;
import com.fav24.dataservices.to.AccessPolicyTO;


/**
 * Implementación del servicio de consulta de las políticas de acceso. 
 * 
 * @author Fav24
 */
@Component
@Scope("prototype")
public class AccessPolicyServiceImpl implements AccessPolicyService {

	/**
	 * {@inheritDoc}
	 */
	public AccessPolicyResultTO getAccessPolicy(AccessPolicyTO accessPolicy) {

		AccessPolicyResultTO resultTO = new AccessPolicyResultTO();

		resultTO.setRequester(accessPolicy.getRequester());
		try {
			resultTO.setAccessPolicy(getAccessPolicy(accessPolicy.getAccessPolicy()));
		} catch (ServerException e) {
			resultTO.setAccessPolicy(accessPolicy.getAccessPolicy());
			resultTO.setErrorCode(e.getErrorCode());
			resultTO.setMessage(e.getMessage());
		}

		return resultTO;
	}
	
	/**
	 * Completa la estrutura de políticas de acceso.
	 * 
	 * @param accessPolicy Estructura de políticas de acceso a completar.
	 * 
	 * @return estructura de políticas de acceso completa.
	 */
	private AccessPolicy getAccessPolicy(AccessPolicy accessPolicy) throws ServerException {
		
		for (EntityAccessPolicy entityAccessPolicy : accessPolicy.getAccessPolicies()) {
			entityAccessPolicy = getEntityAccessPolicy(entityAccessPolicy);
		}
		
		return accessPolicy;
	}
	
	/**
	 * Completa la estrutura de políticas de acceso de una determinada entidad.
	 * 
	 * @param entityAccessPolicy Estructura de políticas de acceso a completar.
	 * 
	 * @return estructura de políticas de acceso completa de la entidad indicada.
	 */
	private EntityAccessPolicy getEntityAccessPolicy(EntityAccessPolicy entityAccessPolicy) throws ServerException {
		
		return entityAccessPolicy;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public AbstractList<String> getPublicEntities() {
		return new ArrayList<String>();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public EntityAccessPolicy getPublicEntityPolicies(String entity) {
		EntityAccessPolicy entityAccessPolicy = new EntityAccessPolicy();
		
		entityAccessPolicy.setEntity(entity);
		
		return entityAccessPolicy;
	}
}
