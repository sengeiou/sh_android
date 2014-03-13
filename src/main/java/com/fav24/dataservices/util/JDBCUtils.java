package com.fav24.dataservices.util;

import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Conjunto de utilidades JDBC.
 */
public class JDBCUtils {

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
}
