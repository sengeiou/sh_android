package com.fav24.shootr.web.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import com.fav24.shootr.Application;

/**
 * Clase base de tests a controllers. 
 * Levanta spring boot con todo el contexto de spring.
 * Utiliza un puerto aleatorio para no interferir con puertos activos. 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@DirtiesContext
public class BaseControllerTest {
	
	@Value("${local.server.port}")
	protected int port;
	
	@Value("${server.contextPath}")
	protected String contextPath;
	
	protected final static String restUrl = "/rest";

	protected String baseUrl;
	
	protected final static RestTemplate restTemplate = new TestRestTemplate();
	
	@Before
	public void init(){
		baseUrl = "http://localhost:" + this.port + contextPath + restUrl;
	}
	
	@Test
	public void doNothing(){};
}
