package com.fav24.dataservices.domain.datasource;



public class DataSourceConfiguration
{
	private String name;
	private ConnectionPoolConfiguration connectionPoolConfiguration;
	private String host;
	private Integer port;
	private String databaseName;
	private String userName;
	private String password;


	/**
	 * Constructor sin parámetros.
	 */
	public DataSourceConfiguration() {

		this.connectionPoolConfiguration = null;
		this.host = null;
		this.port = null;
		this.databaseName = null;
		this.userName = null;
		this.password = null;
	}

	/**
	 * Retorna el nombre de la fuente de datos.
	 * 
	 * @return el nombre de la fuente de datos.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Asigna el nombre de la fuente de datos.
	 * 
	 * @param name El nombre de la fuente de datos.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retorna la configuración del pool de conexiones asociado a esta configuración de fuente de datos.
	 * 
	 * @return la configuración del pool de conexiones asociado a esta configuración de fuente de datos.
	 */
	public ConnectionPoolConfiguration getConnectionPoolConfiguration() {
		return connectionPoolConfiguration;
	}

	/**
	 * Asigna la configuración del pool de conexiones asociado a esta configuración de fuente de datos.
	 * 
	 * @param connectionPoolConfiguration Configuración del pool a asignar.
	 */
	public void setConnectionPoolConfiguration(ConnectionPoolConfiguration connectionPoolConfiguration) {
		this.connectionPoolConfiguration = connectionPoolConfiguration;
	}
	/*

	<xsd:element name="DatabaseName" type="xsd:string" minOccurs="1" maxOccurs="1"/>

	*/
	/**
	 * Retorna el servidor donde está ubicada la base de datos.
	 * 
	 * @return el servidor donde está ubicada la base de datos.
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Asigna el servidor donde está ubicada la base de datos.
	 * 
	 * @param host El servidor a asignar.
	 */
	public void setHost(String host) {
		this.host = host;
	}
	
	/**
	 * Retorna el puerto del servidor donde está ubicado el listener de la base de datos.
	 * 
	 * @return el puerto del servidor donde está ubicado el listener de la base de datos.
	 */
	public Integer getPort() {
		return port;
	}
	
	/**
	 * Asigna puerto del servidor donde está ubicado el listener de la base de datos.
	 * 
	 * @param port El puerto del servidor a asignar.
	 */
	public void setPort(Integer port) {
		this.port = port;
	}
	
	/**
	 * Retorna el nombre de la base de datos.
	 * 
	 * @return el nombre de la base de datos.
	 */
	public String getDatabaseName() {
		return databaseName;
	}
	
	/**
	 * Asigna el nombre de la base de datos.
	 * 
	 * @param databaseName El nombre de la base de datos a asignar.
	 */
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	/**
	 * Retorna el usuario de conexión a la base de datos.
	 * 
	 * @return el usuario de conexión a la base de datos.
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Asigna el usuario de conexión a la base de datos.
	 * 
	 * @param userName El usuario de conexión a la base de datos.
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Retorna la contraseña de conexión a la base de datos.
	 * 
	 * @return la contraseña de conexión a la base de datos.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Asigna la contraseña de conexión a la base de datos.
	 * 
	 * @param password La contraseña de conexión a la base de datos.
	 */
	public void setPassword(String password) {
		this.password = password;
	}
}
