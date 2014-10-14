import com.fav24.dataservices.domain.generic.DataItem;
import com.fav24.dataservices.domain.generic.Generic;
import com.fav24.dataservices.domain.generic.Operation;
import com.fav24.dataservices.domain.policy.AccessPolicy;
import com.fav24.dataservices.domain.policy.EntityAccessPolicy;
import com.fav24.dataservices.service.hook.GenericServiceHook;


public class Device implements GenericServiceHook {

	private static final Long ANDROID_PLATFORM = 0L;
	private static final Long IOS_PLATFORM = 1L;
	private static final Long WINDOWSPHONE_PLATFORM = 2L;

	private static final Long ANDROID_ENGINE = 2L;
	private static final Long IOS_ENGINE = 1L;
	private static final Long WINDOWSPHONE_ENGINE = 4L;

	private static final String ENTITY_DEVICE = "Device";

	private static final String ATTR_IDPUSHENGINE = "idPushEngine";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAlias() {
		return "CreateDevice";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {

		StringBuilder description = new StringBuilder();

		description.append("<p>Gestiona la inclusión del Device en el sistema.</p>");
		description.append("<ul>");
		description.append("<li>Añade información del push engine al que se asociará el device.</li>");
		description.append("</ul>");


		return description.toString();
	}

	/**
	 * {@inheritDoc}
	 * <p/>
	 * Añade el identificador del motor de pushes correspondiente.
	 */
	@Override
	public <T> HookMethodOutput requestBegin(T connection, AccessPolicy accessPolicy, Generic generic) {

		Long idPlatform = generic.getRequestor().getIdPlatform();
		Long idPushEngine;

		if (idPlatform == null) {
			return new HookMethodOutput("No se ha especificado ningún identificador de plataforma en el Requestor.");
		}

		if (idPlatform == ANDROID_PLATFORM) {
			idPushEngine = ANDROID_ENGINE;	
		}
		else if (idPlatform == IOS_PLATFORM) {
			idPushEngine = IOS_ENGINE;	
		}
		else if (idPlatform == WINDOWSPHONE_PLATFORM) {
			idPushEngine = WINDOWSPHONE_ENGINE;	
		}
		else {
			return new HookMethodOutput(String.format("El identificador de plataforma <%d> indicado en el Requestor, no es válido.", idPlatform));
		}

		for (Operation operation : generic.getOperations()) {

			if (operation.getMetadata().getOperation().equals(EntityAccessPolicy.OperationType.UPDATE_CREATE) && 
					ENTITY_DEVICE.equals(operation.getMetadata().getEntity())) {

				for (DataItem dataItem : operation.getData()) {

					dataItem.getAttributes().put(ATTR_IDPUSHENGINE, idPushEngine);
				}
			}
		}

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
	 */
	@Override
	public <T> HookMethodOutput operationBegin(T connection, EntityAccessPolicy entityAccessPolicy, Operation operation) {
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
