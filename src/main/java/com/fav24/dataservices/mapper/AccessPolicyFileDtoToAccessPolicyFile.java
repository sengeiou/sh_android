package com.fav24.dataservices.mapper;

import com.fav24.dataservices.dto.security.AccessPolicyFileDto;
import com.fav24.dataservices.security.AccessPolicyFile;


/**
 * 
 * Clase encargada del mapeo entre el objeto de transferencia AccessPolicyFileDto y el objeto de dominio AccessPolicyFile.
 * 
 * @author Fav24
 *
 */
public class AccessPolicyFileDtoToAccessPolicyFile extends Mapper<AccessPolicyFileDto, AccessPolicyFile> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AccessPolicyFile map(AccessPolicyFileDto origin) {
		return null;
	}
}
