package com.fav24.shootr.web.controller;

import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fav24.shootr.batch.ShooterJobLauncher;

@RestController
@RequestMapping("/rest/")
public class InternalController {

	@Autowired
	private ShooterJobLauncher customJobLauncher;

	@Value("${batch.weblauncher.enabled}")
	private String enabled;

	@RequestMapping(value = "/getEncoding", method = RequestMethod.GET)
	public String getEncoding() throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("Default Charset=").append(Charset.defaultCharset().name());
		sb.append("  -  ");
		sb.append("file.encoding=").append(System.getProperty("file.encoding"));
		return sb.toString();
	}

	@RequestMapping(value = "/ping", method = RequestMethod.GET)
	public String ping() {
		return "pong";
	}
	
	@RequestMapping(value = "/throwTestException")
	public void throwTestException(@RequestBody String request) {
		throw new NullPointerException("test exception");
	}
}