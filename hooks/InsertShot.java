import java.util.Map;

import com.fav24.dataservices.domain.generic.Generic;
import com.fav24.dataservices.domain.generic.Operation;
import com.fav24.dataservices.domain.policy.AccessPolicy;
import com.fav24.dataservices.domain.policy.EntityAccessPolicy;
import com.fav24.dataservices.service.hook.GenericServiceHook;


public class InsertShot implements GenericServiceHook {


	private static final String ATTR_COMMENT = "comment";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAlias() {
		return "InsertShot";
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

	private boolean isOperationMod(EntityAccessPolicy.OperationType operationType) {

		return (operationType.equals(EntityAccessPolicy.OperationType.CREATE))
				|| (operationType.equals(EntityAccessPolicy.OperationType.CREATE_UPDATE))
				|| (operationType.equals(EntityAccessPolicy.OperationType.UPDATE))
				|| (operationType.equals(EntityAccessPolicy.OperationType.UPDATE_CREATE))
				;
	}

	/**
	 * {@inheritDoc}
	 * <p/>
	 * Este metodo valida que se cumplen todas las precondiciones antes de empezar a resolver las operaciones.
	 * <p/>
	 * Si alguna precondicion no se cumple devuelve su respectivo error.
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

		if (isOperationMod(operation.getMetadata().getOperation())) {
			
			int numberOfDataItems = operation.getData().size();
			
			for (int j = 0; j < numberOfDataItems; j++) {

				for (Map.Entry<String, Object> entry : operation.getData().get(j).getAttributes().entrySet()) {

					if (ATTR_COMMENT.equals(entry.getKey())) {
						String text = (String) entry.getValue();
						while (text.contains("\n\n\n")) {
							text = text.replaceAll("\n\n\n", "\n\n");
						}
						text = text.trim();
						entry.setValue(text);
					}

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

		return HookMethodOutput.CONTINUE;
	}
}
