import com.fav24.dataservices.domain.generic.Generic;
import com.fav24.dataservices.domain.generic.KeyItem;
import com.fav24.dataservices.domain.generic.Metadata;
import com.fav24.dataservices.domain.generic.Operation;
import com.fav24.dataservices.domain.policy.AccessPolicy;
import com.fav24.dataservices.domain.policy.EntityAccessPolicy;
import com.fav24.dataservices.domain.policy.EntityData;
import com.fav24.dataservices.domain.policy.EntityDataAttribute;
import com.fav24.dataservices.service.hook.GenericServiceHook;
import com.fav24.dataservices.util.JDBCUtils;

import java.sql.*;
import java.util.AbstractList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;


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
     *
     * Este metodo valida que se cumplen todas las precondiciones antes de empezar a resolver las operaciones.
     *
     * Si alguna precondicion no se cumple devuelve su respectivo error.
     *
	 */
	@Override
	public <T> HookMethodOutput requestBegin(T connection, AccessPolicy accessPolicy, Generic generic) {

        try{

            int numberOfOperations = generic.getOperations().size();

            if ( numberOfOperations != 1){
                return new HookMethodOutput("This call should run one and only one operation and the request contains  " + numberOfOperations + " operations");
            }

            if (generic.getOperations().get(0).getMetadata().getKey().size() != 2 ){
                return new HookMethodOutput("This request only supports dual key access");
            }

            String identifierName = generic.getOperations().get(0).getMetadata().getKey().get(0).getName();
            String identifierValue = generic.getOperations().get(0).getMetadata().getKey().get(0).getValue().toString();

            String passwordName = generic.getOperations().get(0).getMetadata().getKey().get(1).getName();
            String passwordValue = generic.getOperations().get(0).getMetadata().getKey().get(1).getValue().toString();



            if ( !ATTR_USERNAME.equals(identifierName) && !ATTR_MAIL.equals(identifierName) ){
                return new HookMethodOutput("This request must have as identifier  <b>email</b> or < b> userName < / b >. The identifier submitted was : <b > "  + identifierName + "</b>");
            }

            if ( identifierValue.length() < 3  ){
                return new HookMethodOutput("The identifier must be at least 3 characters. The identifier submitted was (" +identifierValue+" ) and had "  + identifierValue.length());
            }

            if ( ATTR_USERNAME.equals(identifierName) && identifierValue.length() > 20  ){
                return new HookMethodOutput("userNames have no more than 20 characters. The identifier submitted was (" +identifierValue+" ) and had "  + identifierValue.length());
            }

            if ( !ATTR_PASSWORD.equals(passwordName) ){
                return new HookMethodOutput("No password was specified. Instead password, was sent : " + passwordName);
            }

            if ( passwordValue.length() < 6 &&  passwordValue.length() > 20){
                return new HookMethodOutput("The password must be between 6 and 20 characters. The password submitted has  : " + passwordValue.length() + " characters");
            }

        }catch(Exception e){
            return new HookMethodOutput("Invalid Request");
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
     *
     * Resuelve la operación validando teniendo en cuenta las restricciones de la politica en cuestión
     *
     *
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
                return new HookMethodOutput(String.format("Login not possible because : %s", e.getMessage()));
            }

            if ( existingUser == null ){
                return new HookMethodOutput("User Not Found");
            } else {


                EntityData entityData = entityAccessPolicy.getData();
                AbstractList<EntityDataAttribute>  entityDataAttributes = entityData.getData();

               //Solo se devuelven los campos que estan autorizados por la politica y que el usuario ha pedido
                for(Map.Entry entry : operation.getData().get(0).getAttributes().entrySet()){

                    String key = (String) entry.getKey();

                    for(EntityDataAttribute entityDataAttribute : entityDataAttributes){
                        if ( key.equals(entityDataAttribute.getAlias()) && (EntityDataAttribute.Direction.OUTPUT.equals( entityDataAttribute.getDirection()) || EntityDataAttribute.Direction.BOTH.equals(entityDataAttribute.getDirection())) ){
                                entry.setValue(existingUser.get(key));
                        }
                    }

                }
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

                attributes.put(ATTR_REVISION, JDBCUtils.getObject(resultSet, 10, Long.class));
                if (resultSet.wasNull()) {
                    attributes.put(ATTR_REVISION, null);
                }

                timestamp = JDBCUtils.getObject(resultSet, 11, Timestamp.class);
                if (timestamp == null) {
                    attributes.put(ATTR_DELETED, null);
                }
                else {
                    timestamp.setNanos(0);
                    attributes.put(ATTR_DELETED, timestamp.getTime());
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
