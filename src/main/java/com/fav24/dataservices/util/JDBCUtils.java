package com.fav24.dataservices.util;

import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fav24.dataservices.DataServicesContext;


/**
 * Conjunto de utilidades JDBC.
 */
public class JDBCUtils {

	public static final int DB_PRODUCT_POSTGRESQL = 0;
	public static final int DB_PRODUCT_MYSQL = 1;
	public static final int DB_PRODUCT_MARIADB = DB_PRODUCT_MYSQL;
	public static final int DB_PRODUCT_HSQL = 2;
	public static final int DB_PRODUCT_ORACLE = 3;

	public static final String HOST = "host";
	public static final String PORT = "port";
	public static final String DATABASE = "database";
	public static final String DATABASE_VERSION = "databaseVersion";
	public static final String PATH = "path";
	public static final String SCHEMA = "schema";
	public static final String CATALOG = "catalog";
	public static final String MAX_CONNECTIONS = "maxConnections";
	public static final String USER = "user";
	public static final String PASSWORD = "password";
	public static final String URL = "url";


	/**
	 * Retorna un mapa de atributos con la información de la conexión indicada.
	 * 
	 * @param connection Conexión de la que se desea obtener la información.
	 * 
	 * @return un mapa de atributos con la información de la conexión indicada.
	 * 
	 * @throws SQLException En caso de no poder obtener la información de la conexión.
	 * @throws MalformedURLException En caso de no poder parsear la URL de conexión.
	 */
	public static Map<String, String> getConnectionInformation(Connection connection) throws SQLException, MalformedURLException {

		Map<String, String> dataSourceAttributes = getURLAttributes(connection.getMetaData().getURL());

		dataSourceAttributes.put(DATABASE, connection.getMetaData().getDatabaseProductName());
		dataSourceAttributes.put(DATABASE_VERSION, connection.getMetaData().getDatabaseProductVersion());
		dataSourceAttributes.put(MAX_CONNECTIONS, String.valueOf(connection.getMetaData().getMaxConnections()));
		dataSourceAttributes.put(USER, connection.getMetaData().getUserName());

		String path = dataSourceAttributes.get(PATH);

		if (path != null && path.length() > 0) {
			ResultSet schemas = connection.getMetaData().getSchemas();
			if (schemas.first()) {
				do {

					String schema = schemas.getString("TABLE_SCHEM");

					if (schemas.wasNull()) {
						schema = null;
					}

					if (schema != null && path.contains(schema)) {

						dataSourceAttributes.put(SCHEMA, schema);

						break;
					}

				} while(schemas.next());
			}

			ResultSet catalogs = connection.getMetaData().getCatalogs();
			if (catalogs.first()) {
				do {

					String catalog = catalogs.getString("TABLE_CAT");

					if (catalogs.wasNull()) {
						catalog = null;
					}

					if (catalog != null && path.contains(catalog)) {

						dataSourceAttributes.put(CATALOG, catalog);

						break;
					}

				} while(schemas.next());
			}
		}

		return dataSourceAttributes;
	}

	/**
	 * Retorna el tipo de base de datos a partir de la metainformación de la conexión indicada.
	 * 
	 * @param connection Conexión de la que se obtendrá la metainformación.
	 * 
	 * @return el tipo de base de datos.
	 * 
	 * @throws SQLException
	 */
	public static int getProduct(Connection connection) throws SQLException {

		String databaseProductName = connection.getMetaData().getDatabaseProductName().toLowerCase();

		if (databaseProductName.indexOf("mysql") != -1) {
			return DB_PRODUCT_MYSQL;
		}

		if (databaseProductName.indexOf("postgres") != -1) {
			return DB_PRODUCT_POSTGRESQL;
		}

		if (databaseProductName.indexOf("hsql") != -1) {
			return DB_PRODUCT_HSQL;
		}

		if (databaseProductName.indexOf("oracle") != -1) {
			return DB_PRODUCT_ORACLE;
		}

		return DB_PRODUCT_MYSQL;
	}

	/**
	 * Retorna el tipo de base de datos a partir del nombre completo de la clase específica que implementa la intarfaz #javax.sql.DataSource.
	 * 
	 * @param dataSourceClassName Nombdre completo de la clase específica que implementa la intarfaz #javax.sql.DataSource.
	 * 
	 * @return el tipo de base de datos.
	 */
	public static int getProduct(String dataSourceClassName) {

		dataSourceClassName = dataSourceClassName.toLowerCase();

		if (dataSourceClassName.indexOf("mysql") != -1) {
			return DB_PRODUCT_MYSQL;
		}

		if (dataSourceClassName.indexOf("postgres") != -1) {
			return DB_PRODUCT_POSTGRESQL;
		}

		if (dataSourceClassName.indexOf("hsql") != -1) {
			return DB_PRODUCT_HSQL;
		}

		if (dataSourceClassName.indexOf("oracle") != -1) {
			return DB_PRODUCT_ORACLE;
		}

		return DB_PRODUCT_MYSQL;
	}

	/**
	 * Retorna la fecha y hora actual de la fuente de datos a la que hace referencia la conexión indicada.
	 * 
	 * @param connection Conexión de la que se desea obtener la información.
	 * 
	 * @return la fecha y hora actual de la fuente de datos a la que hace referencia la conexión indicada.
	 * 
	 * @throws SQLException En caso de no poder obtener la información de la conexión.
	 *  
	 *  Esta función es válida para: MySQL, MariaDB, PostgreSQL, Oracle y hSQL.
	 *  No és válida para: SQLServer.
	 */
	public static Long getConnectionDateTime(Connection connection) throws SQLException {

		String currentTimestampStatement = null;
		Date currentTimestamp;

		switch(getProduct(connection)) {

		case DB_PRODUCT_HSQL:
			currentTimestampStatement = "CALL CURRENT_TIMESTAMP AT TIME ZONE INTERVAL '00:00' HOUR TO MINUTE;";
			break;

		case DB_PRODUCT_MYSQL:
			currentTimestampStatement = "SELECT CURRENT_TIMESTAMP;";
			break;

		case DB_PRODUCT_ORACLE:
			currentTimestampStatement = "SELECT CURRENT_TIMESTAMP FROM DUAL;";
			break;

		case DB_PRODUCT_POSTGRESQL:
			currentTimestampStatement = "SELECT CURRENT_TIMESTAMP;";
			break;

		default:
			return null;
		}

		Statement statement = null;
		ResultSet resultSet = null;

		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(currentTimestampStatement);

			if (resultSet.next())
				currentTimestamp = resultSet.getTimestamp(1, DataServicesContext.MAIN_CALENDAR);
			else
				currentTimestamp = null;

		} catch (SQLException e) {
			throw e;
		}
		finally {
			CloseQuietly(resultSet);
			CloseQuietly(statement);
		}

		return currentTimestamp == null ? null : currentTimestamp.getTime();
	}

	/**
	 * Retorna un mapa con los atributos contenidos en la URL.
	 * 
	 * @param url URL de la que se obtendrán los atributos.
	 * 
	 * @return un mapa con los atributos contenidos en la URL.
	 */
	public static Map<String, String> getURLAttributes(String url) {

		Map<String, String> urlAttributes = new HashMap<String, String>();

		final Pattern p = Pattern.compile("^jdbc:(\\w+)://((\\w+)(:(\\w*))?@)?([^/:]+)(:(\\d+))?(/(\\w+))?");
		final Matcher m = p.matcher(url);

		urlAttributes.put(URL, url);

		if (m.find()) {
			urlAttributes.put(DATABASE, m.group(1));
			urlAttributes.put(USER, m.group(3));
			urlAttributes.put(PASSWORD, m.group(5));
			urlAttributes.put(HOST, m.group(6));
			urlAttributes.put(PORT, m.group(8));
			urlAttributes.put(PATH, m.group(10));
		}

		return urlAttributes;
	}

	/**
	 * Cierra la sentencia indicada, de forma silenciosa.
	 * 
	 * Nota: en caso de ser <code>null</code>, no hace nada.
	 * 
	 * @param statement sentencia a cerrar.
	 */
	public static void CloseQuietly(Statement statement) {

		try {

			if (statement != null) {
				statement.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Cierra el set de resultados indicado, de forma silenciosa.
	 * 
	 * Nota: en caso de ser <code>null</code>, no hace nada.
	 * 
	 * @param resultSet Set de resultados a cerrar.
	 */
	public static void CloseQuietly(ResultSet resultSet) {

		try {

			if (resultSet != null) {
				resultSet.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Cierra la conexión indicada, de forma silenciosa.
	 * 
	 * Nota: en caso de ser <code>null</code>, no hace nada.
	 * 
	 * @param connection Conexión a cerrar.
	 */
	public static void CloseQuietly(Connection connection) {

		try {

			if (connection != null) {
				connection.close();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retorna true o false en función de si la excepción indicada hace o no referencia a 
	 * una violación de una clave primária o un índice único.
	 * 
	 * @param product Identificador del producto.
	 * @param e Excepción a verificar.
	 * 
	 * @return true o false en función de si la excepción indicada hace o no referencia a 
	 * una violación de una clave primária o un índice único.
	 */
	public static boolean IsIntegrityConstraintViolation(int product, SQLException e) {

		if (e.getSQLState().startsWith("23")) {

			switch(product) {

			case DB_PRODUCT_MYSQL:

				switch(e.getErrorCode()) {

				case 1062: // Registro duplicado.
					return true;
				case 1452: // Foreing key. Aun no existe en registro referenciado.
					return true;
				default:
					return false;
				}
			}

			return true;
		}

		return false;
	}
}
