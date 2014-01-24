package com.fav24.dataservices.controller;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.fav24.dataservices.dto.exception.ExceptionDto;
import com.fav24.dataservices.exception.ServerException;

/**
 * Controlador base.
 * 
 * @author Fav24
 */
public class BaseController {

	protected final static Logger logger = LoggerFactory.getLogger(BaseController.class);
	public final static String ERROR_GLOBAL_001 = "GLOBAL_001";

	/**
	 * Vista en formato JSON que se usará para el envío de los mensajes de error.
	 */
	private MappingJackson2JsonView jsonErrorView;


	/**
	 * Constructor por defecto.
	 */
	public BaseController() {
		jsonErrorView = new MappingJackson2JsonView();
	}

	/**
	 * Handler genérico para excepciones controladas.
	 * 
	 * @param exception Excepción a gestionar
	 * 
	 * @return Vista y modelo del error en formato JSON.
	 */
	@ExceptionHandler(ServerException.class)
	public ModelAndView handleAnyException(ServerException exception) {

		logger.error(ExceptionUtils.getFullStackTrace(exception));

		Map<String, String> error = new HashMap<String, String>();

		error.put("errorCode", exception.getErrorCode());
		error.put("errorCode", exception.getMessage());

		return new ModelAndView(jsonErrorView, error);
	}

	/**
	 * Handler por defecto para excepciones no controladas.
	 * 
	 * @param exception Excepción a gestionar
	 * 
	 * @return Vista y modelo del error en formato JSON.
	 */
	@ExceptionHandler(Exception.class)
	public ModelAndView handleAnyException(Exception exception) {
		logger.error(ExceptionUtils.getFullStackTrace(exception));

		Map<String, String> error = new HashMap<String, String>();

		exception.printStackTrace();
		error.put("errorCode", "unknow error");
		error.put("message", exception.getMessage());

		return new ModelAndView(jsonErrorView, error);
	}


	/**
	 * Handler específica para la excepción de tipo de medio no soportado.
	 *   
	 * @param exception Excepción a tratar.
	 * @param request Solicitud que provocó la excepción.
	 * @param response Respuesta a la solicitud.
	 * 
	 * @return excepción en forma de objeto de transferencia. 
	 */
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	@ResponseBody
	public ExceptionDto handleExceptionContentType(HttpMediaTypeNotSupportedException exception, HttpServletRequest request, HttpServletResponse response)
	{
		// -----------------------------------------------
		// Based on http://www.tuicool.com/articles/Vzea2y
		// -----------------------------------------------
		ExceptionDto exceptionTo = new ExceptionDto(ERROR_GLOBAL_001, null, exception.getMessage());

		if (logger.isWarnEnabled()) {

			StringBuilder logText = new StringBuilder();

			logText.append("\n");
			logText.append("INICI -------------------------------------\n");
			logText.append("      - ").append(exception.getMessage()).append("\n");
			logText.append("Path         [").append(request.getPathInfo()).append("] \n");
			logText.append("Method       [").append(request.getMethod()).append("] \n");
			logText.append("contentType  [").append(request.getContentType()).append("] \n");
			logText.append("contentLeng  [").append(request.getContentLength()).append("] \n");
			logText.append("locale       [").append(request.getLocale().toString()).append("] \n");
			logText.append("remote Addr  [").append(request.getRemoteAddr()).append("] \n");
			logText.append("remote Host  [").append(request.getRemoteHost()).append("] \n");
			logText.append("remote User  [").append(request.getRemoteUser()).append("] \n");
			logText.append("charEncoding [").append(request.getCharacterEncoding()).append("] \n");

			// Get Header Information
			Enumeration<?> headerNames = request.getHeaderNames();
			if (headerNames.hasMoreElements()) {
				logText.append("    HEADERS \n");
				logText.append("    ------- \n");

				while (headerNames.hasMoreElements()) {
					
					String headerName = (String) headerNames.nextElement();
					Enumeration<?> headers = request.getHeaders(headerName);
					StringBuffer sb = new StringBuffer();
					boolean first = true;
					
					while (headers.hasMoreElements()) {
						String header = (String)headers.nextElement();
						if (first) {
							first = false;
						}
						else {
							sb.append(", ");
						}
						sb.append(header);
					}
					
					logText.append("      ").append(headerName).append('[').append(sb).append("] \n");
				}
			}

			logger.warn(logText.toString());
		}

		return exceptionTo;
	}
}