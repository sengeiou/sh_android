package com.fav24.dataservices.service.generic.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.fav24.dataservices.domain.Requestor;
import com.fav24.dataservices.domain.datasource.DataSources;
import com.fav24.dataservices.domain.generic.Filter;
import com.fav24.dataservices.domain.generic.FilterItem;
import com.fav24.dataservices.domain.generic.Generic;
import com.fav24.dataservices.domain.generic.KeyItem;
import com.fav24.dataservices.domain.generic.Operation;
import com.fav24.dataservices.domain.security.AccessPolicy;
import com.fav24.dataservices.domain.security.EntityAccessPolicy;
import com.fav24.dataservices.domain.security.EntityAttribute;
import com.fav24.dataservices.domain.security.EntityDataAttribute;
import com.fav24.dataservices.domain.security.EntityDataAttribute.Direction;
import com.fav24.dataservices.domain.security.EntityFilter;
import com.fav24.dataservices.domain.security.EntityKey;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.generic.GenericService;
import com.fav24.dataservices.service.security.AccessPolicyService;
import com.fav24.dataservices.util.JDBCUtils;


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
		private Boolean isView;
		private Map<String, Integer> dataFields;
		private Map<String, Set<String>> keys;
		private Map<String, Set<String>> indexes;
		private Map<String, Integer> keyFields;
		private Map<String, Integer> filterFields;
	}

	private static Map<String, EntityJDBCInformation> entitiesInformation;


	public GenericServiceJDBC() {

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
	private StringBuilder getDataString(String entity, Map<String, Object> attributes, AbstractList<String> columns) throws ServerException {

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

					String attributeAlias = attributeAliases.next();
					column = AccessPolicy.getAttributeName(entity, attributeAlias);

					if (column == null) {
						AccessPolicy.checkAttributesAccesibility(entity, new ArrayList<String>(attributes.keySet()));
					}
					else {
						resultingData.append(',');
						resultingData.append(column);

						if (columns != null) {
							columns.add(column);
						}
					}
				}
			}
		}
		else {
			resultingData.append("count(*)");
		}

		return resultingData;
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
	private StringBuilder getKeyString(String entity, AbstractList<KeyItem> keys, AbstractList<String> columns, AbstractList<Object> values) throws ServerException {

		StringBuilder resultingKey = new StringBuilder();

		String column;
		KeyItem key = keys.get(0);

		if (key.getValue() == null) {

			resultingKey.append(AccessPolicy.getAttributeName(entity, key.getName())).append(" IS NULL");
		}
		else {

			column = AccessPolicy.getAttributeName(entity, key.getName());

			resultingKey.append(column).append('=').append('?');

			if (columns != null) {
				columns.add(column);
			}

			if (values != null) {
				values.add(key.getValue());
			}
		}

		for (int i=1; i<keys.size(); i++) {

			key = keys.get(i);

			resultingKey.append(" AND ");

			if (key.getValue() == null) {

				resultingKey.append(AccessPolicy.getAttributeName(entity, key.getName())).append(" IS NULL");
			}
			else {

				column = AccessPolicy.getAttributeName(entity, key.getName());

				resultingKey.append(column).append('=').append('?');

				if (columns != null) {
					columns.add(column);
				}

				if (values != null) {
					values.add(key.getValue());
				}
			}
		}

		return resultingKey;
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
	private StringBuilder getFilterString(String entity, FilterItem filter, AbstractList<String> columns, AbstractList<Object> values) throws ServerException {

		StringBuilder resultingFilter = new StringBuilder();

		String column = AccessPolicy.getAttributeName(entity, filter.getName());
		resultingFilter.append(column);

		if (filter.getValue() == null) {

			switch(filter.getComparator()) {

			case EQ:
				resultingFilter.append(" IS NULL");
				break;
			case NE:
				resultingFilter.append(" IS NOT NULL");
				break;
			case GT:
				resultingFilter.append(" > NULL");
				break;
			case GE:
				resultingFilter.append(" >= NULL");
				break;
			case LT:
				resultingFilter.append(" < NULL");
				break;
			case LE:
				resultingFilter.append(" <= NULL");
				break;
			}
		}
		else {

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

			resultingFilter.append('?');

			if (columns != null) {
				columns.add(column);
			}

			if (values != null) {
				values.add(filter.getValue());
			}
		}

		return resultingFilter;
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
	private StringBuilder getFilterSetString(String entity, Filter filterSet, AbstractList<String> columns, AbstractList<Object> values) throws ServerException {

		if ((filterSet.getFilterItems() == null || filterSet.getFilterItems().size() == 0) &&
				(filterSet.getFilters() == null || filterSet.getFilters().size() == 0)) {

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
		if (filterSet.getFilters() != null && filterSet.getFilters().size() > 0) {
			if (filterSet.getFilterItems() != null && filterSet.getFilterItems().size() > 0) {
				resultingFilterSet.append(filterSet.getNexus() == Filter.NexusType.AND ? " AND " : " OR ");
			}
			Filter currentFilterSet = filterSet.getFilters().get(0);
			resultingFilterSet.append(getFilterSetString(entity, currentFilterSet, columns, values));
			for (int i=1; i<filterSet.getFilterItems().size(); i++) {

				currentFilterSet = filterSet.getFilters().get(i);
				resultingFilterSet.append(filterSet.getNexus() == Filter.NexusType.AND ? " AND " : " OR ");
				resultingFilterSet.append(getFilterSetString(entity, currentFilterSet, columns, values));

			}
		}

		resultingFilterSet.append(')');

		return resultingFilterSet;
	}

	/**
	 * Método para la conversión de tipos de datos.
	 *  
	 * @param destinationType Tipo de dato al que se desea convertir el valor.
	 * @param value Valor a convertir.
	 * 
	 * @return valor convertido.
	 */
	private Object translateToType(int destinationType, Object value) {

		if (value != null) {

			switch(destinationType) {
			case java.sql.Types.DATE:
				if (value instanceof Number) {
					return new Date(((Number) value).longValue());
				}
				else if (value instanceof String) {
					return Date.valueOf((String)value);
				}
				break;
			case java.sql.Types.TIME:
				if (value instanceof Number) {
					return new Time(((Number) value).longValue());
				}
				else if (value instanceof String) {
					return Time.valueOf((String)value);
				}
				break;
			case java.sql.Types.TIMESTAMP:
				if (value instanceof Number) {
					return new Timestamp(((Number) value).longValue());
				}
				else if (value instanceof String) {
					return Timestamp.valueOf((String)value);
				}
				break;
			}
		}

		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Operation retreave(Requestor requestor, Operation operation) throws ServerException {

		final StringBuilder querySelect = new StringBuilder();
		final StringBuilder queryFrom = new StringBuilder();
		final StringBuilder queryWhere = new StringBuilder();
		final StringBuilder queryLimit = new StringBuilder();

		final EntityJDBCInformation entityInformation = entitiesInformation.get(AccessPolicy.getEntityName(operation.getMetadata().getEntity()));

		querySelect.append("SELECT ");

		/*
		 * Especificación del conjunto de campos de la query.
		 */
		final AbstractList<String> dataColumns = new ArrayList<String>();
		querySelect.append(getDataString(operation.getMetadata().getEntity(), operation.getData() != null && operation.getData().size() > 0 ? operation.getData().get(0).getAttributes() : null, dataColumns));

		/*
		 * Especificación de la tabla.
		 */
		queryFrom.append(" FROM ").append(AccessPolicy.getEntityName(operation.getMetadata().getEntity()));

		/*
		 * Especificación del filtro.
		 */
		AbstractList<String> keyColumns = null, filterColumns = null;
		AbstractList<Object> keyValues = null, filterValues = null;

		if (operation.getMetadata().getKey() != null && operation.getMetadata().getKey().size() > 0) {
			keyColumns = new ArrayList<String>();
			keyValues = new ArrayList<Object>();

			StringBuilder key = getKeyString(operation.getMetadata().getEntity(), operation.getMetadata().getKey(), keyColumns, keyValues);

			if (key != null && key.length() > 0) {
				queryWhere.append(" WHERE ").append(key);
			}
		}
		else if (operation.getMetadata().getFilter() != null) {
			filterColumns = new ArrayList<String>();
			filterValues = new ArrayList<Object>();

			StringBuilder filter = getFilterSetString(operation.getMetadata().getEntity(), operation.getMetadata().getFilter(), filterColumns, filterValues);

			if (filter != null && filter.length() > 0) {
				queryWhere.append(" WHERE ").append(filter);
			}
		}
		else {
			throw new ServerException(ERROR_UNCOMPLETE_REQUEST, ERROR_UNCOMPLETE_REQUEST_MESSAGE);
		}

		/* 
		 *  Tratamiento del intervalo de datos a retornar.
		 *  
		 *  LIMIT items OFFSET offset
		 *  
		 *  Esta sintaxis es válida para: MySQL, MariaDB, PostgreSQL y hSQL.
		 *  No és válida para: Oracle y SQLServer.
		 */
		Long items = operation.getMetadata().getItems() == null ? AccessPolicy.getCurrentAccesPolicy().getEntityPolicy(operation.getMetadata().getEntity()).getMaxPageSize() : operation.getMetadata().getItems();
		Long offset = operation.getMetadata().getOffset();

		if ((items != null && items != 0) || (offset != null && offset != 0)) {

			queryLimit.append(" LIMIT ").append(items);

			if (offset != null) {
				queryLimit.append(" OFFSET ").append(offset);
			}
		}

		Object[] params = null;
		int[] types = null;

		if (keyColumns != null && keyColumns.size() > 0) {

			types = new int[keyColumns.size()];
			params = new Object[types.length];

			for (int i=0; i<keyColumns.size(); i++) {
				types[i] = entityInformation.keyFields.get(keyColumns.get(i));
				params[i] = translateToType(types[i], keyValues.get(i));
			}
		}
		else if (filterColumns != null && filterColumns.size() > 0) {
			types = new int[filterColumns.size()];
			params = new Object[types.length];

			for (int i=0; i<filterColumns.size(); i++) {
				types[i] = entityInformation.filterFields.get(filterColumns.get(i));
				params[i] = translateToType(types[i], filterValues.get(i));
			}
		}

		querySelect.append(queryFrom).append(queryWhere).append(queryLimit);
		operation = DataSources.getJdbcTemplateDataService().query(querySelect.toString(), params, types, new GenericJDBCResultSetExtractor(operation));

		StringBuilder countQuery = new StringBuilder("SELECT count(*) ");
		countQuery.append(queryFrom).append(queryWhere);
		operation.getMetadata().setTotalItems(DataSources.getJdbcTemplateDataService().queryForObject(countQuery.toString(), params, Long.class));

		return operation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void checkAndGatherAccessPoliciesInformationAgainstDataSource(AccessPolicy accessPolicy) throws ServerException {

		if (accessPolicy == null) {
			return;
		}

		entitiesInformation = new HashMap<String, EntityJDBCInformation>();

		javax.sql.DataSource dataSource = DataSources.getJdbcTemplateDataService().getDataSource();
		Connection connection = null;

		try {
			connection = dataSource.getConnection();

			// Se recorren las entidades.
			for (EntityAccessPolicy entityAccessPolicy : accessPolicy.getAccessPolicies()) {

				// Entidad.
				EntityJDBCInformation entityJDBCInformation = new EntityJDBCInformation();
				String table = entityAccessPolicy.getName().getName();
				ResultSet tables = null;
				try {

					tables = connection.getMetaData().getTables(null, null, table, null);

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
						entityJDBCInformation.isView = !tables.getString("TABLE_TYPE").equals("TABLE"); // si es distinto de "TABLE", los atributos de datos únicamente pueden ser de lectura.

						entitiesInformation.put(entityJDBCInformation.name, entityJDBCInformation);
					}
					else {
						throw new ServerException(GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED, GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " La tabla " + table + " no existe en la fuente de datos.");
					}
				}
				catch(Exception e) {
					throw new ServerException(GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED, GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No se ha podido acceder a la metainformación de la tabla "  + table + ", debido a: " + e.getMessage());
				}
				finally {
					JDBCUtils.CloseQuietly(tables);
				}

				// Atributos de datos de la entidad.
				if (entityAccessPolicy.getData() != null && entityAccessPolicy.getData().getData() != null && entityAccessPolicy.getData().getData().size() > 0) {

					entityJDBCInformation.dataFields = new TreeMap<String, Integer>();
					ResultSet columns = null;

					try {
						columns = connection.getMetaData().getColumns(entityJDBCInformation.catalog, entityJDBCInformation.schema, entityJDBCInformation.name, null);

						if (columns.first()) {
							do {
								String columnName = columns.getString("COLUMN_NAME");
								int columnType = columns.getInt("DATA_TYPE"); // => SQL type from java.sql.Types

								for (EntityDataAttribute entityDataAttribute : entityAccessPolicy.getData().getData()) {

									if (entityDataAttribute.getName().equalsIgnoreCase(columnName)) {
										entityJDBCInformation.dataFields.put(columnName, columnType);

										if (entityJDBCInformation.isView && entityDataAttribute.getDirection() != Direction.OUTPUT) {
											throw new ServerException(GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED, GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " La columa " + columnName +" de la tabla "  + table + " es de solo lectura.");
										}
										break;
									}
								}

							} while(columns.next());
						}
						else {
							JDBCUtils.CloseQuietly(columns);

							throw new ServerException(GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED, GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No se han encontrado columnas para la tabla "  + table + ".");
						}
					}
					catch(Exception e) {
						throw new ServerException(GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED, GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No se ha podido acceder a la metainformación de las columnas de la tabla "  + table + ", debido a: " + e.getMessage());
					}
					finally {
						JDBCUtils.CloseQuietly(columns);
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

					entityJDBCInformation.keyFields = new TreeMap<String, Integer>();

					if (entityJDBCInformation.isView) {

						for (EntityKey entityKey : entityAccessPolicy.getKeys().getKeys()) {

							for (EntityAttribute keyAttribute : entityKey.getKey()) {
								entityJDBCInformation.keyFields.put(keyAttribute.getName(), null);
							}
						}
					}
					else {

						entityJDBCInformation.keys = new HashMap<String, Set<String>>();

						//Índices únicos.
						ResultSet uniqueIndexes = null;

						try {
							uniqueIndexes = connection.getMetaData().getIndexInfo(entityJDBCInformation.catalog, entityJDBCInformation.schema, entityJDBCInformation.name, true, false);

							if (uniqueIndexes.first()) {
								do {
									String columnName = uniqueIndexes.getString("COLUMN_NAME");
									String indexName = uniqueIndexes.getString("INDEX_NAME");
									if (uniqueIndexes.wasNull()) {
										indexName = null;
									}

									Set<String> indexFields = entityJDBCInformation.keys.get(indexName);
									if (indexFields == null) {
										indexFields = new HashSet<String>();
										entityJDBCInformation.keys.put(indexName, indexFields);
									}

									indexFields.add(columnName);
									entityJDBCInformation.keyFields.put(columnName, null);

								} while(uniqueIndexes.next());
							}
						}
						catch(Exception e) {
							throw new ServerException(GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED, GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No se ha podido acceder a la metainformación de las claves de la tabla "  + table + ", debido a: " + e.getMessage());
						}
						finally {
							JDBCUtils.CloseQuietly(uniqueIndexes);
						}

						for (EntityKey entityKey : entityAccessPolicy.getKeys().getKeys()) {

							if (!hasEquivalentAttributeCollection(entityJDBCInformation.keys, entityKey.getKey())) {

								StringBuilder specificMessage = new StringBuilder(" Clave no permitida: La tabla ").append(table).
										append(" no tiene definida la clave única o índice único para los campos ").append(entityKey.getKeyNamesString()).append(".");
								specificMessage.append("\nLas claves disponibles son:\n");

								for(Set<String> keyFields : entityJDBCInformation.keys.values()) {

									specificMessage.append('<');
									boolean firstField = true;
									for(String keyField : keyFields) {

										if (!firstField) {
											specificMessage.append(", ");
										}

										specificMessage.append(keyField);
										firstField = false;
									}
									specificMessage.append('>');
								}

								throw new ServerException(GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED, GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + specificMessage);
							}
						}
					}

					ResultSet columns = null;
					try {
						columns = connection.getMetaData().getColumns(entityJDBCInformation.catalog, entityJDBCInformation.schema, entityJDBCInformation.name, null);
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
					catch(Exception e) {
						throw new ServerException(GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED, GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No se ha podido acceder a la metainformación de las columnas de las claves de la tabla "  + table + ", debido a: " + e.getMessage());
					}
					finally {
						JDBCUtils.CloseQuietly(columns);
					}

					// Comprobación de que los campos clave están correctamente definidos en la base de datos.
					StringBuilder lostKeyFields = null;
					for (Entry<String, Integer> field : entityJDBCInformation.keyFields.entrySet()) {

						if (field.getValue() == null) {
							if (lostKeyFields != null) {
								lostKeyFields.append(", ");	
								lostKeyFields.append(field.getKey());
							}
							else {
								lostKeyFields = new StringBuilder(field.getKey());
							}
						}
					}

					if (lostKeyFields != null) {

						StringBuilder specificMessage = new StringBuilder(" No se han encontrado las columnas clave ").append(lostKeyFields).append(" para la tabla ").append(table);
						specificMessage.append("\nLas claves disponibles son:\n");

						for(Set<String> keyFields : entityJDBCInformation.keys.values()) {

							specificMessage.append('<');
							boolean firstField = true;
							for(String keyField : keyFields) {

								if (!firstField) {
									specificMessage.append(", ");
								}

								specificMessage.append(keyField);
								firstField = false;
							}
							specificMessage.append('>');
						}

						throw new ServerException(GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED, GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No se han encontrado las columnas clave " + lostKeyFields + " para la tabla "  + table + ".");
					}
				}

				// Atributos de los filtros de la entidad.
				if (entityAccessPolicy.getFilters() != null && entityAccessPolicy.getFilters().getFilters() != null && entityAccessPolicy.getFilters().getFilters().size() > 0) {

					entityJDBCInformation.filterFields = new TreeMap<String, Integer>();

					if (entityJDBCInformation.isView) {

						for (EntityAttribute filterAttribute : entityAccessPolicy.getFilters().getAllFiltersAttributes(null)) {

							entityJDBCInformation.filterFields.put(filterAttribute.getName(), null);
						}
					}
					else {

						entityJDBCInformation.indexes = new HashMap<String, Set<String>>();

						//Índices.
						ResultSet indexes = null;

						try {
							indexes = connection.getMetaData().getIndexInfo(entityJDBCInformation.catalog, entityJDBCInformation.schema, entityJDBCInformation.name, false, false);
							if (indexes.first()) {
								do {
									String columnName = indexes.getString("COLUMN_NAME");
									String indexName = indexes.getString("INDEX_NAME");
									if (indexes.wasNull()) {
										indexName = null;
									}

									Set<String> indexFields = entityJDBCInformation.indexes.get(indexName);
									if (indexFields == null) {
										indexFields = new HashSet<String>();
										entityJDBCInformation.indexes.put(indexName, indexFields);
									}

									indexFields.add(columnName);
									entityJDBCInformation.filterFields.put(columnName, null);

								} while(indexes.next());
							}
						}
						catch(Exception e) {
							throw new ServerException(GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED, GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No se ha podido acceder a la metainformación de los índices de la tabla "  + table + ", debido a: " + e.getMessage());
						}
						finally {
							JDBCUtils.CloseQuietly(indexes);
						}

						//Foreing keys
						ResultSet importedKeys = null;

						try {
							importedKeys = connection.getMetaData().getImportedKeys(entityJDBCInformation.catalog, entityJDBCInformation.schema, entityJDBCInformation.name);
							if (importedKeys.first()) {
								do {
									String columnName = importedKeys.getString("FKCOLUMN_NAME");
									String foreingKeyName = importedKeys.getString("FK_NAME");
									if (importedKeys.wasNull()) {
										foreingKeyName = null;
									}

									Set<String> indexFields = entityJDBCInformation.indexes.get(foreingKeyName);
									if (indexFields == null) {
										indexFields = new HashSet<String>();
										entityJDBCInformation.indexes.put(foreingKeyName, indexFields);
									}

									indexFields.add(columnName);
									entityJDBCInformation.filterFields.put(columnName, null);

								} while(importedKeys.next());
							}
						}
						catch(Exception e) {
							throw new ServerException(GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED, GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No se ha podido acceder a la metainformación de las claves foraneas importadas de la tabla "  + table + ", debido a: " + e.getMessage());
						}
						finally {
							JDBCUtils.CloseQuietly(importedKeys);
						}

						for (EntityFilter entityFilter : entityAccessPolicy.getFilters().getFilters()) {

							if (!hasEquivalentAttributeCollection(entityJDBCInformation.indexes, entityFilter.getFilter())) {
								throw new ServerException(GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED, GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " Filtro no permitido: La tabla " + table + " no tiene definido un índice para los campos " + entityFilter.getFilterNamesString() + ".");
							}
						}
					}

					ResultSet columns = null;

					try {
						columns = connection.getMetaData().getColumns(entityJDBCInformation.catalog, entityJDBCInformation.schema, entityJDBCInformation.name, null);
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
					catch(SQLException e) {
						throw new ServerException(GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED, GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No se ha podido acceder a la metainformación de las columnas de los filtros definidos para la tabla "  + table + ".");
					}
					finally {
						JDBCUtils.CloseQuietly(columns);
					}

					// Comprobación de que los campos clave están correctamente definidos en la base de datos.
					StringBuilder lostFilterFields = null;
					for (Entry<String, Integer> field : entityJDBCInformation.filterFields.entrySet()) {

						if (field.getValue() == null) {
							if (lostFilterFields != null) {
								lostFilterFields.append(", ");	
								lostFilterFields.append(field.getKey());
							}
							else {
								lostFilterFields = new StringBuilder(field.getKey());
							}
						}
					}

					if (lostFilterFields != null) {
						throw new ServerException(GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED, GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No se han encontrado las columnas de filtrado " + lostFilterFields + " para la tabla "  + table + ".");
					}
				}
			}
		} 
		catch (ServerException e) {
			entitiesInformation = null;
			throw e;
		}
		catch (Exception e) {
			entitiesInformation = null;
			throw new ServerException(GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED, GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No ha sido posible obtener los metadatos de la fuente de datos, debido a: " + e.getMessage());
		}
		finally {
			JDBCUtils.CloseQuietly(connection);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resetAccessPoliciesInformationAgainstDataSource() {

		entitiesInformation = null;
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

					if (!entitiesInformation.containsKey(AccessPolicy.getEntityName(operation.getMetadata().getEntity()))) {
						throw new ServerException(AccessPolicyService.ERROR_NO_CURRENT_POLICY_DEFINED_FOR_ENTITY, String.format(AccessPolicyService.ERROR_NO_CURRENT_POLICY_DEFINED_FOR_ENTITY_MESSAGE, operation.getMetadata().getEntity()));	
					}

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

		if (entitiesInformation == null) {
			throw new ServerException(AccessPolicyService.ERROR_NO_CURRENT_POLICY_DEFINED, AccessPolicyService.ERROR_NO_CURRENT_POLICY_DEFINED_MESSAGE);	
		}

		GenericTransactionCallback genericTransactionCallback = new GenericTransactionCallback(generic);

		DataSources.getTransactionTemplateDataService().execute(genericTransactionCallback);

		generic.getRequestor().setTime(System.currentTimeMillis());

		if (genericTransactionCallback.getInTransactionException() != null) {
			throw genericTransactionCallback.getInTransactionException();
		}

		return generic;
	}
}
