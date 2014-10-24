package com.fav24.shootr.web;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fav24.shootr.util.ObjectConverter;
import com.fav24.shootr.web.dto.BaseDTO;
import com.fav24.shootr.web.dto.Status;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleException(Exception e, HttpServletRequest servletRequest) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String exceptionAsString = sw.toString();

		Status status = new Status();
		status.setMessage(e.getMessage());
		status.setExplanation(exceptionAsString);
		status.setErrorCode("GMSS001");
		
		String params = null;
		Map<String, String[]> parameterMap = servletRequest.getParameterMap();
		try{ 
			params = ObjectConverter.toJson(parameterMap);
		} catch (Exception e2){
			
		}
		
		BaseDTO dto = new BaseDTO();
		dto.setJsonAttributes(params);
		dto.setService(servletRequest.getRequestURI().toString());
		dto.setStatus(status);
		
		ResponseEntity<Object> responseEntity = new ResponseEntity<Object>(dto, HttpStatus.OK);
		return responseEntity;
	}
}
