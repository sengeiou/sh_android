import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fav24.dataservices.domain.generic.DataItem;
import com.fav24.dataservices.domain.generic.Generic;
import com.fav24.dataservices.domain.generic.Operation;
import com.fav24.dataservices.domain.policy.AccessPolicy;
import com.fav24.dataservices.domain.policy.EntityAccessPolicy;
import com.fav24.dataservices.service.hook.GenericServiceHook;
import com.fav24.dataservices.util.FastHttpUtils;
import com.fav24.dataservices.util.JDBCUtils;


public class Follow implements GenericServiceHook {

	private static final String GET_USER_DELETE = "SELECT `csys_deleted` FROM `User` "
			+ "WHERE `idUser`=?";

	private static final String UPDATE_FOLLOW_DELETE = "UPDATE `Follow` SET "
			+ "`csys_deleted`=? "
			+ "WHERE `idUser`= ? AND `idUserFollowed`= ?";

	private static final String UPDATE_FOLLOWINGS = "UPDATE `User` SET "
			+ "`numFollowings`=?, "
			+ "`csys_birth`=?, `csys_modified`=? , `csys_revision`=`csys_revision` + 1 "
			+ "WHERE `idUser`=?";

	private static final String GET_NUM_FOLLOWINGS = "SELECT COUNT(*) FROM `Follow` "
			+ "WHERE `idUser`= ? AND `csys_deleted` IS NULL";

	private static final String UPDATE_FOLLOWERS = "UPDATE `User` SET "
			+ "`numFollowers`=?, "
			+ "`csys_birth`=?, `csys_modified`=?, `csys_revision`=`csys_revision` + 1 "
			+ "WHERE `idUser`=? AND `csys_deleted` IS NULL";

	private static final String GET_NUM_FOLLOWERS = "SELECT COUNT(*) FROM `Follow` "
			+ "WHERE `idUserFollowed`= ? AND `csys_deleted` IS NULL";


	private static final String SPECIAL_SERVICES_HOST = "localhost";
	private static final Integer SPECIAL_SERVICES_PORT = 8085;

	private static final String QUEUE_SHOT_URL = "http://" + SPECIAL_SERVICES_HOST + ":" + SPECIAL_SERVICES_PORT + "/shootr-services/rest/notification/follow";

	private static final String ENTITY_FOLLOW = "Follow";

	private static final String ATTR_IDUSER = "idUser";
	private static final String ATTR_IDFOLLOWEDUSER = "idFollowedUser";

	private static final String ATTR_BIRTH = "birth";
	private static final String ATTR_MODIFIED = "modified";
	private static final String ATTR_REVISION = "revision";


	private static final ObjectMapper objectMapper = new ObjectMapper();


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAlias() {
		return "Follow";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {

		StringBuilder description = new StringBuilder();

		description.append("<p>Gestiona las acciones relacionas con Follow y Unfollow.</p>");
		description.append("<ul>");
		description.append("<li>Controla el caso de seguimiento offline de un usuario borrado.</li>");
		description.append("<li>Recalcula los Followings y Followers del solicitante y de sus afectados.</li>");
		description.append("<li>Envía la notificación correspondiente en caso de añadir un nuevo Following.</li>");
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

        if (ENTITY_FOLLOW.equals(operation.getMetadata().getEntity())) {

            Connection sqlConnection = (Connection) connection;

            if (operation.getMetadata().getOperation().equals(EntityAccessPolicy.OperationType.CREATE) || operation.getMetadata().getOperation().equals(EntityAccessPolicy.OperationType.DELETE)) {

                try {

                    for (DataItem dataItem : operation.getData()) {

                        Long idUser = ((Number) dataItem.getAttributes().get(ATTR_IDUSER)).longValue();
                        Long idUserFollowed = ((Number) dataItem.getAttributes().get(ATTR_IDFOLLOWEDUSER)).longValue();
                        Timestamp deleteUser = getUserDelete(sqlConnection, idUser);
                        Timestamp deleteFollowed = getUserDelete(sqlConnection, idUserFollowed);

                        /*
                        Si uno de los dos usuarios está borrado, la operación no ha de efectuarse
                         */
                        if (deleteUser != null || deleteFollowed != null ){
                            return HookMethodOutput.STOP_OK;
                        }
                    }

                } catch (Throwable t) {
                    return new HookMethodOutput(t.getMessage());
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

        Connection sqlConnection = (Connection) connection;

		if (ENTITY_FOLLOW.equals(operation.getMetadata().getEntity())) {
            try {
                if (operation.getMetadata().getOperation().equals(EntityAccessPolicy.OperationType.CREATE)) {

                    manageFollowEvents(sqlConnection, operation, true);
                }
                else if (operation.getMetadata().getOperation().equals(EntityAccessPolicy.OperationType.DELETE)) {

                    manageFollowEvents(sqlConnection, operation, false);
                }
            } catch (Throwable t) {
                return new HookMethodOutput(t.getMessage());
            }
		}
		return HookMethodOutput.CONTINUE;
	}


    /**
     * Para cada una de las operaciones, gestiona los eventos de follow, generando push o no dependiendo del resultado
     * @param sqlConnection
     * @param operation
     * @param generatePush Si es true, genera un push
     * @throws IOException
     */
    private void manageFollowEvents(Connection sqlConnection, Operation operation, boolean generatePush) throws IOException {
        for (DataItem dataItem : operation.getData()) {
            manageFollowEvent(sqlConnection, dataItem, generatePush);
        }
    }

    /**
     * Actualiza el numero de gente a la que sigue el usuario que ha hecho follow / unfollow y los seguidores del usuario que recibe la acción
     *
     * @param sqlConnection
     * @param dataItem
     * @param generatePush Si es true, genera un push
     * @throws IOException
     */
    private void manageFollowEvent(Connection sqlConnection, DataItem dataItem, boolean generatePush) throws IOException {

        // Actualización de following y followers.
        updateFollowers(sqlConnection, dataItem);
        updateFollowings(sqlConnection, dataItem);

        if (generatePush){

            Long idUser = ((Number)dataItem.getAttributes().get(ATTR_IDUSER)).longValue();
            Long idUserFollowed = ((Number)dataItem.getAttributes().get(ATTR_IDFOLLOWEDUSER)).longValue();

            // Envío an sistema de push.
            Map<String, Object> attrs = new HashMap<String, Object>();
            attrs.put(ATTR_IDUSER, idUser);
            attrs.put(ATTR_IDFOLLOWEDUSER, idUserFollowed);
            FastHttpUtils.sendPost(QUEUE_SHOT_URL, null, objectMapper.writeValueAsString(attrs), null);
        }

    }

    /**
	 * Retorna <code>null</code> o el momento en que el usuario fué eliminado.
	 * 
	 * @param connection Conexión con transacción abierta contra el subsistema.
	 * @param idUser Identificador del usuario del que se desea obtener su información de borrado.
	 * 
	 * @return <code>null</code> o el momento en que el usuario fué eliminado.
	 * 
	 * @throws SQLException 
	 */
	private Timestamp getUserDelete(Connection connection, long idUser) throws SQLException {

		PreparedStatement statement = null;

		try {
			statement = connection.prepareStatement(GET_USER_DELETE);

			JDBCUtils.setObject(statement, 1, idUser, java.sql.Types.NUMERIC);

			if (statement.execute()) {

				ResultSet resultSet = statement.getResultSet();

				if (resultSet.first()) {
					return JDBCUtils.getObject(resultSet, 1, Timestamp.class);
				}
			}
		}
		finally {
			JDBCUtils.CloseQuietly(statement);
		}

		return null;
	}

	/**
	 * Retorna true o false en función de si se eliminó o no el follow correspondiente.
	 * 
	 * @param connection Conexión con transacción abierta contra el subsistema.
	 * @param delete Momento en el que el usuario fué eliminado.
	 * @param idUser Identificador del usuario que ha realizado el follow.
	 * @param idUserFollowed Identificador del usuario followed.
	 * 
	 * @return true o false en función de si se eliminó o no el follow correspondiente.
	 * 
	 * @throws SQLException 
	 */
    @Deprecated
	private boolean updateFollowDelete(Connection connection, Timestamp delete, long idUser, long idUserFollowed) throws SQLException {

		PreparedStatement statement = null;

		try {
			statement = connection.prepareStatement(UPDATE_FOLLOW_DELETE);

			JDBCUtils.setObject(statement, 1, delete, java.sql.Types.TIMESTAMP);
			JDBCUtils.setObject(statement, 2, idUser, java.sql.Types.NUMERIC);
			JDBCUtils.setObject(statement, 3, idUserFollowed, java.sql.Types.NUMERIC);

			if (statement.executeUpdate() > 0) {
				return true;
			}
		}
		finally {
			JDBCUtils.CloseQuietly(statement);
		}

		return false;
	}

	/**
	 * Retorna el número de followers del usuario indicado.
	 * 
	 * @param connection Conexión con transacción abierta contra el subsistema.
	 * @param idUser Identificador del usuario del que se desea obtener su número de followers.
	 * 
	 * @return el número de followers del usuario indicado.
	 * 
	 * @throws SQLException 
	 */
	private long getNumFollowers(Connection connection, long idUser) throws SQLException {

		PreparedStatement statement = null;
		long result = 0;

		try {
			statement = connection.prepareStatement(GET_NUM_FOLLOWERS);

			JDBCUtils.setObject(statement, 1, idUser, java.sql.Types.NUMERIC);

			if (statement.execute()) {

				ResultSet resultSet = statement.getResultSet();

				if (resultSet.first()) {
					result = JDBCUtils.getObject(resultSet, 1, Long.class);
				}
			}
		}
		finally {
			JDBCUtils.CloseQuietly(statement);
		}

		return result;
	}

	/**
	 * Retorna el número de followings del usuario indicado.
	 * 
	 * @param connection Conexión con transacción abierta contra el subsistema.
	 * @param idUser Identificador del usuario del que se desea obtener su número de followings.
	 * 
	 * @return el número de followings del usuario indicado.
	 * 
	 * @throws SQLException 
	 */
	private long getNumFollowings(Connection connection, long idUser) throws SQLException {

		PreparedStatement statement = null;
		long result = 0;

		try {
			statement = connection.prepareStatement(GET_NUM_FOLLOWINGS);

			JDBCUtils.setObject(statement, 1, idUser, java.sql.Types.NUMERIC);

			if (statement.execute()) {

				ResultSet resultSet = statement.getResultSet();

				if (resultSet.first()) {
					result = JDBCUtils.getObject(resultSet, 1, Long.class);
				}
			}
		}
		finally {
			JDBCUtils.CloseQuietly(statement);
		}

		return result;
	}

	/**
	 * Retorna el resultado de la operación de actualización de followings.
	 * 
	 * @param connection Conexión con transacción abierta contra el subsistema.
	 * @param dataItem Datos de la operación.
	 * 
	 * @return el resultado de la operación de actualización de followings.
	 */
	private HookMethodOutput updateFollowings(Connection connection, DataItem dataItem) {

		NavigableMap<String, Object> attributes = dataItem.getAttributes();

		PreparedStatement statement = null;

		// Actualizar contador de los followings del usuario.
		try {

			long numFollowings = getNumFollowings(connection, ((Number)attributes.get(ATTR_IDUSER)).longValue());

			statement = connection.prepareStatement(UPDATE_FOLLOWINGS);

			JDBCUtils.setObject(statement, 1, numFollowings, java.sql.Types.NUMERIC);

			Long milliseconds = attributes.get(ATTR_BIRTH) != null ? ((Number)attributes.get(ATTR_BIRTH)).longValue() : null;
			JDBCUtils.setObject(statement, 2, milliseconds == null ? null : new Timestamp(milliseconds), java.sql.Types.TIMESTAMP);

			milliseconds = attributes.get(ATTR_MODIFIED) != null ? ((Number)attributes.get(ATTR_MODIFIED)).longValue() : null;
			JDBCUtils.setObject(statement, 3, milliseconds == null ? null : new Timestamp(milliseconds), java.sql.Types.TIMESTAMP);

			JDBCUtils.setObject(statement, 4, attributes.get(ATTR_IDUSER), java.sql.Types.NUMERIC);

			if (statement.executeUpdate() != 1) {
				return new HookMethodOutput(String.format("No ha sido posible actualizar el número de followings para el usuario <%d>.", attributes.get(ATTR_IDUSER)));
			}
		}
		catch (SQLException e) {
			return new HookMethodOutput(String.format("No ha sido posible actualizar el número de followings para el usuario <%d>, debido a: ", attributes.get(ATTR_IDUSER), e.getMessage()));
		}
		finally {
			JDBCUtils.CloseQuietly(statement);
		}

		return HookMethodOutput.CONTINUE;
	}

	/**
	 * Retorna el resultado de la operación de actualización de followers.
	 * 
	 * @param connection Conexión con transacción abierta contra el subsistema.
	 * @param dataItem Datos de la operación.
	 * 
	 * @return el resultado de la operación de actualización de followers.
	 */
	private HookMethodOutput updateFollowers(Connection connection, DataItem dataItem) {

		NavigableMap<String, Object> attributes = dataItem.getAttributes();

		PreparedStatement statement = null;

		// Actualizar contador de los followings del usuario.
		try {

			long numFollowers = getNumFollowers(connection, ((Number)attributes.get(ATTR_IDFOLLOWEDUSER)).longValue());

			statement = connection.prepareStatement(UPDATE_FOLLOWERS);

			JDBCUtils.setObject(statement, 1, numFollowers, java.sql.Types.NUMERIC);

			Long milliseconds = attributes.get(ATTR_BIRTH) != null ? ((Number)attributes.get(ATTR_BIRTH)).longValue() : null;
			JDBCUtils.setObject(statement, 2, milliseconds == null ? null : new Timestamp(milliseconds), java.sql.Types.TIMESTAMP);

			milliseconds = attributes.get(ATTR_MODIFIED) != null ? ((Number)attributes.get(ATTR_MODIFIED)).longValue() : null;
			JDBCUtils.setObject(statement, 3, milliseconds == null ? null : new Timestamp(milliseconds), java.sql.Types.TIMESTAMP);

			JDBCUtils.setObject(statement, 4, attributes.get(ATTR_IDFOLLOWEDUSER), java.sql.Types.NUMERIC);

			if (statement.executeUpdate() != 1) {
				return new HookMethodOutput(String.format("No ha sido posible actualizar el número de followers para el usuario <%d>.", attributes.get(ATTR_IDFOLLOWEDUSER)));
			}
		}
		catch (SQLException e) {
			return new HookMethodOutput(String.format("No ha sido posible actualizar el número de followers para el usuario <%d>, debido a: ", attributes.get(ATTR_IDFOLLOWEDUSER), e.getMessage()));
		}
		finally {
			JDBCUtils.CloseQuietly(statement);
		}

		return HookMethodOutput.CONTINUE;
	}
}
