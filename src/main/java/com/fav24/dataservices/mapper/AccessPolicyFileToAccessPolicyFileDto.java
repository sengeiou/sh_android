package com.fav24.dataservices.mapper;

import com.fav24.dataservices.dto.security.AccessPolicyFileDto;
import com.fav24.dataservices.security.AccessPolicyFile;


/**
 * 
 * Clase encargada del mapeo entre el objeto de dominio AccessPolicy y el objeto de transferencia AccessPolicyDto.
 * 
 * @author Fav24
 *
 */
public class AccessPolicyFileToAccessPolicyFileDto extends Mapper<AccessPolicyFile, AccessPolicyFileDto> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AccessPolicyFileDto map(AccessPolicyFile origin) {
		return null;
	}
}
