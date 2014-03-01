package com.fav24.dataservices.controller.jsp;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fav24.dataservices.controller.rest.BaseRestController;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.MainService;

/**
 * Controla las peticiones de entrada al índice de la consola de administración.
 * 
 * @author Fav24
 */
@Controller
@RequestMapping("/")
public class MainController extends BaseRestController {

	final static Logger logger = LoggerFactory.getLogger(MainController.class);

	@Autowired
	protected MainService mainService;


	/**
	 * Muestra la lista de operaciones disponibles para este servicio de datos.
	 *  
	 * @return la vista del índice general.
	 */
	@RequestMapping(value = "/main", method = { RequestMethod.GET, RequestMethod.POST })
	public String availableEntities() {

		return "main";
	}

	/**
	 * Muestra la información de las fuentes de datos, tanto de las que se expone información, como de las .
	 *  
	 * @return la vista de información de las fuentes de datos.
	 */
	@RequestMapping(value = "/dataSourceInformation", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView dataSourceInformation() {

		ModelAndView model;
		
		try {
			model = new ModelAndView("data_source_information");

			model.addObject("dataSource", mainService.getDataSourceInformation());
			model.addObject("statsDataSource", mainService.getStatsDataSourceInformation());

		} catch (ServerException e) {

			model = new ModelAndView("error_pages/server_error");

			model.getModel().put("errorCode", e.getErrorCode());
			model.getModel().put("message", e.getMessage());
		}

		return model;
	}
}
