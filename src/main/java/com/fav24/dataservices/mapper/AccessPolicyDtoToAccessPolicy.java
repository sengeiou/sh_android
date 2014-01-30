package com.fav24.dataservices.mapper;

import java.util.TreeSet;

import com.fav24.dataservices.domain.Requestor;
import com.fav24.dataservices.dto.security.AccessPolicyDto;
import com.fav24.dataservices.dto.security.EntityAccessPolicyDtoElement;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.security.AccessPolicy;
import com.fav24.dataservices.security.EntityAccessPolicy;


/**
 * 
 * Clase encargada del mapeo entre el objeto de transferencia AccessPolicyDto y el objeto de dominio AccessPolicy.
 * 
 * @author Fav24
 *
 */
public class AccessPolicyDtoToAccessPolicy extends Mapper<AccessPolicyDto, AccessPolicy> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AccessPolicy map(AccessPolicyDto origin) throws ServerException {

		AccessPolicy accessPolicy = new AccessPolicy(new TreeSet<EntityAccessPolicy>());

		accessPolicy.setRequestor((Requestor)Mapper.Map(origin.getRequestor()));
		
		for (EntityAccessPolicyDtoElement entityAccesPolicy : origin.getAccessPolicies()) {
			
			accessPolicy.getAccessPolicies().add((EntityAccessPolicy)Mapper.Map(entityAccesPolicy));
		}
		
		return accessPolicy;
	}
}
