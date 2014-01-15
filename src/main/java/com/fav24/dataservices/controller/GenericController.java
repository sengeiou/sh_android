package com.fav24.dataservices.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fav24.dataservices.services.GenericService;
import com.fav24.dataservices.to.GenericResultTO;
import com.fav24.dataservices.to.GenericTO;

/**
 * Controla las peticiones de entrada a un conjunto de entidades de datos.
 * 
 * @author Fav24
 */
@Controller
@RequestMapping("/")
public class GenericController extends BaseController {

	final static Logger logger = LoggerFactory.getLogger(GenericController.class);


	@Autowired
	protected GenericService genericService;

	/**
	 * Procesa un conjunto de entidades de datos, según sus operaciones asociadas.
	 * 
	 * @param generic La lista de entidades, atributos y operaciones a procesar.
	 * 
	 * @return el resultado del procesado de la petición.
	 */
	@RequestMapping(value = "/generic", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	GenericResultTO processGeneric(@RequestBody final GenericTO generic) {

		return genericService.processGeneric(generic);
	}
}
