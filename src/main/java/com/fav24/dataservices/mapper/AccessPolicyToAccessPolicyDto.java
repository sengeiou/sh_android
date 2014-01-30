package com.fav24.dataservices.mapper;

import com.fav24.dataservices.dto.RequestorDtoElement;
import com.fav24.dataservices.dto.security.AccessPolicyDto;
import com.fav24.dataservices.dto.security.EntityAccessPolicyDtoElement;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.security.AccessPolicy;
import com.fav24.dataservices.security.EntityAccessPolicy;


/**
 * 
 * Clase encargada del mapeo entre el objeto de dominio AccessPolicy y el objeto de transferencia AccessPolicyDto.
 * 
 * @author Fav24
 *
 */
public class AccessPolicyToAccessPolicyDto extends Mapper<AccessPolicy, AccessPolicyDto> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AccessPolicyDto map(AccessPolicy origin) throws ServerException {

		AccessPolicyDto accessPolicy = new AccessPolicyDto();

		accessPolicy.setRequestor((RequestorDtoElement)Mapper.Map(origin.getRequestor()));

		EntityAccessPolicyDtoElement[] policies = new EntityAccessPolicyDtoElement[origin.getAccessPolicies().size()];
		int i=0;
		for (EntityAccessPolicy entityAccesPolicy : origin.getAccessPolicies()) {
			
			policies[i++] = (EntityAccessPolicyDtoElement)Mapper.Map(entityAccesPolicy);
		}
		
		accessPolicy.setAccessPolicies(policies);
		
		return accessPolicy;
	}
}
