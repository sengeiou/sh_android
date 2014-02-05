package com.fav24.dataservices.mapper;

import com.fav24.dataservices.domain.Requestor;
import com.fav24.dataservices.dto.RequestorDto;


/**
 * 
 * Clase encargada del mapeo entre el objeto de transferencia RequestorDto y el objeto de dominio Requestor.
 * 
 * @author Fav24
 *
 */
public class RequestorDtoToRequestor extends Mapper<RequestorDto, Requestor> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Requestor map(RequestorDto origin) {
		
		Requestor requestor = new Requestor();
		
		requestor.setIdDevice(origin.getReq()[RequestorDto.POSITION_ID_DEVICE]);
		requestor.setAppVersion(origin.getReq()[RequestorDto.POSITION_APP_VERSION]);
		requestor.setIdPlatform(origin.getReq()[RequestorDto.POSITION_ID_PLATFORM]);
		requestor.setIdUser(origin.getReq()[RequestorDto.POSITION_ID_USER]);
		requestor.setTime(origin.getReq()[RequestorDto.POSITION_SYSTEM_TIME]);
		
		return requestor;
	}
}
