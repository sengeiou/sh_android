package com.fav24.shootr.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import com.fav24.shootr.Application;
import com.fav24.shootr.web.dto.BaseDTO;
import com.fav24.shootr.web.dto.Status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@DirtiesContext
public class GlobalControllerExceptionHandlerTest {

	@Value("${local.server.port}")
	private int port;

	@Value("${server.contextPath}")
	private String contextPath;
	
	private String baseUrl;

	private final static RestTemplate restTemplate = new TestRestTemplate();

	@Before
	public void init() {
		baseUrl = "http://localhost:" + this.port + contextPath;
	}

	@Test
	public void throwTestException() throws Exception {
		String service = "/rest/throwTestException";
		String uri = baseUrl + service;
		String exceptionMessage = "test exception";
		
		ResponseEntity<BaseDTO> entity = null;
		BaseDTO response = null;
		Status status = null;
		try { 
			entity = restTemplate.postForEntity(uri, exceptionMessage, BaseDTO.class);
			response = entity.getBody();
			status = response.getStatus();
		} catch (Exception e){
			fail(e.getMessage());
		}
		
		System.out.println(entity);
		System.out.println(entity.getStatusCode());
		System.out.println(response);
		
		assertEquals(HttpStatus.OK, entity.getStatusCode());
		assertEquals(contextPath+service, response.getService());
		assertEquals(exceptionMessage, status.getMessage());
	}
}
