package com.fav24.dataservices.domain.datasource;

import java.sql.Connection;


/**
 * Configuración de las características de una conexión establecida por el pool de conexiones.
 */
public class ConnectionConfiguration
{
	/**
	 * Enumeración de los tipos de aislamiento entre transacciones que existen. 
	 */
	public enum TransactionIsolationType {

		SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE, "SERIALIZABLE"),
		REPEATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ, "REPEATABLE_READ"),
		READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED, "READ_COMMITTED"),
		READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED, "READ_UNCOMMITTED"),
		NONE(Connection.TRANSACTION_NONE, "NONE");

		private final int transactionIsolationType;
		private final String transactionIsolationTypeName;
		private final String transactionIsolationTypeISOName;

		/**
		 * Constructor privado del tipo de aislamiento.
		 * 
		 * @param transactionIsolationType Entero aue identifica el tipo de aislamiento.
		 * @param transactionIsolationTypeName Cadena de texto aue identifica el tipo de aislamiento.
		 */
		TransactionIsolationType(int transactionIsolationType, String transactionIsolationTypeName) {

			this.transactionIsolationType = transactionIsolationType;
			this.transactionIsolationTypeName = transactionIsolationTypeName;

			switch(transactionIsolationType) {

			case Connection.TRANSACTION_SERIALIZABLE:
				this.transactionIsolationTypeISOName = "TRANSACTION_SERIALIZABLE";
				break;
			case Connection.TRANSACTION_REPEATABLE_READ:
				this.transactionIsolationTypeISOName = "TRANSACTION_REPEATABLE_READ";
				break;
			case Connection.TRANSACTION_READ_COMMITTED:
				this.transactionIsolationTypeISOName = "TRANSACTION_READ_COMMITTED";
				break;
			case Connection.TRANSACTION_READ_UNCOMMITTED:
				this.transactionIsolationTypeISOName = "TRANSACTION_READ_UNCOMMITTED";
				break;
			case Connection.TRANSACTION_NONE:
				this.transactionIsolationTypeISOName = "TRANSACTION_NONE";
				break;
			default:
				this.transactionIsolationTypeISOName = null;
			}
		}

		/**
		 * Retorna el entero que identifica este tipo de aislamiento.
		 * 
		 * @return el entero que identifica este tipo de aislamiento.
		 */
		public int getTransactionIsolationType() {
			return transactionIsolationType;
		}

		/**
		 * Retorna la cadena de texto que identifica este tipo de aislamiento.
		 * 
		 * @return la cadena de texto que identifica este tipo de aislamiento.
		 */
		public String getTransactionIsolationTypeName() {
			return transactionIsolationTypeName;
		}

		/**
		 * Retorna la cadena de texto en formato ISO que identifica este tipo de aislamiento.
		 * 
		 * @return la cadena de texto en formato ISO que identifica este tipo de aislamiento.
		 */
		public String getTransactionIsolationTypeISOName() {
			return transactionIsolationTypeISOName;
		}

		/**
		 * Retorna el tipo de aislamiento a partir de la cadena de texto indicada.
		 * 
		 * @param text Cadena de texto a partir de la que se deduce el tipo de aislamiento.
		 * 
		 * @return el tipo de aislamiento a partir de la cadena de texto indicada.
		 */
		public static TransactionIsolationType fromString(String text) {
			if (text != null) {
				for (TransactionIsolationType transactionIsolationType : TransactionIsolationType.values()) {
					if (text.equalsIgnoreCase(transactionIsolationType.transactionIsolationTypeName)) {
						return transactionIsolationType;
					}
				}
			}
			return null;
		}
	}


	private Boolean autoCommit;
	private Long timeout;
	private TransactionIsolationType transactionIsolation;
	private Boolean jdbc4ConnectionTest;
	private String initSql;
	private String TestQuery;

	/**
	 * Constructor por defecto.
	 */
	public ConnectionConfiguration() {

		this.autoCommit = null;
		this.timeout = null;
		this.transactionIsolation = null;
		this.jdbc4ConnectionTest = null;
		this.initSql = null;
		this.TestQuery = null;
	}

	/**
	 * Retorna true o false en función de si la conexión tendrá o no activada la confirmación automática de operaciones.
	 * 
	 * Nota: Esta propiedad controla el comportamiento del auto-commit de las conexiones al devolverlas al pool.
	 * 
	 * @return true o false en función de si la conexión tendrá o no activada la confirmación automática de operaciones.
	 */
	public Boolean getAutoCommit() {
		return autoCommit;
	}

	/**
	 * Asigna true o false en función de si la conexión tendrá o no activada la confirmación automática de operaciones.
	 * 
	 * Nota: Esta propiedad controla el comportamiento del auto-commit de las conexiones al devolverlas al pool.
	 * 
	 * @param autoCommit True o false en función de si la conexión tendrá o no activada la confirmación automática de operaciones.
	 */
	public void setAutoCommit(Boolean autoCommit) {
		this.autoCommit = autoCommit;
	}

	/**
	 * Retorna el tiempo máximo en milisegundos que un cliente esperará una conexión del pool.
	 * 
	 * 	Nota: Esta propiedad controla el tiempo máximo en milisegundos que un cliente esperará una conexión del pool.
	 *        Si este tiempo se excede sin que haya una conexión disponible, se producirá una excepción de SQL.
	 *         
	 * @return el tiempo máximo en milisegundos que un cliente esperará una conexión del pool.
	 */
	public Long getTimeout() {
		return timeout;
	}

	/**
	 * Asigna el tiempo máximo en milisegundos que un cliente esperará una conexión del pool.
	 * 
	 * 	Nota: Esta propiedad controla el tiempo máximo en milisegundos que un cliente esperará una conexión del pool.
	 *        Si este tiempo se excede sin que haya una conexión disponible, se producirá una excepción de SQL.
	 *         
	 * @param timeout El tiempo máximo en milisegundos a asignar.
	 */
	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}

	/**
	 * Retorna el nivel de aislamiento de las transacciones por defecto, para las conexiones retornadas por el pool.
	 *  
	 * @return el nivel de aislamiento de las transacciones por defecto, para las conexiones retornadas por el pool.
	 */
	public TransactionIsolationType getTransactionIsolation() {
		return transactionIsolation;
	}

	/**
	 * Asigna el nivel de aislamiento de las transacciones por defecto, para las conexiones retornadas por el pool.
	 *  
	 * Nota: Si no se especifica esta propiedad, se utiliza el nivel de aislamiento de transacción por defecto definido por el controlador JDBC. 
	 * Por lo general, se debe utilizar el nivel de aislamiento de transacción predeterminado por el controlador JDBC. 
	 * Utilice esta propiedad si tiene requisitos específicos de aislamiento comunes para todas las consultas, 
	 * de lo contrario simplemente establecer el nivel de aislamiento de forma manual al crear o preparar los statements.
	 * 
	 * @param transactionIsolation El nivel de aislamiento a asignar.
	 */
	public void setTransactionIsolation(TransactionIsolationType transactionIsolation) {
		this.transactionIsolation = transactionIsolation;
	}

	/**
	 * Retorna true o false en función de si se debe o no utilizar el método JDBC4 Connection.isValid() para comprobar que la conexión todavía está viva.
	 * 
	 * Nota: Este valor es mutuamente excluyente con la propiedad {@linkplain #getTestQuery}. Este modo de comprobación es siempre preferible a <TestQuery>.
	 * 
	 * @return true o false en función de si se debe o no utilizar el método JDBC4 Connection.isValid() para comprobar que la conexión todavía está viva.
	 */
	public Boolean getJdbc4ConnectionTest() {
		return jdbc4ConnectionTest;
	}

	/**
	 * Asigna true o false en función de si se debe o no utilizar el método JDBC4 Connection.isValid() para comprobar que la conexión todavía está viva.
	 * 
	 * Nota: Este valor es mutuamente excluyente con la propiedad {@linkplain #getTestQuery}. Este modo de comprobación es siempre preferible a <TestQuery>.
	 * 
	 * @param jdbc4ConnectionTest true o false en función de si se debe o no utilizar el método JDBC4 Connection.isValid() para comprobar que la conexión todavía está viva.
	 */
	public void setJdbc4ConnectionTest(Boolean jdbc4ConnectionTest) {
		this.jdbc4ConnectionTest = jdbc4ConnectionTest;
	}

	/**
	 * Retorna una sentencia SQL que se ejecutará después de cada nueva creación de la conexión antes de añadirla al pool.
	 *  
	 * @return una sentencia SQL que se ejecutará después de cada nueva creación de la conexión antes de añadirla al pool.
	 */
	public String getInitSql() {
		return initSql;
	}

	/**
	 * Asigna una sentencia SQL que se ejecutará después de cada nueva creación de la conexión antes de añadirla al pool.
	 *  
	 * Nota: Si la sentencia no es válida o se produce una excepción, se trata como un error de conexión y se seguirá la lógica de reintentos.
	 *  
	 * @param initSql Sentencia a asignar.
	 */
	public void setInitSql(String initSql) {
		this.initSql = initSql;
	}

	/**
	 * Retorna la sentencia SQL que chequea el estado de una conexión. 
	 * 
	 * Ésta es la consulta que se ejecutará justo antes de devolver una conexión del pool para validar que la conexión a la base de datos todavía está viva.
	 * Depende de cada base de datos y debe ser una consulta lo más eficiente posible (por ejemplo, "SELECT 1").
	 * Esta propiedad tiene efecto en caso que la propiedad {@linkplain #getJdbc4ConnectionTest()} esté desactivada.
	 * 
	 * @return la sentencia SQL que chequea el estado de una conexión. 
	 */
	public String getTestQuery() {
		return TestQuery;
	}

	/**
	 * Asigna la sentencia SQL que chequea el estado de una conexión. 
	 * 
	 * Ésta es la consulta que se ejecutará justo antes de devolver una conexión del pool para validar que la conexión a la base de datos todavía está viva.
	 * Depende de cada base de datos y debe ser una consulta lo más eficiente posible (por ejemplo, "SELECT 1").
	 * Esta propiedad tiene efecto en caso que la propiedad {@linkplain #getJdbc4ConnectionTest()} esté desactivada.
	 * 
	 * @param testQuery La sentencia SQL a asignar. 
	 */
	public void setTestQuery(String testQuery) {
		TestQuery = testQuery;
	}
}
