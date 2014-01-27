package com.fav24.dataservices.mapper;

import com.fav24.dataservices.dto.security.AccessPolicyDto;
import com.fav24.dataservices.security.AccessPolicy;


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
	protected AccessPolicyDto map(AccessPolicy origin) {
		return null;
	}
}
