package com.fav24.shootr.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfiguration {

	@Value("${tomcat.connector.maxThreads}")
	private int MAX_THREADS;

//	@Bean
//	public EmbeddedServletContainerFactory servletContainer() {
//		TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
//		factory.addConnectorCustomizers(new TomcatConnectorCustomizer() {
//			@Override
//			public void customize(Connector connector) {
//				Object defaultMaxThreads = connector.getAttribute("maxThreads");
//				connector.setAttribute("maxThreads", MAX_THREADS);
//			}
//		});
//		factory.setPort(9000);
//		factory.addAdditionalTomcatConnectors(createSslConnector());
//		return factory;
//	}
//
//	private Connector createSslConnector() {
//		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
//		Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
//		try {
//			File keystore = new ClassPathResource("keystore").getFile();
//			File truststore = new ClassPathResource("keystore").getFile();
//			connector.setScheme("https");
//			connector.setSecure(true);
//			connector.setPort(8443);
//			protocol.setSSLEnabled(true);
//			protocol.setKeystoreFile(keystore.getAbsolutePath());
//			protocol.setKeystorePass("changeit");
//			protocol.setTruststoreFile(truststore.getAbsolutePath());
//			protocol.setTruststorePass("changeit");
//			protocol.setKeyAlias("apitester");
//			return connector;
//		} catch (IOException ex) {
//			throw new IllegalStateException("can't access keystore: [" + "keystore" + "] or truststore: [" + "keystore" + "]", ex);
//		}
//	}
}
