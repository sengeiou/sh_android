package com.fav24.dataservices.controller.rest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fav24.dataservices.domain.generic.Generic;
import com.fav24.dataservices.dto.generic.GenericDto;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.mapper.Mapper;
import com.fav24.dataservices.service.GenericService;

/**
 * Controla las peticiones de entrada a un conjunto de entidades de datos.
 * 
 * @author Fav24
 */
@Controller
@RequestMapping("/generic")
public class GenericController extends BaseRestController {

	final static Logger logger = LoggerFactory.getLogger(GenericController.class);

	private static final String MESSAGE_GENERIC_CALL_OK = "Las operaciones se han realizado correctamente.";
	
	
	@Autowired
	protected GenericService genericService;

	/**
	 * Procesa un conjunto de entidades de datos, según sus operaciones asociadas.
	 * 
	 * @param generic La lista de entidades, atributos y operaciones a procesar.
	 * 
	 * @return el resultado del procesado de la petición.
	 */
	@RequestMapping(value = "", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	GenericDto processGeneric(@RequestBody final GenericDto generic) {

		GenericDto result = null;

		try {
			result = Mapper.Map(genericService.processGeneric((Generic) Mapper.Map(generic)));
			
			result.setStatusCode(BaseRestController.OK);
			result.setStatusMessage(MESSAGE_GENERIC_CALL_OK);
		} catch (ServerException e) {

			result = new GenericDto(e);
			result.setRequestor(generic.getRequestor());
		}
		
		result.getRequestor().setSystemTime(System.currentTimeMillis());

		return result;
	}
}
