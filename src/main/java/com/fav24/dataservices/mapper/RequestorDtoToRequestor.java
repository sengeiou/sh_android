package com.fav24.dataservices.mapper;

import com.fav24.dataservices.domain.Requestor;
import com.fav24.dataservices.dto.RequestorDto;
import com.fav24.dataservices.exception.ServerException;


/**
 * Clase encargada del mapeo entre el objeto de transferencia RequestorDto y el objeto de dominio Requestor.
 */
public class RequestorDtoToRequestor extends Mapper<RequestorDto, Requestor> {

	public static final String ERROR_MAPPER_MALFORMED_REQUESTOR = "MAP_REQUESTOR";
	public static final String ERROR_MAPPER_MALFORMED_REQUESTOR_MESSAGE = "El array de valores que conforman la sección <req>, es incorrecto.";

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Requestor map(RequestorDto origin) throws ServerException {

		try {
			Requestor requestor = new Requestor();

			requestor.setIdDevice(origin.getReq()[RequestorDto.POSITION_ID_DEVICE]);
			requestor.setAppVersion(origin.getReq()[RequestorDto.POSITION_APP_VERSION]);
			requestor.setIdPlatform(origin.getReq()[RequestorDto.POSITION_ID_PLATFORM]);
			requestor.setIdUser(origin.getReq()[RequestorDto.POSITION_ID_USER]);
			requestor.setTime(origin.getReq()[RequestorDto.POSITION_SYSTEM_TIME]);

			return requestor;
		}
		catch(Exception e) {
			
			throw new ServerException(ERROR_MAPPER_MALFORMED_REQUESTOR, ERROR_MAPPER_MALFORMED_REQUESTOR_MESSAGE);	
		}
	}
}
