package com.fav24.dataservices.xml.policy;

import java.util.AbstractList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fav24.dataservices.domain.policy.EntityAccessPolicy;
import com.fav24.dataservices.domain.policy.EntityDataAttribute;
import com.fav24.dataservices.domain.policy.EntityOrderAttribute;
import com.fav24.dataservices.exception.ServerException;


public class EntityAccessPolicyDOM extends EntityAccessPolicy
{
	/**
	 * Lee, interpreta y construye las estructuras de políticas de acceso contenidas en el nodo indicado.
	 * 
	 * @param node Nodo del que se obtienen las políticas de acceso.
	 * 
	 * @throws ServerException 
	 */
	public EntityAccessPolicyDOM(Node node) throws ServerException {

		Element element = (Element) node;

		setAllowedOperations(element.getAttribute("AllowedOperations"));
		setVirtual(Boolean.parseBoolean(element.getAttribute("isVirtual")));
		setIncommingAlwaysWins(Boolean.parseBoolean(element.getAttribute("incommingAlwaysWins")));

		if (element.hasAttribute("Hooks")) {
			setHooks(element.getAttribute("Hooks"));
		}

		setOnlyByKey(Boolean.parseBoolean(element.getAttribute("OnlyByKey")));
		setOnlySpecifiedFilters(Boolean.parseBoolean(element.getAttribute("OnlySpecifiedFilters")));
		setMaxPageSize(Long.parseLong(element.getAttribute("MaxPageSize")));

		NodeList nodes_i = node.getChildNodes();

		for(int i=0; i < nodes_i.getLength(); i++) {
			Node node_i = nodes_i.item(i);

			if (node_i.getNodeType() == Node.ELEMENT_NODE) {

				String nodeName = node_i.getNodeName();

				if ("Name".equals(nodeName)) {
					setName(new EntityAttributeDOM(node_i));
				}
				else if ("Data".equals(nodeName)) {
					setData(new EntityDataDOM(node_i));
				}
				else if ("Keys".equals(nodeName)) {
					setKeys(new EntityKeysDOM(node_i));
				}
				else if ("Filters".equals(nodeName)) {
					setFilters(new EntityFiltersDOM(node_i));
				}
				else if ("Ordination".equals(nodeName)) {
					setOrdination(new EntityOrdinationDOM(node_i, getData()));
				}
			}
		}

		checkEntityAccessPolicy();
	}

	/**
	 * Asigna el conjunto de operaciones permitidas sobre esta entidad.
	 * 
	 * @param allowedOperations El conjunto de operaciones permitidas a asignar en 
	 * 							forma de literales de operaciones separados por espacios en blanco.
	 */
	private void setAllowedOperations(String allowedOperations) {

		String[] splittedAllowedOperations = allowedOperations.toLowerCase().trim().split(" ");

		for (String operation : splittedAllowedOperations) {
			getAllowedOperations().add(OperationType.fromString(operation.trim()));
		}
	}

	/**
	 * Asigna el conjunto de puntos de incorporación.
	 * 
	 * @param hooks El conjunto de puntos de incorporación en 
	 * 							forma de literales de hooks separados por espacios en blanco.
	 */
	private void setHooks(String hooks) {

		String[] splittedHooks = hooks.trim().split(" ");

		for (String hook : splittedHooks) {

			if (hook != null) {

				hook = hook.trim();
				if (hook.length() > 0) {

					getHooks().put(hook, null);
				}
			}
		}
	}

	/**
	 * Lanza una excepción en caso de encontrar algún problema en esta definición de políticas de acceso.
	 * 
	 * @throws ServerException
	 */
	public void checkEntityAccessPolicy() throws ServerException {

		// Atributos de datos de la entidad.
		if (getData() != null && getData().getData().size() > 0) {

			AbstractList<EntityDataAttribute> data = getData().getData();

			StringBuilder duplicatedAttributes = null;

			for(int i=0; i<data.size(); i++) {

				String currentAlias = data.get(i).getAlias();

				for(int j=0; j<data.size(); j++) {

					if (i != j) {

						if (currentAlias.equalsIgnoreCase(data.get(j).getAlias())) {
							if (duplicatedAttributes == null) {
								duplicatedAttributes = new StringBuilder(currentAlias);
							}
							else {
								duplicatedAttributes.append(", ").append(currentAlias);
							}
						}
					}
				}
			}

			if (duplicatedAttributes != null) {
				throw new ServerException(ERROR_ACCESS_POLICY_CHECK_FAILED, ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " Los atributos <" + duplicatedAttributes + "> en la sección de datos de la entidad "  + getName().getAlias() + ", están repetidos.");
			}
		}

		// Atributos de las ordenaciones de la entidad.
		if (getOrdination() != null && getOrdination().getOrder().size() > 0) {

			AbstractList<EntityOrderAttribute> order = getOrdination().getOrder();

			StringBuilder illegalAttributes = null;

			for(EntityOrderAttribute orderAttribute : order) {

				if (!getData().hasAttribute(orderAttribute.getAlias())) {

					if (illegalAttributes == null) {
						illegalAttributes = new StringBuilder(orderAttribute.getAlias());
					}
					else {
						illegalAttributes.append(", ").append(orderAttribute.getAlias());	
					}
				}
			}

			if (illegalAttributes != null) {
				throw new ServerException(ERROR_ACCESS_POLICY_CHECK_FAILED, ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No se han definido los atributos correspondientes en la sección de datos, para los atributos de ordenación <" + illegalAttributes + "> de la entidad "  + getName().getAlias() + ".");
			}
		}
	}
}
