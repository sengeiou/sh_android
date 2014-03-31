package com.fav24.dataservices.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.DataServicesContext;

@Component
public class ContextRefreshedListener implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private DataServicesContext dataServicesContext;

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

		DataServicesContext dataServicesContext = DataServicesContext.getCurrentDataServicesContext(); 

		// Se obtiene el directorio base de la aplicación.
		dataServicesContext.setApplicationHome(applicationContext.getEnvironment().getProperty(DataServicesContext.APPLICATION_HOME));

		// Se inicializa en contexto de ejecución de los data services.
		dataServicesContext.initDataServicesContext();
	}
}

