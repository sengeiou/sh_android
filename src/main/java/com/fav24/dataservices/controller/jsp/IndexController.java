package com.fav24.dataservices.controller.jsp;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fav24.dataservices.controller.rest.BaseRestController;

/**
 * Controla las peticiones de entrada al índice de la consola de administración.
 * 
 * @author Fav24
 */
@Controller
@RequestMapping("/")
public class IndexController extends BaseRestController {

	final static Logger logger = LoggerFactory.getLogger(IndexController.class);


	/**
	 * Muestra la lista de operaciones disponibles para este servicio de datos.
	 *  
	 * @return la vista del índice general.
	 */
	@RequestMapping(value = "/index", method = { RequestMethod.GET, RequestMethod.POST })
	public String availableEntities() {

		return "index";
	}
}
