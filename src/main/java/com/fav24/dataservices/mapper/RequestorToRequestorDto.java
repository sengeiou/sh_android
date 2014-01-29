package com.fav24.dataservices.mapper;

import com.fav24.dataservices.domain.Requestor;
import com.fav24.dataservices.dto.RequestorDto;


/**
 * 
 * Clase encargada del mapeo entre el objeto de dominio Requestor y el objeto de transferencia RequestorDto.
 * 
 * @author Fav24
 *
 */
public class RequestorToRequestorDto extends Mapper<Requestor, RequestorDto> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected RequestorDto map(Requestor origin) {
		
		RequestorDto requestor = new RequestorDto(origin.getIdDevice(), origin.getIdUser(), origin.getIdPlatform(), origin.getAppVersion(), origin.getTime());
		
		return requestor;
	}
}
