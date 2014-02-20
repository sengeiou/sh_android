package com.fav24.dataservices.listener;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.security.AccessPolicy;
import com.fav24.dataservices.service.impl.GenericServiceJDBC;

@Component
public class ContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent> {

	public static final Logger logger = LoggerFactory.getLogger(ContextRefreshedListener.class);

	public static final String APPLICATION_NAME = "dataservices";
	public static final String APPLICATION_HOME = APPLICATION_NAME + ".home";

	public static final String ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_NOT_DEFINED = "AC000";
	public static final String ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_NOT_DEFINED_MESSAGE = "El parámetro " + APPLICATION_HOME + " no está definido en el contexto del servidor de aplicaciones.";
	public static final String ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_DOESNT_EXISTS = "AC001";
	public static final String ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_DOESNT_EXISTS_MESSAGE = "La ruta indicada en el parámetro " + APPLICATION_HOME + " <%s> no existe.";
	public static final String ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_IS_NOT_A_DIRECTORY = "AC002";
	public static final String ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_IS_NOT_A_DIRECTORY_MESSAGE = "La ruta indicada en el parámetro " + APPLICATION_HOME + " <%s> no hace referencia a un directorio.";


	private static String applicationHome;

	@Autowired
	protected GenericServiceJDBC genericServiceJDBC;


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {

		ApplicationContext applicationContext = event.getApplicationContext();

		/*
		 * La inicialización de cada componente, provoca un refresco del contexto.
		 * 
		 * Por ejemplo:
		 * 		Root WebApplicationContext ------> WebApplicationContext for namespace 'mvc-rest-dispatcher-servlet'
		 *                                     |-> WebApplicationContext for namespace 'mvc-jsp-dispatcher-servlet'
		 * 
		 * Para cargar la información únicamente en el inicio del contexto raiz, una buena
		 * práctica para distinguir el contexto raiz del resto, es comprobar si su contexto
		 * predecesor es nulo. 
		 */
		if (applicationContext.getParent() != null) {
			return;	
		}
		
		// Se obtiene el directorio base de la aplicación.
		applicationHome = applicationContext.getEnvironment().getProperty(APPLICATION_HOME);

		try {

			if (applicationHome == null) {

				throw new ServerException(ContextRefreshedListener.ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_NOT_DEFINED, 
						ContextRefreshedListener.ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_NOT_DEFINED_MESSAGE);
			}
			else {

				// Se cargan los archivos de políticas de seguridad existentes.
				File applicationHomeDir = new File(applicationHome);

				if (!applicationHomeDir.exists()) {

					throw new ServerException(ContextRefreshedListener.ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_DOESNT_EXISTS, 
							String.format(ContextRefreshedListener.ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_DOESNT_EXISTS_MESSAGE, applicationHomeDir.toPath().toString()));
				}
				else if (!applicationHomeDir.isDirectory()) {

					throw new ServerException(ContextRefreshedListener.ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_IS_NOT_A_DIRECTORY, 
							String.format(ContextRefreshedListener.ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_IS_NOT_A_DIRECTORY_MESSAGE, applicationHomeDir.toPath().toString()));
				}
				else {

					AccessPolicy.resetAccessPolicies();
					AccessPolicy.loadDefaultAccessPolicies();

					genericServiceJDBC.checkAccessPoliciesAgainstDataSource(AccessPolicy.getCurrentAccesPolicy());
				}
			}
		}
		catch(ServerException e) {

			AccessPolicy.resetAccessPolicies();
			
			logger.error(e.getMessage());
		}
	}

	/**
	 * Retorna la ruta base de la aplicación.
	 * 
	 * @return la ruta base de la aplicación.
	 */
	public static String getApplicationHome() {
		return applicationHome;
	}
}

