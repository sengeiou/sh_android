package com.fav24.dataservices.service.security.impl;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.domain.security.AccessPolicy;
import com.fav24.dataservices.domain.security.EntityAccessPolicy;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.security.AccessPolicyService;
import com.fav24.dataservices.service.security.RetrieveAccessPolicyService;


/**
 * Implementación del servicio de consulta de las políticas de acceso. 
 * 
 * @author Fav24
 */
@Component
@Scope("prototype")
public class RetrieveAccessPolicyServiceImpl implements RetrieveAccessPolicyService {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AccessPolicy getCurrentAccessPolicy(AccessPolicy accessPolicy) throws ServerException {

		if (AccessPolicy.getCurrentAccesPolicy() == null || 
				AccessPolicy.getCurrentAccesPolicy().getAccessPolicies() == null || 
				AccessPolicy.getCurrentAccesPolicy().getAccessPolicies().isEmpty()) {
			
			throw new ServerException(AccessPolicyService.ERROR_NO_CURRENT_POLICY_DEFINED, AccessPolicyService.ERROR_NO_CURRENT_POLICY_DEFINED_MESSAGE);
		}
		
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
					entityAccessPolicy.setOnlyByKey(currentEntityAccessPolicy.getOnlyByKey());
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
	@Override
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
	@Override
	public EntityAccessPolicy getPublicEntityPolicy(String entity) {

		return AccessPolicy.getCurrentAccesPolicy() != null ? AccessPolicy.getCurrentAccesPolicy().getEntityPolicy(entity) : null;
	}
}
