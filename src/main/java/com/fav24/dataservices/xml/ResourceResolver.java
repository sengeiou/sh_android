package com.fav24.dataservices.xml;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import com.fav24.dataservices.service.policy.AccessPolicyService;


/**
 * Resolutor de entradas de parser de tipo Load & Save.
 */
public class ResourceResolver implements LSResourceResolver {

	/**
	 * Clase que resuelve una entrada de parser de tipo Load & Save.
	 */
	public class CamaleonLSInput implements LSInput {

		private String publicId;
		private String systemId;
		private String baseURI;
		private Charset encoding;


		/**
		 * Constructor.
		 * 
		 * @param publicId Identificador público del recurso a resolver.
		 * @param systemId Identificador de sistema del recurso a resolver.
		 * @param baseURI Ubicación base del recurso a resolver.
		 */
		public CamaleonLSInput(String publicId, String systemId, String baseURI) {
			this.publicId = publicId;
			this.systemId = systemId;
			this.baseURI = baseURI;
			this.encoding = Charset.forName("UTF-8");
		}

		/**
		 * {@inheritDoc}
		 */
		public String getBaseURI() {
			return baseURI;
		}

		/**
		 * Retorna un stream de bytes correspondiente al contenido del recurso resuelto.
		 * 
		 * @return un stream de bytes correspondiente al contenido del recurso resuelto.
		 */
		public InputStream getByteStream()
		{
			if (systemId.endsWith("AccessPolicy.xsd"))
				return AccessPolicyService.class.getResourceAsStream("AccessPolicy.xsd");

			return null;
		}

		/**
		 * Retorna un stream de caracteres correspondiente al contenido del recurso resuelto.
		 * 
		 * @return un stream de caracteres correspondiente al contenido del recurso resuelto.
		 */
		public Reader getCharacterStream() {
			InputStream is = getByteStream();

			return (is == null) ? null : new InputStreamReader(is, encoding);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * No implementado.
		 */
		public void setCharacterStream(Reader characterStream)	{
		}

		/**
		 * {@inheritDoc}
		 * 
		 * No implementado. Siempre retorna false.
		 */
		public boolean getCertifiedText() {
			return false;
		}
		
		/**
		 * {@inheritDoc}
		 * 
		 * No implementado. No tiene efecto.
		 */
		public void setCertifiedText(boolean certifiedText)	{
		}

		/**
		 * Retorna la codificación del contenido del recurso resuelto.
		 * 
		 * @return la codificación del contenido del recurso resuelto.
		 */
		public String getEncoding()	{
			return encoding.displayName();
		}
		
		/**
		 * Asigna la codificación en la que se debe interpretar el contenido del recurso resuelto.
		 * 
		 * @param encoding Codificación en la que se debe interpretar el contenido del recurso resuelto.
		 */
		public void setEncoding(String encoding) {
			this.encoding = Charset.forName(encoding);
		}
		
		/**
		 * Retorna el identificador público del recurso a resolver.
		 * 
		 * @return el identificador público del recurso a resolver.
		 */
		public String getPublicId() {
			return publicId;
		}

		/**
		 * Asigna el identificador público del recurso a resolver.
		 * 
		 * @param publicId El identificador público del recurso a asignar.
		 */
		public void setPublicId(String publicId) {
			this.publicId = publicId;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * No implementado.
		 */
		public String getStringData() {
			return null;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * No implementado.
		 */
		public void setStringData(String stringData) {
		}

		/**
		 * Retorna el identificador único del sistema, del recurso a resolver.
		 * 
		 * @return el identificador único del sistema, del recurso a resolver.
		 */
		public String getSystemId()	{
			return systemId;
		}

		public void setSystemId(String systemId) {
			this.systemId = systemId;
		}
		
		/**
		 * {@inheritDoc}
		 * 
		 * No implementado.
		 */
		public void setBaseURI(String baseURI) {
		}

		public void setByteStream(InputStream byteStream) {
		}
	}

	public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI)
	{
		return new CamaleonLSInput(publicId, systemId, baseURI);
	}
}
