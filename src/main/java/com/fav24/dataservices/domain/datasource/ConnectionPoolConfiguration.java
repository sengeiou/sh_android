package com.fav24.dataservices.domain.datasource;


/**
 * Configuración del pool de conexiones.
 */
public class ConnectionPoolConfiguration
{
	private ConnectionAcquire connectionAcquire;
	private ConnectionConfiguration connectionConfiguration;
	private String dataSourceClassName;
	private Long idleTimeout;
	private Long maxLifetime;
	private Integer minimumPoolSize;
	private Integer maximumPoolSize;


	/**
	 * Constructor sin parámetros.
	 */
	public ConnectionPoolConfiguration() {

		this.connectionAcquire = null;
		this.connectionConfiguration = null;
		this.dataSourceClassName = null;
	}

	/**
	 * Retorna las políticas de adquisición de nuevas conexiones del pool.
	 * 
	 * @return las políticas de adquisición de nuevas conexiones del pool.
	 */
	public ConnectionAcquire getConnectionAcquire() {
		return connectionAcquire;
	}

	/**
	 * Asigna las políticas de adquisición de nuevas conexiones del pool.
	 * 
	 * @param connectionAcquire Las políticas de adquisición a asignar.
	 */
	public void setConnectionAcquire(ConnectionAcquire connectionAcquire) {
		this.connectionAcquire = connectionAcquire;
	}

	/**
	 * Retorna la configuración de las conexiones que gestionará el pool.
	 * 
	 * @return la configuración de las conexiones que gestionará el pool.
	 */
	public ConnectionConfiguration getConnectionConfiguration() {
		return connectionConfiguration;
	}

	/**
	 * Asigna la configuración de las conexiones que gestionará el pool.
	 * 
	 * @param connectionConfiguration La configuración de las conexiones a asignar.
	 */
	public void setConnectionConfiguration(ConnectionConfiguration connectionConfiguration) {
		this.connectionConfiguration = connectionConfiguration;
	}

	/**
	 * Retorna el nombre de la clase DataSource proporcionada por el controlador JDBC.
	 * 
	 * p.e. com.mysql.jdbc.jdbc2.optional.MysqlDataSource para MySQL. 
	 * 
	 * Nota: Los DataSource <Note XA> no son compatibles. 
	 *       XA requiere un administrador de transacciones como p.e. bitronix.
	 *       
	 * @return el nombre de la clase DataSource proporcionada por el controlador JDBC.
	 */
	public String getDataSourceClassName() {
		return dataSourceClassName;
	}

	/**
	 * Asigna el nombre de la clase DataSource proporcionada por el controlador JDBC.
	 * 
	 * @param dataSourceClassName El nombre de la clase DataSource a asignar.
	 */
	public void setDataSourceClassName(String dataSourceClassName) {
		this.dataSourceClassName = dataSourceClassName;
	}

	/**
	 * Retorna el tiempo máximo en milisegundos que una conexión puede estar inactiva en el pool.
	 *  
	 * Nota: Una conexión se considera una inactiva, si ha estado sujeta a una variación máxima de 30 segundos, 
	 * y una variación media de 15 segundos. En cualquier otro caso, se considerará activa. 
	 * Un valor de 0 significa que las conexiones inactivas no se eliminarán del pool.
	 * 
	 * @return el tiempo máximo en milisegundos que una conexión puede estar inactiva en el pool.
	 */
	public Long getIdleTimeout() {
		return idleTimeout;
	}

	/**
	 * Asigna el tiempo máximo en milisegundos que una conexión puede estar inactiva en el pool.
	 *  
	 * Nota: Una conexión se considera una inactiva, si ha estado sujeta a una variación máxima de 30 segundos, 
	 * y una variación media de 15 segundos. En cualquier otro caso, se considerará activa. 
	 * Un valor de 0 significa que las conexiones inactivas no se eliminarán del pool.
	 * 
	 * @param idleTimeout El tiempo máximo en milisegundos que una conexión puede estar inactiva en el pool a asignar.
	 */
	public void setIdleTimeout(Long idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	/**
	 * Retorna la duración máxima de una conexión en el pool. Cuando una conexión llega a este tiempo de espera, incluso si se usa poco, será retirada del pool.
	 * 
	 * Nota: Una conexión en uso nunca se retirará, sólo cuando está inactiva va a ser eliminada.
	 * Se recomienda el establecimiento de este valor, y el uso de algo razonable como 30 minutos o 1 hora.
	 * Un valor de 0 indica que no hay tiempo de vida máximo (duración infinita), sujeto por supuesto a la configuración idleTimeout
	 * 
	 * @return la duración máxima de una conexión en el pool. Cuando una conexión llega a este tiempo de espera, incluso si se usa poco, será retirada del pool.
	 */
	public Long getMaxLifetime() {
		return maxLifetime;
	}

	/**
	 * Asigna la duración máxima de una conexión en el pool. Cuando una conexión llega a este tiempo de espera, incluso si se usa poco, será retirada del pool.
	 * 
	 * Nota: Una conexión en uso nunca se retirará, sólo cuando está inactiva va a ser eliminada.
	 * Se recomienda el establecimiento de este valor, y el uso de algo razonable como 30 minutos o 1 hora.
	 * Un valor de 0 indica que no hay tiempo de vida máximo (duración infinita), sujeto por supuesto a la configuración idleTimeout
	 * 
	 * @param maxLifetime La duración máxima de una conexión en el pool a asignar.
	 */
	public void setMaxLifetime(Long maxLifetime) {
		this.maxLifetime = maxLifetime;
	}

	/**
	 * Retorna el número mínimo de conexiones que el pool trata de mantener, incluyendo tanto inactivas como conexiones en-uso.
	 * 
	 * Nota: Si las conexiones se establecen por debajo de este valor, el pool hará un mayor esfuerzo para restaurar conexiones en uso de forma rápida y eficiente.
	 * 
	 * @return el número mínimo de conexiones que el pool trata de mantener, incluyendo tanto inactivas como conexiones en-uso.
	 */
	public Integer getMinimumPoolSize() {
		return minimumPoolSize;
	}

	/**
	 * Asigna el número mínimo de conexiones que el pool trata de mantener, incluyendo tanto inactivas como conexiones en-uso.
	 * 
	 * Nota: Si las conexiones se establecen por debajo de este valor, el pool hará un mayor esfuerzo para restaurar conexiones en uso de forma rápida y eficiente.
	 * 
	 * @param minimumPoolSize El número mínimo de conexiones que el pool trata de mantener.
	 */
	public void setMinimumPoolSize(Integer minimumPoolSize) {
		this.minimumPoolSize = minimumPoolSize;
	}

	/**
	 * Retorna el tamaño máximo que se permite al pool, tieniendo en cuenta tanto las conexiones en uso, como en reposo.
	 * 
	 * Nota: Básicamente, este valor determinará el número máximo de conexiones reales a la base de datos backend.
	 * Cuando el pool alcanza este valor, y no hay conexiones inactivas disponibles, la llamada a getConnection() se bloqueará durante un máximo de milisegundos 
	 * definido por el {@linkplain ConnectionConfiguration#getTimeout()} antes de tiempo de espera.
	 * 
	 * @return el tamaño máximo que se permite al pool, tieniendo en cuenta tanto las conexiones en uso, como en reposo.
	 */
	public Integer getMaximumPoolSize() {
		return maximumPoolSize;
	}

	/**
	 * Asigna el tamaño máximo que se permite al pool, tieniendo en cuenta tanto las conexiones en uso, como en reposo.
	 * 
	 * Nota: Básicamente, este valor determinará el número máximo de conexiones reales a la base de datos backend.
	 * Cuando el pool alcanza este valor, y no hay conexiones inactivas disponibles, la llamada a getConnection() se bloqueará durante un máximo de milisegundos 
	 * definido por el {@linkplain ConnectionConfiguration#getTimeout()} antes de tiempo de espera.
	 * 
	 * @param maximumPoolSize El tamaño máximo que se permite al pool a asignar.
	 */
	public void setMaximumPoolSize(Integer maximumPoolSize) {
		this.maximumPoolSize = maximumPoolSize;
	}
}
