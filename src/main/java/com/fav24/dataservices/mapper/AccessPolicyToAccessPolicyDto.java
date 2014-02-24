package com.fav24.dataservices.mapper;

import com.fav24.dataservices.domain.security.AccessPolicy;
import com.fav24.dataservices.domain.security.EntityAccessPolicy;
import com.fav24.dataservices.dto.RequestorDto;
import com.fav24.dataservices.dto.security.AccessPolicyDto;
import com.fav24.dataservices.dto.security.EntityAccessPolicyDto;
import com.fav24.dataservices.exception.ServerException;


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

		accessPolicy.setRequestor((RequestorDto)Mapper.Map(origin.getRequestor()));

		EntityAccessPolicyDto[] policies = new EntityAccessPolicyDto[origin.getAccessPolicies().size()];
		int i=0;
		for (EntityAccessPolicy entityAccesPolicy : origin.getAccessPolicies()) {
			
			policies[i++] = (EntityAccessPolicyDto)Mapper.Map(entityAccesPolicy);
		}
		
		accessPolicy.setAccessPolicies(policies);
		
		return accessPolicy;
	}
}
