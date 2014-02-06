package com.fav24.dataservices.service.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.fav24.dataservices.domain.Filter;
import com.fav24.dataservices.domain.FilterItem;
import com.fav24.dataservices.domain.Generic;
import com.fav24.dataservices.domain.KeyItem;
import com.fav24.dataservices.domain.Operation;
import com.fav24.dataservices.domain.Requestor;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.security.AccessPolicy;
import com.fav24.dataservices.security.EntityAccessPolicy;
import com.fav24.dataservices.security.EntityDataAttribute;
import com.fav24.dataservices.security.EntityDataAttribute.Direction;
import com.fav24.dataservices.security.EntityFilter;
import com.fav24.dataservices.security.EntityKey;
import com.fav24.dataservices.service.GenericService;


/**
 * Versión JDBC de la interfaz de servicio Generic. 
 * 
 * @author Fav24
 */
@Component
@Scope("prototype")
public class GenericServiceJDBC extends GenericServiceBasic {

	private class EntityJDBCInformation {

		private String name;
		private String catalog;
		private String schema;
		private Boolean isReadonly;
		private Map<String, Integer> dataFields;
		private Map<String, List<String>> keys;
		private Map<String, List<String>> indexes;
		private Map<String, Integer> keyFields;
		private Map<String, Integer> filterFields;
	}

	private static Map<String, EntityJDBCInformation> entitiesInformation;

	private TransactionTemplate transactionTemplate;
	private JdbcTemplate jdbcTemplate;

	/**
	 * Asigna la plantilla para la gestión de transacciones que usará este servicio.
	 * 
	 * @param transactionTemplate La plantilla para la gestión de transacciones a asignar.
	 */
	@Autowired
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	/**
	 * Asigna plantilla de acceso JDBC que usará este servicio.
	 * 
	 * @param jdbcTemplate La plantilla de acceso JDBC a asignar.
	 */
	@Autowired
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean startTransaction() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean endTransaction(boolean commit) {
		return true;
	}

	/**
	 * Retorna una cadena de texto con el conjunto de campos de datos.
	 * 
	 * @param entity Nombre de la entidad a la que pertenece la lista de datos.
	 * @param attributes Mapa de datos a resolver.
	 * @param columns Lista en donde se retornará el conjunto de columnas de datos en el mismo orden de resolución.
	 * 
	 * @return una cadena de texto con el conjunto de campos clave de la entidad indicada en FNC.
	 */
	private String getDataString(String entity, Map<String, Object> attributes, AbstractList<String> columns) throws ServerException {

		StringBuilder resultingData = new StringBuilder();

		if (attributes != null) {

			Iterator<String> attributeAliases = attributes.keySet().iterator();

			if (attributeAliases.hasNext()) {

				String column = AccessPolicy.getAttributeName(entity, attributeAliases.next());

				resultingData.append(column);

				if (columns != null) {
					columns.add(column);
				}

				while (attributeAliases.hasNext()) {

					column = AccessPolicy.getAttributeName(entity, attributeAliases.next());

					resultingData.append(',');
					resultingData.append(column);

					if (columns != null) {
						columns.add(column);
					}
				}
			}
		}
		else {
			resultingData.append("count(*)");
		}

		return resultingData.toString();
	}

	/**
	 * Retorna una cadena de texto con el conjunto de campos clave de la entidad indicada en FNC.
	 * 
	 * @param entity Nombre de la entidad a la que pertenece la lista de claves.
	 * @param keys Lista de claves a resolver.
	 * @param columns Lista en donde se retornará el conjunto de columnas clave en el mismo orden de resolución.
	 * @param values Lista en donde se retornará el conjunto de valores de las columnas clave en el mismo orden de resolución.
	 * 
	 * @return una cadena de texto con el conjunto de campos clave de la entidad indicada en FNC.
	 */
	private String getKeyString(String entity, AbstractList<KeyItem> keys, AbstractList<String> columns, AbstractList<Object> values) throws ServerException {

		StringBuilder resultingKey = new StringBuilder();

		KeyItem key = keys.get(0);

		String column = AccessPolicy.getAttributeName(entity, key.getName());

		resultingKey.append(column).append('=').append(key.getValue());

		if (columns != null) {
			columns.add(column);
		}

		if (values != null) {
			values.add(key.getValue());
		}

		for (int i=1; i<keys.size(); i++) {

			key = keys.get(i);

			resultingKey.append(" AND ");

			column = AccessPolicy.getAttributeName(entity, key.getName());
			resultingKey.append(column).append('=').append(key.getValue());

			if (columns != null) {
				columns.add(column);
			}

			if (values != null) {
				values.add(key.getValue());
			}
		}

		return resultingKey.toString();
	}

	/**
	 * Retorna una cadena de texto con el conjunto de campos de filtrado de la entidad indicada en FN parentizada.
	 * 
	 * @param entity Nombre de la entidad a la que pertenece la lista de campos filtrado.
	 * @param filterSet Conjunto de filtros a resolver.
	 * @param columns Lista en donde se retornará el conjunto de columnas de filtrado en el mismo orden de resolución.
	 * @param values Lista en donde se retornará el conjunto de valores de las columnas clave en el mismo orden de resolución.
	 * 
	 * @return una cadena de texto con el conjunto de campos de filtrado de la entidad indicada en FN parentizada.
	 */
	private String getFilterString(String entity, FilterItem filter, AbstractList<String> columns, AbstractList<Object> values) throws ServerException {

		StringBuilder resultingFilter = new StringBuilder();

		String column = AccessPolicy.getAttributeName(entity, filter.getName());
		resultingFilter.append(column);
		if (columns != null) {
			columns.add(column);
		}

		switch(filter.getComparator()) {

		case EQ:
			resultingFilter.append(" = ");
			break;
		case NE:
			resultingFilter.append(" <> ");
			break;
		case GT:
			resultingFilter.append(" > ");
			break;
		case GE:
			resultingFilter.append(" >= ");
			break;
		case LT:
			resultingFilter.append(" < ");
			break;
		case LE:
			resultingFilter.append(" <= ");
			break;
		}

		resultingFilter.append(filter.getValue());

		if (values != null) {
			values.add(filter.getValue());
		}

		return resultingFilter.toString();
	}

	/**
	 * Retorna una cadena de texto con el conjunto de campos de filtrado de la entidad indicada en FN parentizada.
	 * 
	 * @param entity Nombre de la entidad a la que pertenece la lista de campos filtrado.
	 * @param filterSet Conjunto de filtros a resolver.
	 * @param columns Lista en donde se retornará el conjunto de columnas de filtrado en el mismo orden de resolución.
	 * @param values Lista en donde se retornará el conjunto de valores de las columnas clave en el mismo orden de resolución.
	 * 
	 * @return una cadena de texto con el conjunto de campos de filtrado de la entidad indicada en FN parentizada.
	 */
	private String getFilterSetString(String entity, Filter filterSet, AbstractList<String> columns, AbstractList<Object> values) throws ServerException {

		if ((filterSet.getFilterItems() == null || filterSet.getFilterItems().size() == 0) &&
				(filterSet.getFilterSets() == null || filterSet.getFilterSets().size() == 0)) {

			return null;
		}

		StringBuilder resultingFilterSet = new StringBuilder();

		resultingFilterSet.append('(');

		/*
		 * Resolución de los filtros simples.
		 */
		if (filterSet.getFilterItems() != null && filterSet.getFilterItems().size() > 0) {

			FilterItem currentFilter = filterSet.getFilterItems().get(0);

			resultingFilterSet.append(getFilterString(entity, currentFilter, columns, values));
			for (int i=1; i<filterSet.getFilterItems().size(); i++) {

				currentFilter = filterSet.getFilterItems().get(i);

				resultingFilterSet.append(filterSet.getNexus() == Filter.NexusType.AND ? " AND " : " OR ");
				resultingFilterSet.append(getFilterString(entity, currentFilter, columns, values));

			}
		}

		/*
		 * Resolución de los conjuntos de filtros anidados.
		 */
		if (filterSet.getFilterSets() != null && filterSet.getFilterSets().size() > 0) {
			if (filterSet.getFilterItems() != null && filterSet.getFilterItems().size() > 0) {
				resultingFilterSet.append(filterSet.getNexus() == Filter.NexusType.AND ? " AND " : " OR ");
			}
			Filter currentFilterSet = filterSet.getFilterSets().get(0);
			resultingFilterSet.append(getFilterSetString(entity, currentFilterSet, columns, values));
			for (int i=1; i<filterSet.getFilterItems().size(); i++) {

				currentFilterSet = filterSet.getFilterSets().get(i);
				resultingFilterSet.append(filterSet.getNexus() == Filter.NexusType.AND ? " AND " : " OR ");
				resultingFilterSet.append(getFilterSetString(entity, currentFilterSet, columns, values));

			}
		}

		resultingFilterSet.append(')');

		return resultingFilterSet.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Operation retreave(Requestor requestor, Operation operation) throws ServerException {

		final StringBuilder query = new StringBuilder();
		final EntityJDBCInformation entityInformation = entitiesInformation.get(operation.getMetadata().getEntity());

		query.append("SELECT ");

		/*
		 * Especificación del conjunto de campos de la query.
		 */
		final AbstractList<String> dataColumns = new ArrayList<String>();
		query.append(getDataString(operation.getMetadata().getEntity(), operation.getData() != null && operation.getData().size() > 0 ? operation.getData().get(0).getAttributes() : null, dataColumns));

		/*
		 * Especificación de la tabla.
		 */
		query.append(" FROM ").append(operation.getMetadata().getEntity());

		/*
		 * Especificación del filtro.
		 */
		final AbstractList<String> keyColumns;
		final AbstractList<Object> keyValues;
		final AbstractList<String> filterColumns;
		final AbstractList<Object> filterValues;

		if (operation.getMetadata().getKey() != null && operation.getMetadata().getKey().size() > 0) {
			keyColumns = new ArrayList<String>();
			keyValues = new ArrayList<Object>();
			filterColumns = null;
			filterValues = null;
			query.append(" WHERE ").append(getKeyString(operation.getMetadata().getEntity(), operation.getMetadata().getKey(), keyColumns, keyValues));
		}
		else if (operation.getMetadata().getFilter() != null) {
			keyColumns = null;
			keyValues = null;
			filterColumns = new ArrayList<String>();
			filterValues = new ArrayList<Object>();
			query.append(" WHERE ").append(getFilterSetString(operation.getMetadata().getEntity(), operation.getMetadata().getFilter(), filterColumns, filterValues));
		}
		else {
			throw new ServerException(ERROR_UNCOMPLETE_REQUEST, ERROR_UNCOMPLETE_REQUEST_MESSAGE);
		}

		Object[] params = null;
		int[] types = null;

		if (keyColumns.size() > 0) {

			params = new Object[keyColumns.size()];
			types = new int[params.length];

			for (int i=0; i<keyColumns.size(); i++) {
				params[i] = keyValues.get(i);
				types[i] = entityInformation.keyFields.get(keyColumns.get(i));
			}
		}
		else if (filterColumns.size() > 0) {
			params = new Object[filterColumns.size()];
			types = new int[params.length];

			for (int i=0; i<filterColumns.size(); i++) {
				params[i] = filterValues.get(i);
				types[i] = entityInformation.filterFields.get(filterColumns.get(i));
			}
		}

		return jdbcTemplate.query(query.toString(), params, types, new GenericJDBCResultSetExtractor(operation));
	}

	/**
	 * {@inheritDoc}
	 */
	public void checkAccessPoliciesAgainstDataSource(AccessPolicy accessPolicy) throws ServerException {

		entitiesInformation = new HashMap<String, EntityJDBCInformation>();

		DataSource dataSource = jdbcTemplate.getDataSource();

		try {
			Connection connection = dataSource.getConnection();

			// Se recorren las entidades.
			for (EntityAccessPolicy entityAccessPolicy : accessPolicy.getAccessPolicies()) {

				// Entidad.
				String table = entityAccessPolicy.getName().getName();
				EntityJDBCInformation entityJDBCInformation = new EntityJDBCInformation();

				ResultSet tables = connection.getMetaData().getTables(null, null, table, null);

				if (tables.first()) {

					entityJDBCInformation.catalog = tables.getString("TABLE_CAT");
					if (tables.wasNull()) {
						entityJDBCInformation.catalog = null;
					}
					entityJDBCInformation.schema = tables.getString("TABLE_SCHEM");
					if (tables.wasNull()) {
						entityJDBCInformation.schema = null;
					}
					entityJDBCInformation.name = tables.getString("TABLE_NAME");
					entityJDBCInformation.isReadonly = !tables.getString("TABLE_TYPE").equals("TABLE"); // si es distinto de "TABLE", los atributos de datos únicamente pueden ser de lectura.

					entitiesInformation.put(entityJDBCInformation.name, entityJDBCInformation);
				}
				else {
					throw new ServerException(GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED, GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " La tabla " + table + " no existe en la fuente de datos.");
				}

				// Atributos de datos de la entidad.
				if (entityAccessPolicy.getData() != null && entityAccessPolicy.getData().getData() != null && entityAccessPolicy.getData().getData().size() > 0) {

					entityJDBCInformation.dataFields = new TreeMap<String, Integer>();
					ResultSet columns = connection.getMetaData().getColumns(entityJDBCInformation.catalog, entityJDBCInformation.schema, entityJDBCInformation.name, null);

					if (columns.first()) {
						do {
							String columnName = columns.getString("COLUMN_NAME");
							int columnType = columns.getInt("DATA_TYPE"); // => SQL type from java.sql.Types

							for (EntityDataAttribute entityDataAttribute : entityAccessPolicy.getData().getData()) {

								if (entityDataAttribute.getName().equalsIgnoreCase(columnName)) {
									entityJDBCInformation.dataFields.put(columnName, columnType);

									if (entityJDBCInformation.isReadonly && entityDataAttribute.getDirection() != Direction.OUTPUT) {
										throw new ServerException(GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED, GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " La columa " + columnName +" de la tabla "  + table + " es de solo lectura.");
									}
									break;
								}
							}

						} while(columns.next());
					}
					else {
						throw new ServerException(GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED, GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No se han encontrado columnas para la tabla "  + table + ".");
					}

					// Error, hay campos que no se han podido encontrar el la tabla.
					if (entityJDBCInformation.dataFields.size() != entityAccessPolicy.getData().getData().size()) {

						StringBuilder lostFields = new StringBuilder();

						for (EntityDataAttribute entityDataAttribute : entityAccessPolicy.getData().getData()) {

							String fieldName = entityDataAttribute.getName();

							boolean found = false;
							for (Entry<String, Integer> field : entityJDBCInformation.dataFields.entrySet()) {

								if (fieldName.equalsIgnoreCase(field.getKey())) {
									found = true;
									break;
								}
							}

							if (!found) {
								if (lostFields.length() > 0) {
									lostFields.append(", ");	
								}

								lostFields.append(fieldName);
							}
						}

						throw new ServerException(GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED, GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No se han encontrado las columnas " + lostFields + " para la tabla "  + table + ".");
					}
				}

				// Atributos de las claves de la entidad.
				if (entityAccessPolicy.getKeys() != null && entityAccessPolicy.getKeys().getKeys() != null && entityAccessPolicy.getKeys().getKeys().size() > 0) {

					entityJDBCInformation.keys = new HashMap<String, List<String>>();
					entityJDBCInformation.keyFields = new TreeMap<String, Integer>();

					//Índices únicos.
					ResultSet uniqueIndexes = connection.getMetaData().getIndexInfo(entityJDBCInformation.catalog, entityJDBCInformation.schema, entityJDBCInformation.name, true, false);
					if (uniqueIndexes.first()) {
						do {
							String columnName = uniqueIndexes.getString("COLUMN_NAME");
							String indexName = uniqueIndexes.getString("INDEX_NAME");
							if (uniqueIndexes.wasNull()) {
								indexName = null;
							}

							List<String> indexFields = entityJDBCInformation.keys.get(indexName);
							if (indexFields == null) {
								indexFields = new ArrayList<String>();
								entityJDBCInformation.keys.put(indexName, indexFields);
							}

							indexFields.add(columnName);
							entityJDBCInformation.keyFields.put(columnName, null);

						} while(uniqueIndexes.next());
					}

					for (EntityKey entityKey : entityAccessPolicy.getKeys().getKeys()) {

						if (!hasEquivalentAttributeCollection(entityJDBCInformation.keys, entityKey.getKey())) {
							throw new ServerException(GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED, GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " Clave no permitida: La tabla " + table + " no tiene definida la clave única o índice único para los campos " + entityKey.getKeyNamesString() + ".");
						}
					}

					ResultSet columns = connection.getMetaData().getColumns(entityJDBCInformation.catalog, entityJDBCInformation.schema, entityJDBCInformation.name, null);
					if (columns.first()) {
						do {
							String columnName = columns.getString("COLUMN_NAME");
							int columnType = columns.getInt("DATA_TYPE"); // => SQL type from java.sql.Types

							if (entityJDBCInformation.keyFields.containsKey(columnName)) {
								entityJDBCInformation.keyFields.put(columnName, columnType);
							}

						} while(columns.next());
					}
				}

				// Atributos de los filtros de la entidad.
				if (entityAccessPolicy.getFilters() != null && entityAccessPolicy.getFilters().getFilters() != null && entityAccessPolicy.getFilters().getFilters().size() > 0) {

					entityJDBCInformation.indexes = new HashMap<String, List<String>>();
					entityJDBCInformation.filterFields = new TreeMap<String, Integer>();

					//Índices.
					ResultSet indexes = connection.getMetaData().getIndexInfo(entityJDBCInformation.catalog, entityJDBCInformation.schema, entityJDBCInformation.name, false, false);
					if (indexes.first()) {
						do {
							String columnName = indexes.getString("COLUMN_NAME");
							String indexName = indexes.getString("INDEX_NAME");
							if (indexes.wasNull()) {
								indexName = null;
							}

							List<String> indexFields = entityJDBCInformation.indexes.get(indexName);
							if (indexFields == null) {
								indexFields = new ArrayList<String>();
								entityJDBCInformation.indexes.put(indexName, indexFields);
							}

							indexFields.add(columnName);
							entityJDBCInformation.filterFields.put(columnName, null);

						} while(indexes.next());
					}

					for (EntityFilter entityFilter : entityAccessPolicy.getFilters().getFilters()) {

						if (!hasEquivalentAttributeCollection(entityJDBCInformation.indexes, entityFilter.getFilter())) {
							throw new ServerException(GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED, GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " Filtro no permitido: La tabla " + table + " no tiene definido un índice para los campos " + entityFilter.getFilterNamesString() + ".");
						}
					}

					ResultSet columns = connection.getMetaData().getColumns(entityJDBCInformation.catalog, entityJDBCInformation.schema, entityJDBCInformation.name, null);
					if (columns.first()) {
						do {
							String columnName = columns.getString("COLUMN_NAME");
							int columnType = columns.getInt("DATA_TYPE"); // => SQL type from java.sql.Types

							if (entityJDBCInformation.filterFields.containsKey(columnName)) {
								entityJDBCInformation.filterFields.put(columnName, columnType);
							}

						} while(columns.next());
					}
				}
			}

		} catch (SQLException e) {
			throw new ServerException(GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED, GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No ha sido posible obtener los metadatos de la fuente de datos.");
		}
	}

	/**
	 * Clase interna para la gestión de la transacción del conjunto de operaciones.
	 */
	private class GenericTransactionCallback implements TransactionCallback<Generic> {

		private Generic generic;
		private ServerException inTransactionException;


		/**
		 * Constructor con parámetro.
		 * 
		 * @param generic Estructura de operaciones a resolver.
		 */
		public GenericTransactionCallback(Generic generic)	{

			this.generic = generic;
			this.inTransactionException = null;
		}

		/**
		 * {@inheritDoc}
		 */
		public Generic doInTransaction(TransactionStatus paramTransactionStatus) {

			try {

				for (Operation operation : generic.getOperations()) {

					processOperation(generic.getRequestor(), operation);
				}

			} catch (ServerException e) {

				inTransactionException = e;
				paramTransactionStatus.setRollbackOnly();
			}

			return generic;
		}

		/**
		 * Retorna <code>null</code> o la excepción, en caso que se haya producido.
		 * 
		 * @return <code>null</code> o la excepción, en caso que se haya producido.
		 */
		public ServerException getInTransactionException() {
			return inTransactionException;
		}
	}		

	/**
	 * {@inheritDoc}
	 */
	public Generic processGeneric(final Generic generic) throws ServerException {

		GenericTransactionCallback genericTransactionCallback = new GenericTransactionCallback(generic);

		transactionTemplate.execute(genericTransactionCallback);

		generic.getRequestor().setTime(System.currentTimeMillis());

		if (genericTransactionCallback.getInTransactionException() != null) {
			throw genericTransactionCallback.getInTransactionException();
		}

		return generic;
	}
}
