import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.AbstractList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.fav24.dataservices.domain.generic.*;
import com.fav24.dataservices.domain.policy.AccessPolicy;
import com.fav24.dataservices.domain.policy.EntityAccessPolicy;
import com.fav24.dataservices.domain.policy.EntityAccessPolicy.OperationType;
import com.fav24.dataservices.service.hook.GenericServiceHook;
import com.fav24.dataservices.util.JDBCUtils;


public class UserLogin implements GenericServiceHook {


    private static final String SELECT_USER_BY_MAIL = "SELECT `idUser`, `sessionToken`, `idFavouriteTeam` , `userName` , `email` , `name` , `photo` , " +
            "`csys_birth` , `csys_modified` , `csys_revision` , `csys_deleted` " +
            "FROM `shooter`.`User` " +
            "WHERE `email` = ? AND `password` = ? AND `csys_deleted` IS NULL";

    private static final String SELECT_USER_BY_USERNAME = "SELECT `idUser`, `sessionToken`, `idFavouriteTeam`  , `userName` , `email` , `name` , `photo` , " +
            "`csys_birth` , `csys_modified` , `csys_revision` , `csys_deleted` " +
            "FROM `shooter`.`User` " +
            "WHERE `userName` = ? AND `password` = ? AND `csys_deleted` IS NULL";

    private static final String ATTR_PASSWORD = "password";
    private static final String ATTR_USERNAME = "userName";
    private static final String ATTR_MAIL = "email";

    private static final String ATTR_IDUSER = "idUser";
    private static final String ATTR_IDFAVOURITETEAM = "idFavouriteTeam";
    private static final String ATTR_PHOTO = "photo";
    private static final String ATTR_SESSIONTOKEN = "sessionToken";
    private static final String ATTR_NAME = "name";


    private static final String ATTR_BIRTH = "birth";
    private static final String ATTR_MODIFIED = "modified";
    private static final String ATTR_DELETED = "deleted";
    private static final String ATTR_REVISION = "revision";



	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAlias() {
		return "UserLogin";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {

		StringBuilder description = new StringBuilder();

		description.append("<p>Realiza el Login de un usuario.</p>");
		description.append("<p>Entrada.</p>");
		description.append("<ul>");
		description.append("<li>UserName + Password </li>");
		description.append("<li>Email + Password </li>");
		description.append("</ul>");
		description.append("<p>Salida.</p>");
		description.append("<ul>");
		description.append("<li>Entidad User</li>");
		description.append("<li>Error</li>");
		description.append("</ul>");

		return description.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> HookMethodOutput requestBegin(T connection, AccessPolicy accessPolicy, Generic generic) {

        try{

            int numberOfOperations = generic.getOperations().size();

            if ( numberOfOperations != 1){
                return new HookMethodOutput("En esta llamada debe ejecutarse UNA y solamente UNA operacion y la petición contiene  " + numberOfOperations + " operaciones");
            }

            if (generic.getOperations().get(0).getMetadata().getKey().size() != 2 ){
                return new HookMethodOutput("Esta llamada solo admite acceso por key doble");
            }

            String identifierName = generic.getOperations().get(0).getMetadata().getKey().get(0).getName();
            String identifierValue = generic.getOperations().get(0).getMetadata().getKey().get(0).getValue().toString();

            String passwordName = generic.getOperations().get(0).getMetadata().getKey().get(1).getName();
            String passwordValue = generic.getOperations().get(0).getMetadata().getKey().get(1).getValue().toString();



            if ( !ATTR_USERNAME.equals(identifierName) && !ATTR_MAIL.equals(identifierName) ){
                return new HookMethodOutput("Esta llamada como <b>identificador</b> el email o el <b>userName</b> y se ha enviado: <b>"  + identifierName + "</b>");
            }

            if ( identifierValue.length() < 3  ){
                return new HookMethodOutput("El identificador ha de tener almenos 3 carácteres de longitud. El identificador que enviado (" +identifierValue+" ) tiene "  + identifierValue.length());
            }

            if ( ATTR_USERNAME.equals(identifierName) && identifierValue.length() > 20  ){
                return new HookMethodOutput("Los userNames tienen como máximo 20 carácteres de longitud. El identificador que enviado (" +identifierValue+" ) tiene "  + identifierValue.length());
            }

            if ( !ATTR_PASSWORD.equals(passwordName) ){
                return new HookMethodOutput("No se ha indicado un password. En vez de password se ha enviado : " + passwordName);
            }

            if ( passwordValue.length() < 6 &&  passwordValue.length() > 20){
                return new HookMethodOutput("El password ha de tener entre 6 y 20 carácteres. El password enviado tiene  : " + passwordValue.length() + " carácteres");
            }

        }catch(Exception e){
            return new HookMethodOutput("La llamada tiene un formato incorrecto");
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
        Connection sqlConnection = (Connection) connection;
        Timestamp now = new Timestamp(System.currentTimeMillis());
        now.setNanos(0); // Para evitar problemas de compatibilidad entre la escritura en DB y la lectura o selección.

        NavigableMap<String, Object> existingUser = null;

            try {
                existingUser = loginUser(sqlConnection, operation.getMetadata());
            } catch (SQLException e) {
                operation.getMetadata().setItems(0L);
                operation.getMetadata().setTotalItems(operation.getMetadata().getItems());
                return new HookMethodOutput(String.format("No ha sido posible hacer login  debido a: %s", e.getMessage()));
            }

            if ( existingUser == null ){
                return new HookMethodOutput("No se ha encontraro ningún usuario con esas credenciales");
            } else {
                operation.getData().get(0).setAttributes(existingUser);
            }



        operation.getMetadata().setItems((long)operation.getData().size());
        operation.getMetadata().setTotalItems(operation.getMetadata().getItems());

		return HookMethodOutput.CONTINUE;
	}

    /**
     * Devuelve el usuario, en caso de existir.
     *
     * @param connection Conexión con transacción abierta contra el subsistema.
     * @param metadata usuario a buscar
     *
     * @return  el usuario, en caso de existir.
     */
    private NavigableMap<String, Object> loginUser(Connection connection, Metadata metadata) throws SQLException {

        NavigableMap<String, Object> attributes = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;


        AbstractList<KeyItem> keys = metadata.getKey();

        KeyItem identifier = keys.get(0);
        KeyItem password = keys.get(1);

        String identifierName = identifier.getName();
        String identifierValue = (String) identifier.getValue();

        String passwordValue = (String) password.getValue();

        try {

            if ( ATTR_MAIL.equals(identifierName)  ){
                statement = connection.prepareStatement(SELECT_USER_BY_MAIL);
            }else{
                statement = connection.prepareStatement(SELECT_USER_BY_USERNAME);
            }

            JDBCUtils.setObject(statement, 1, identifierValue, java.sql.Types.VARCHAR);
            JDBCUtils.setObject(statement, 2, passwordValue, java.sql.Types.VARCHAR);


            resultSet = statement.executeQuery();
            if (resultSet.first()) {

                attributes = new TreeMap<String, Object>();

                attributes.put(ATTR_IDUSER, JDBCUtils.getObject(resultSet, 1, Long.class));
                attributes.put(ATTR_SESSIONTOKEN, JDBCUtils.getObject(resultSet, 2, String.class));
                attributes.put(ATTR_IDFAVOURITETEAM, JDBCUtils.getObject(resultSet, 3, Long.class));

                attributes.put(ATTR_USERNAME, JDBCUtils.getObject(resultSet, 4, String.class));
                attributes.put(ATTR_MAIL, JDBCUtils.getObject(resultSet, 5, String.class));
                attributes.put(ATTR_NAME, JDBCUtils.getObject(resultSet, 6, String.class));
                attributes.put(ATTR_PHOTO, JDBCUtils.getObject(resultSet, 7, String.class));


                Timestamp timestamp = JDBCUtils.getObject(resultSet, 8, Timestamp.class);
                if (timestamp == null) {
                    attributes.put(ATTR_BIRTH, null);
                }
                else {
                    timestamp.setNanos(0);
                    attributes.put(ATTR_BIRTH, timestamp.getTime());
                }

                timestamp = JDBCUtils.getObject(resultSet, 9, Timestamp.class);
                if (timestamp == null) {
                    attributes.put(ATTR_MODIFIED, null);
                }
                else {
                    timestamp.setNanos(0);
                    attributes.put(ATTR_MODIFIED, timestamp.getTime());
                }

                timestamp = JDBCUtils.getObject(resultSet, 10, Timestamp.class);
                if (timestamp == null) {
                    attributes.put(ATTR_DELETED, null);
                }
                else {
                    timestamp.setNanos(0);
                    attributes.put(ATTR_DELETED, timestamp.getTime());
                }

                attributes.put(ATTR_REVISION, JDBCUtils.getObject(resultSet, 11, Long.class));
                if (resultSet.wasNull()) {
                    attributes.put(ATTR_REVISION, null);
                }

                if (resultSet.next()) {
                    return null;
                }
            }
        }
        catch (SQLException e) {
            throw e;
        }
        finally {
            JDBCUtils.CloseQuietly(resultSet);
            JDBCUtils.CloseQuietly(statement);
        }





        return attributes;
    }


	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> HookMethodOutput operationEnd(T connection, EntityAccessPolicy entityAccessPolicy, Operation operation) {

		return HookMethodOutput.CONTINUE;
	}
}
