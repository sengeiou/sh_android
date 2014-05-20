package com.fav24.dataservices.controller.jsp;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.fav24.dataservices.exception.ServerException;

/**
 * Controlador base.
 */
public class BaseJspController {

	private ModelAndView errorView;


	/**
	 * Constructor por defecto.
	 */
	public BaseJspController() {
		errorView = new ModelAndView("error_pages/server_error");
	}

	/**
	 * Handler genérico para excepciones controladas.
	 * 
	 * @param exception Excepción a gestionar
	 * 
	 * @return Vista y modelo del error.
	 */
	@ExceptionHandler(ServerException.class)
	public ModelAndView handleAnyException(ServerException exception) {

		errorView.getModel().put("errorCode", exception.getErrorCode());
		errorView.getModel().put("message", exception.getMessage());
		
		return errorView;
	}

	/**
	 * Handler por defecto para excepciones no controladas.
	 * 
	 * @param exception Excepción a gestionar
	 * 
	 * @return Vista y modelo del error.
	 */
	@ExceptionHandler(Exception.class)
	public ModelAndView handleAnyException(Exception exception) {

		errorView.getModel().put("errorCode", "Error desconocido");
		errorView.getModel().put("message", exception.getMessage());
		
		return errorView;
	}
}