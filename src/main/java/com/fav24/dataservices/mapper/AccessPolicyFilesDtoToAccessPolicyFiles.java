package com.fav24.dataservices.mapper;

import com.fav24.dataservices.dto.security.AccessPolicyFilesDto;
import com.fav24.dataservices.security.AccessPolicyFiles;


/**
 * 
 * Clase encargada del mapeo entre el objeto de transferencia AccessPolicyFileDto y el objeto de dominio AccessPolicyFile.
 * 
 * @author Fav24
 *
 */
public class AccessPolicyFilesDtoToAccessPolicyFiles extends Mapper<AccessPolicyFilesDto, AccessPolicyFiles> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AccessPolicyFiles map(AccessPolicyFilesDto origin) {
		return null;
	}
}
