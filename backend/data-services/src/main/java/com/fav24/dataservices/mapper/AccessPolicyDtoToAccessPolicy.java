package com.fav24.dataservices.mapper;

import java.util.TreeSet;

import com.fav24.dataservices.domain.Requestor;
import com.fav24.dataservices.domain.policy.AccessPolicy;
import com.fav24.dataservices.domain.policy.EntityAccessPolicy;
import com.fav24.dataservices.dto.policy.AccessPolicyDto;
import com.fav24.dataservices.dto.policy.EntityAccessPolicyDto;
import com.fav24.dataservices.exception.ServerException;


/**
 * Clase encargada del mapeo entre el objeto de transferencia AccessPolicyDto y el objeto de dominio AccessPolicy.
 */
public class AccessPolicyDtoToAccessPolicy extends Mapper<AccessPolicyDto, AccessPolicy> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AccessPolicy map(AccessPolicyDto origin) throws ServerException {

		AccessPolicy accessPolicy = new AccessPolicy(origin.getAlias(), (Requestor)Mapper.Map(origin.getRequestor()));

		if (origin.getAccessPolicies() != null) {
			
			TreeSet<EntityAccessPolicy> entityAccessPolicies = new TreeSet<EntityAccessPolicy>();
			
			for (EntityAccessPolicyDto entityAccesPolicy : origin.getAccessPolicies()) {

				entityAccessPolicies.add((EntityAccessPolicy)Mapper.Map(entityAccesPolicy));
			}
			
			accessPolicy.setAccessPolicies(entityAccessPolicies);
		}
		
		return accessPolicy;
	}
}
