import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fav24.dataservices.domain.generic.DataItem;
import com.fav24.dataservices.domain.generic.Generic;
import com.fav24.dataservices.domain.generic.Operation;
import com.fav24.dataservices.domain.policy.AccessPolicy;
import com.fav24.dataservices.domain.policy.EntityAccessPolicy;
import com.fav24.dataservices.service.hook.GenericServiceHook;
import com.fav24.dataservices.util.FastHttpUtils;


public class CreateShot implements GenericServiceHook {

	private static final String SPECIAL_SERVICES_HOST = "localhost";
	private static final Integer SPECIAL_SERVICES_PORT = 8085;

	private static final String QUEUE_SHOT_URL = "http://" + SPECIAL_SERVICES_HOST + ":" + SPECIAL_SERVICES_PORT + "/shootr-services/rest/notification/shot";

	private static final String ENTITY_SHOT = "Shot";

	private static final String ATTR_IDSHOT = "idShot";
	private static final String ATTR_IDUSER = "idUser";
	private static final String ATTR_COMMENT = "comment";

	private static final ObjectMapper objectMapper = new ObjectMapper();


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAlias() {
		return "CreateShot";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {

		StringBuilder description = new StringBuilder();

		description.append("<p>Gestiona las validaciones que se aplican a las inserciones de shots</p>");
		description.append("<ul>");
		description.append("<li>Un usuario no puede insertar 2 shots iguales seguidos</li>");
		description.append("<li>Si un shot contiene mas de 2 saltos de linea consecutivos son ignorados</li>");
		description.append("</ul>");


		return description.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> HookMethodOutput requestBegin(T connection, AccessPolicy accessPolicy, Generic generic) {

		return HookMethodOutput.CONTINUE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> HookMethodOutput requestEnd(T connection, AccessPolicy accessPolicy, Generic generic) {

		return HookMethodOutput.CONTINUE;
	}

	/**
	 * {@inheritDoc}
	 * <p/>
	 * Resuelve la operación validando teniendo en cuenta las restricciones de la politica en cuestión
	 */
	@Override
	public <T> HookMethodOutput operationBegin(T connection, EntityAccessPolicy entityAccessPolicy, Operation operation) {

		if (operation.getMetadata().getOperation().equals(EntityAccessPolicy.OperationType.CREATE) && 
				ENTITY_SHOT.equals(operation.getMetadata().getEntity())) {

			for (DataItem dataItem : operation.getData()) {

				String text  = (String)dataItem.getAttributes().get(ATTR_COMMENT);

				if (text != null) {

					while (text.contains("\n\n\n")) {
						text = text.replaceAll("\n\n\n", "\n\n");
					}

					dataItem.getAttributes().put(ATTR_COMMENT, text.trim());
				}
			}
		}

		return HookMethodOutput.CONTINUE;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> HookMethodOutput operationEnd(T connection, EntityAccessPolicy entityAccessPolicy, Operation operation) {

		if (operation.getMetadata().getOperation().equals(EntityAccessPolicy.OperationType.CREATE) && 
				ENTITY_SHOT.equals(operation.getMetadata().getEntity())) {

			Map<String, Object> attrs = new HashMap<String, Object>();

			for (DataItem dataItem : operation.getData()) {

				// Envío an sistema de push.
				attrs.put(ATTR_IDSHOT, dataItem.getAttributes().get(ATTR_IDSHOT));
				attrs.put(ATTR_IDUSER, dataItem.getAttributes().get(ATTR_IDUSER));
				attrs.put(ATTR_COMMENT, dataItem.getAttributes().get(ATTR_COMMENT));

				try {
					FastHttpUtils.sendPost(QUEUE_SHOT_URL, null, objectMapper.writeValueAsString(attrs), null);
				} catch (Throwable t) {
					return new HookMethodOutput(t.getMessage());
				}
			}
		}

		return HookMethodOutput.CONTINUE;
	}
}
