package com.fav24.dataservices.service.security.impl;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.security.AccessPolicy;
import com.fav24.dataservices.security.EntityAccessPolicy;
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
	public AccessPolicy getCurrentAccessPolicy(AccessPolicy accessPolicy) throws ServerException {

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

		accessPolicy.getRequestor().setTime(System.currentTimeMillis());

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
	public EntityAccessPolicy getPublicEntityPolicy(String entity) {

		return AccessPolicy.getCurrentAccesPolicy() != null ? AccessPolicy.getCurrentAccesPolicy().getEntityPolicy(entity) : null;
	}
}
