package com.fav24.dataservices.services.impl;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.security.AccessPolicy;
import com.fav24.dataservices.security.EntityAccessPolicy;
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
	public AccessPolicyResultTO getCurrentAccessPolicy(AccessPolicyTO accessPolicy) {

		AccessPolicyResultTO resultTO = new AccessPolicyResultTO();

		resultTO.setRequestor(accessPolicy.getRequestor());
		try {
			resultTO.setAccessPolicy(getCurrentAccessPolicy(accessPolicy.getAccessPolicy()));
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
	 * @param accessPolicy Estructura de políticas de acceso solicitadas.
	 * 
	 * @return estructura de políticas de acceso completa.
	 */
	private AccessPolicy getCurrentAccessPolicy(AccessPolicy accessPolicy) throws ServerException {

		if (accessPolicy.getAccessPolicies() == null || accessPolicy.getAccessPolicies().size() == 0) {
			accessPolicy.setAccessPolicies(AccessPolicy.getCurrentAccesPolicy().getAccessPolicies());
		}
		else {
			Iterator<EntityAccessPolicy> entityAccessPolicies = accessPolicy.getAccessPolicies().iterator();
			while (entityAccessPolicies.hasNext()) {

				EntityAccessPolicy entityAccessPolicy = entityAccessPolicies.next();
				EntityAccessPolicy currentEntityAccessPolicy = AccessPolicy.getCurrentAccesPolicy().getEntityPolicy(entityAccessPolicy.getName().getAlias());

				if (currentEntityAccessPolicy != null) {
					entityAccessPolicy.getName().setName(currentEntityAccessPolicy.getName().getName());
					entityAccessPolicy.setAllowedOperations(currentEntityAccessPolicy.getAllowedOperations());
					entityAccessPolicy.setOnlyByKey(currentEntityAccessPolicy.setOnlyByKey());
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
	public AbstractList<String> getPublicEntities() {

		AbstractList<String> publicEntities = new ArrayList<String>();

		if (AccessPolicy.getCurrentAccesPolicy() != null) {
			for (EntityAccessPolicy entityAccessPolicy : AccessPolicy.getCurrentAccesPolicy().getAccessPolicies()) {

				publicEntities.add(entityAccessPolicy.getName().getAlias());
			}
		}

		return publicEntities;
	}

	/**
	 * {@inheritDoc}
	 */
	public EntityAccessPolicy getPublicEntityPolicies(String entity) {
		
		return AccessPolicy.getCurrentAccesPolicy() != null ? AccessPolicy.getCurrentAccesPolicy().getEntityPolicy(entity) : null;
	}

	/**
	 * {@inheritDocs}
	 */
	public AccessPolicyResultTO loadAccessPolicy(AccessPolicyTO accessPolicy) {
		
		AccessPolicyResultTO resultTO = new AccessPolicyResultTO();
		
		resultTO.setRequestor(accessPolicy.getRequestor());
		resultTO.setAccessPolicy(accessPolicy.getAccessPolicy());
		
		Set<EntityAccessPolicy> accessPolicies = accessPolicy.getAccessPolicy().getAccessPolicies();
		
		if (accessPolicies == null || accessPolicies.size() == 0) {
			resultTO.	
		}
		else {
			resultTO.set
		}

	}
}
