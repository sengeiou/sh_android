package com.fav24.dataservices.mapper;

import com.fav24.dataservices.domain.Requestor;
import com.fav24.dataservices.dto.security.AccessPolicyDto;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.security.AccessPolicy;


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
		
		AccessPolicy accessPolicy = new AccessPolicy();
		
		accessPolicy.setRequestor((Requestor)Mapper.Map(origin.getRequestor()));
		
		return accessPolicy;
	}
}
