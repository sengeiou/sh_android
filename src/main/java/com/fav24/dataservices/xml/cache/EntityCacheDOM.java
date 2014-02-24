package com.fav24.dataservices.xml.cache;

import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class EntityCacheDOM extends CacheConfigurationDOM
{
	private String Alias;


	/**
	 * Lee, interpreta y construye las estructuras de políticas de acceso contenidas en el nodo indicado.
	 * 
	 * @param node Nodo del que se obtienen las políticas de acceso.
	 * @param defaultCacheConfiguration Configuración por defecto de los aspectos de la caché.
	 */
	public EntityCacheDOM(Node node, CacheConfigurationDOM defaultCacheConfiguration) {

		super(node);

		setAlias(((Element)node).getAttribute("AllowedOperations"));
	}

	/**
	 * Retorna el alias de la entidad a la que hace referencia esta caché.
	 * 
	 * @return el alias de la entidad a la que hace referencia esta caché.
	 */
	public String getAlias() {
		return Alias;
	}

	/**
	 * Asigna el alias de la entidad a la que hace referencia esta caché.
	 *  
	 * @param alias Alias a asignar.
	 */
	public void setAlias(String alias) {
		Alias = alias;
	}
}
