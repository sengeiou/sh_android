package com.fav24.dataservices.mapper;

import com.fav24.dataservices.domain.Requestor;
import com.fav24.dataservices.domain.security.AccessPolicyFiles;
import com.fav24.dataservices.dto.security.AccessPolicyFilesDto;
import com.fav24.dataservices.exception.ServerException;


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
	protected AccessPolicyFiles map(AccessPolicyFilesDto origin) throws ServerException {

		AccessPolicyFiles accessPolicyFiles = new AccessPolicyFiles(origin.getPolicyFilesURLs());

		accessPolicyFiles.setRequestor((Requestor)Mapper.Map(origin.getRequestor()));

		return accessPolicyFiles;
	}
}
