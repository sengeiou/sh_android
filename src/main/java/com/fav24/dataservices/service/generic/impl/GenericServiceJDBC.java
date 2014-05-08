package com.fav24.dataservices.service.generic.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
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
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.domain.Requestor;
import com.fav24.dataservices.domain.datasource.DataSources;
import com.fav24.dataservices.domain.generic.DataItem;
import com.fav24.dataservices.domain.generic.Filter;
import com.fav24.dataservices.domain.generic.FilterItem;
import com.fav24.dataservices.domain.generic.KeyItem;
import com.fav24.dataservices.domain.generic.Metadata;
import com.fav24.dataservices.domain.generic.Operation;
import com.fav24.dataservices.domain.security.AccessPolicy;
import com.fav24.dataservices.domain.security.EntityAccessPolicy;
import com.fav24.dataservices.domain.security.EntityAttribute;
import com.fav24.dataservices.domain.security.EntityDataAttribute;
import com.fav24.dataservices.domain.security.EntityDataAttribute.Direction;
import com.fav24.dataservices.domain.security.EntityDataAttribute.SynchronizationField;
import com.fav24.dataservices.domain.security.EntityFilter;
import com.fav24.dataservices.domain.security.EntityKey;
import com.fav24.dataservices.domain.security.EntityOrderAttribute;
import com.fav24.dataservices.domain.security.EntityOrderAttribute.Order;
import com.fav24.dataservices.domain.security.EntityOrdination;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.generic.GenericService;
import com.fav24.dataservices.util.JDBCUtils;


/**
 * Versión JDBC de la interfaz de servicio Generic. 
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
		private Set<String> generatedData;
	}

	private static Map<String, EntityJDBCInformation> entitiesInformation;
	private Connection connection;


	public GenericServiceJDBC() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void startTransaction() throws ServerException {

		javax.sql.DataSource dataSource = DataSources.getDataSourceDataService();

		try {
			this.connection = dataSource.getConnection();
		}		
		catch (Exception e) {
			throw new ServerException(GenericService.ERROR_START_TRANSACTION, String.format(GenericService.ERROR_START_TRANSACTION_MESSAGE, e.getMessage()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void endTransaction(boolean commit) throws ServerException {

		if (this.connection != null) {

			try {

				if (!connection.getAutoCommit()) {

					if (commit) {

						this.connection.commit();
					}
					else {

						this.connection.rollback();
					}
				}
			}
			catch(Exception e) {
				throw new ServerException(GenericService.ERROR_END_TRANSACTION, String.format(GenericService.ERROR_END_TRANSACTION_MESSAGE, e.getMessage()));	
			}
			finally {
				JDBCUtils.CloseQuietly(this.connection);
				this.connection = null;
			}
		}
	}

	/**
	 * Retorna una cadena de texto con el conjunto de campos de datos.
	 * 
	 * @param entity Nombre de la entidad a la que pertenece la lista de datos.
	 * @param attributes Mapa de datos a resolver.
	 * 
	 * @return una cadena de texto con el conjunto de campos clave de la entidad indicada en FNC.
	 */
	private StringBuilder getDataString(String entity, Map<String, Object> attributes) throws ServerException {

		StringBuilder resultingData = new StringBuilder();

		if (attributes != null) {

			Iterator<String> attributeAliases = attributes.keySet().iterator();

			if (attributeAliases.hasNext()) {

				String column = AccessPolicy.getAttributeName(entity, attributeAliases.next());

				resultingData.append(column);

				while (attributeAliases.hasNext()) {

					String attributeAlias = attributeAliases.next();
					column = AccessPolicy.getAttributeName(entity, attributeAlias);

					if (column == null) {
						AccessPolicy.checkAttributesAccesibility(entity, new ArrayList<String>(attributes.keySet()));
					}
					else {
						resultingData.append(',');
						resultingData.append(column);
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
	 * Retorna una cadena de texto con el conjunto de campos de datos.
	 * 
	 * @param entity Nombre de la entidad a la que pertenece la lista de datos.
	 * @param attributes Mapa de datos a resolver.
	 * @param entityInformation Información extraida de la fuente de datos, acerca de la entidad.
	 * @param inColumns Lista en donde se retornará el conjunto de columnas de datos a insertar en el mismo orden de resolución.
	 * @param inAliases Lista en donde se retornará el conjunto de alias de datos a insertar en el mismo orden de resolución.
	 * @param outColumns Lista en donde se retornará el conjunto de columnas de datos a retornar en el mismo orden de resolución.
	 * @param outAliases Lista en donde se retornará el conjunto de alias de datos a retornar en el mismo orden de resolución.
	 * 
	 * @return una cadena de texto con el conjunto de campos clave de la entidad indicada en FNC.
	 */
	private StringBuilder getInsertDataString(String entity, Map<String, Object> attributes, EntityJDBCInformation entityInformation, 
			AbstractList<String> inColumns, AbstractList<String> inAliases,
			AbstractList<String> outColumns, AbstractList<String> outAliases
			) throws ServerException {

		StringBuilder resultingData = new StringBuilder();

		Iterator<String> attributeAliases = attributes.keySet().iterator();

		if (attributeAliases.hasNext()) {

			String alias = attributeAliases.next();
			String column = AccessPolicy.getAttributeName(entity, alias);

			if (entityInformation.generatedData.contains(column)) {
				outColumns.add(column);
				outAliases.add(alias);
			}
			else {
				inColumns.add(column);
				inAliases.add(alias);
				resultingData.append(column);
			}

			while (attributeAliases.hasNext()) {

				alias = attributeAliases.next();
				column = AccessPolicy.getAttributeName(entity, alias);

				if (column == null) {
					AccessPolicy.checkAttributesAccesibility(entity, new ArrayList<String>(attributes.keySet()));
				}
				else {
					if (entityInformation.generatedData.contains(column)) {
						outColumns.add(column);
						outAliases.add(alias);
					}
					else {
						inColumns.add(column);
						inAliases.add(alias);
						resultingData.append(',');
						resultingData.append(column);
					}
				}
			}
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
			for (int i=1; i<filterSet.getFilters().size(); i++) {

				currentFilterSet = filterSet.getFilters().get(i);
				resultingFilterSet.append(filterSet.getNexus() == Filter.NexusType.AND ? " AND " : " OR ");
				resultingFilterSet.append(getFilterSetString(entity, currentFilterSet, columns, values));

			}
		}

		resultingFilterSet.append(')');

		return resultingFilterSet;
	}

	/**
	 * Retorna una cadena de texto con el filtro (o <code>null</code>) de los registros eliminados.
	 * 
	 * @param metadata Metadata de la operación de la que se desea obtener el filtro.
	 * 
	 * @return una cadena de texto con el filtro (o <code>null</code>) de los registros eliminados.
	 * 
	 * @throws ServerException
	 */
	private StringBuilder getExcludeDeletedString(Metadata metadata) throws ServerException {

		if (metadata.getIncludeDeleted() == null || !metadata.getIncludeDeleted()) {

			StringBuilder includeDeletedString = new StringBuilder();

			EntityAccessPolicy entityAccessPolicy = AccessPolicy.getEntityPolicy(metadata.getEntity());

			String deletedFieldName = entityAccessPolicy.getData().getAttribute(SynchronizationField.DELETED.getSynchronizationField()).getName();

			includeDeletedString.append(deletedFieldName).append(" IS NULL");

			return includeDeletedString;
		}

		return null;
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
	private StringBuilder getDefaultOrdinationString(EntityOrdination ordination) throws ServerException {

		StringBuilder resultingOrdination = null;

		for(EntityOrderAttribute orderAttribute : ordination.getOrder()) {

			if (resultingOrdination == null) {
				resultingOrdination = new StringBuilder();
			}
			else {
				resultingOrdination.append(", ");
			}

			resultingOrdination.append(orderAttribute.getName());

			if (orderAttribute.getOrder() == Order.ASCENDING) {

				resultingOrdination.append(" ASC");
			}
			else if (orderAttribute.getOrder() == Order.DESCENDING) {

				resultingOrdination.append(" DESC");
			}
		}

		return resultingOrdination;
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
	 * Extrae el conjunto de datos del set de resultados y lo añade a la estructura de datos de la operación.   
	 * 
	 * @param resultSet Set de resultados del que se extrae la información.
	 * @param operation Operación en la que se añade el conjunto de datos extraido.
	 * 
	 * @return la operación poblada. 
	 * 
	 * @throws SQLException
	 * @throws DataAccessException
	 */
	private Operation extractData(ResultSet resultSet, Operation operation) throws SQLException, DataAccessException {

		long numItems = 0;
		AbstractList<DataItem> data = operation.getData();

		if (data != null && data.size() > 0) {

			DataItem referenceDataItem = new DataItem(data.get(0));

			if (referenceDataItem != null && referenceDataItem.getAttributes() != null && referenceDataItem.getAttributes().size() > 0) {

				int itemIndex = 0;

				if (resultSet.first()) {

					do
					{
						DataItem dataItem;

						if (data.size() <= itemIndex) {
							data.add(dataItem = new DataItem(referenceDataItem));
						}
						else {
							dataItem = data.get(itemIndex);
						}
						itemIndex++;

						int i=1;
						for (String attributeAlias : dataItem.getAttributes().keySet()) {

							Object value = resultSet.getObject(i++);

							dataItem.getAttributes().put(attributeAlias, resultSet.wasNull() ? null : value);
						}

						numItems++;
					}while(resultSet.next());
				}
			}
		}

		operation.getMetadata().setItems(numItems);

		return operation;
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

		EntityAccessPolicy entityAccessPolicy = AccessPolicy.getEntityPolicy(operation.getMetadata().getEntity());

		final EntityJDBCInformation entityInformation = entitiesInformation.get(entityAccessPolicy.getName().getName());

		querySelect.append("SELECT ");

		/*
		 * Especificación del conjunto de campos de la query.
		 */
		querySelect.append(getDataString(operation.getMetadata().getEntity(), operation.getData() != null && operation.getData().size() > 0 ? operation.getData().get(0).getAttributes() : null));

		/*
		 * Especificación de la tabla.
		 */
		queryFrom.append(" FROM ").append(AccessPolicy.getEntityName(operation.getMetadata().getEntity()));

		/*
		 * Exclusión de los registros eliminados.
		 */
		if (operation.getMetadata().getIncludeDeleted() == null || !operation.getMetadata().getIncludeDeleted()) {

			queryWhere.append(" WHERE (");
			queryWhere.append(getExcludeDeletedString(operation.getMetadata())).append(')');
		}

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

				if (queryWhere.length() == 0) {
					queryWhere.append(" WHERE ").append(key);
				}
				else {
					queryWhere.append(" AND (").append(key).append(')');
				}
			}
		}
		else if (operation.getMetadata().getFilter() != null) {
			filterColumns = new ArrayList<String>();
			filterValues = new ArrayList<Object>();

			StringBuilder filter = getFilterSetString(operation.getMetadata().getEntity(), operation.getMetadata().getFilter(), filterColumns, filterValues);

			if (filter != null && filter.length() > 0) {

				if (queryWhere.length() == 0) {
					queryWhere.append(" WHERE ").append(filter);
				}
				else {
					queryWhere.append(" AND (").append(filter).append(')');
				}
			}
		}
		else {
			throw new ServerException(ERROR_UNCOMPLETE_KEY_FILTER_REQUEST, ERROR_UNCOMPLETE_KEY_FILTER_REQUEST_MESSAGE);
		}

		if (entityAccessPolicy.getOrdination() != null) {

			queryWhere.append(" ORDER BY ").append(getDefaultOrdinationString(entityAccessPolicy.getOrdination()));
		}

		/*
		 *  Tratamiento del intervalo de datos a retornar.
		 *  
		 *  LIMIT items OFFSET offset
		 *  
		 *  Esta sintaxis es válida para: MySQL, MariaDB, PostgreSQL y hSQL.
		 *  No és válida para: Oracle y SQLServer.
		 */
		Long items = operation.getMetadata().getItems() == null ? entityAccessPolicy.getMaxPageSize() : operation.getMetadata().getItems();
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

		/*
		 * Selección de los registros.
		 */
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			preparedStatement = connection.prepareStatement(querySelect.toString());

			if (params != null) {

				for (int i=0; i<params.length; i++) {
					preparedStatement.setObject(i+1, params[i], types[i]);
				}
			}

			resultSet = preparedStatement.executeQuery();

			extractData(resultSet, operation);

			resultSet.close();
			preparedStatement.close();

			// Obtención del número total de registros que satisfacen la consulta.
			StringBuilder countQuery = new StringBuilder("SELECT COUNT(*) ").append(queryFrom).append(queryWhere);
			preparedStatement = connection.prepareStatement(countQuery.toString());

			if (params != null) {

				for (int i=0; i<params.length; i++) {
					preparedStatement.setObject(i+1, params[i], types[i]);
				}
			}

			resultSet = preparedStatement.executeQuery();
			if (resultSet.first()) {

				Long totalItems = resultSet.getLong(1);
				operation.getMetadata().setTotalItems(resultSet.wasNull() ? null : totalItems);
			}
			else {
				operation.getMetadata().setTotalItems(null);
			}
		}
		catch (Exception e) {
			throw new ServerException(GenericService.ERROR_OPERATION, String.format(GenericService.ERROR_OPERATION_MESSAGE, operation.getMetadata().getOperation().getOperationType(), operation.getMetadata().getEntity(), e.getMessage()));
		}
		finally {
			JDBCUtils.CloseQuietly(resultSet);
			JDBCUtils.CloseQuietly(preparedStatement);
		}

		return operation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Operation create(Requestor requestor, Operation operation) throws ServerException {

		if (operation.getData() == null || operation.getData().size() == 0) {
			throw new ServerException(GenericService.ERROR_INVALID_CREATE_REQUEST, String.format(GenericService.ERROR_INVALID_CREATE_REQUEST_MESSAGE, operation.getMetadata().getEntity()));
		}

		DataItem firsItem = operation.getData().get(0);
		if (firsItem.getAttributes() == null || firsItem.getAttributes().size() == 0) {
			throw new ServerException(GenericService.ERROR_INVALID_CREATE_REQUEST, String.format(GenericService.ERROR_INVALID_CREATE_REQUEST_MESSAGE, operation.getMetadata().getEntity()));
		}

		final StringBuilder queryInsert = new StringBuilder();

		EntityAccessPolicy entityAccessPolicy = AccessPolicy.getEntityPolicy(operation.getMetadata().getEntity());

		final EntityJDBCInformation entityInformation = entitiesInformation.get(entityAccessPolicy.getName().getName());

		/*
		 * Construcción de la sentencia de inserción.
		 */
		String revisionColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.REVISION.getSynchronizationField()).getName(); // REVISION
		String birthColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.BIRTH.getSynchronizationField()).getName(); // BIRTH
		String modifiedColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.MODIFIED.getSynchronizationField()).getName(); // MODIFIED
		String deletedColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.DELETED.getSynchronizationField()).getName(); // DELETED
		
		queryInsert.append("INSERT INTO ").append(entityAccessPolicy.getName().getName());

		queryInsert.append('(');

		queryInsert.append(revisionColumn);
		queryInsert.append(',').append(birthColumn);
		queryInsert.append(',').append(modifiedColumn);
		queryInsert.append(',').append(deletedColumn);

		int initSize = firsItem.getAttributes().size();
		AbstractList<String> inColumns = new ArrayList<String>(initSize);
		AbstractList<String> inAliases = new ArrayList<String>(initSize);
		AbstractList<String> outColumns = new ArrayList<String>(initSize);
		AbstractList<String> outAliases = new ArrayList<String>(initSize);

		queryInsert.append(',').append(getInsertDataString(operation.getMetadata().getEntity(), firsItem.getNonSystemAttributes(), entityInformation, inColumns, inAliases, outColumns, outAliases));

		queryInsert.append(") VALUES (?,?,?,?"); // REVISION, BIRTH, MODIFIED, DELETED

		for (int i=0; i<inColumns.size(); i++) {

			queryInsert.append(",?");
		}
		queryInsert.append(')');

		/*
		 * Inserción de los registros.
		 */
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			int revisionColumnType = entityInformation.dataFields.get(revisionColumn);
			int birthColumnType = entityInformation.dataFields.get(birthColumn);
			int modifiedColumnType = entityInformation.dataFields.get(modifiedColumn);
			int deletedColumnType = entityInformation.dataFields.get(deletedColumn);
			
			String[] generatedKeyColumns = outColumns.toArray(new String[outColumns.size()]);

			preparedStatement = connection.prepareStatement(queryInsert.toString(), generatedKeyColumns);

			Long millisecondsSinceEpoch = System.currentTimeMillis();
			Timestamp now = new Timestamp(millisecondsSinceEpoch);
			int i;
			NavigableMap<String, Object> itemAttributes;
			for (DataItem item :  operation.getData()) {

				item.getAttributes().put(SynchronizationField.REVISION.getSynchronizationField(), 0L);
				item.getAttributes().put(SynchronizationField.BIRTH.getSynchronizationField(), millisecondsSinceEpoch);
				item.getAttributes().put(SynchronizationField.MODIFIED.getSynchronizationField(), millisecondsSinceEpoch);
				item.getAttributes().put(SynchronizationField.DELETED.getSynchronizationField(), null);

				preparedStatement.setObject(1, 0L, revisionColumnType);
				preparedStatement.setObject(2, now, birthColumnType);
				preparedStatement.setObject(3, now, modifiedColumnType);
				preparedStatement.setObject(4, null, deletedColumnType);

				i=5;
				itemAttributes = item.getAttributes();

				for (String inAlias : inAliases) {

					preparedStatement.setObject(i++, itemAttributes.get(inAlias));
				}
				preparedStatement.addBatch();
			}
			preparedStatement.executeBatch();

			// Recogida de los datos generados.
			resultSet = preparedStatement.getGeneratedKeys();

			if (resultSet.first()) {

				for (DataItem item :  operation.getData()) {

					int GeneratedkeyIndex = 1;
					for(String outAlias : outAliases) {

						item.getAttributes().put(outAlias, resultSet.getObject(GeneratedkeyIndex++));
					}

					resultSet.next();
				}
			}

			resultSet.close();
			preparedStatement.close();

			// Información de totales.
			operation.getMetadata().setItems(Long.valueOf(operation.getData().size()));

			// Obtención del número total de registros que satisfacen la consulta.
			StringBuilder countQuery = new StringBuilder("SELECT COUNT(*) FROM ").append(entityAccessPolicy.getName().getName());
			preparedStatement = connection.prepareStatement(countQuery.toString());

			resultSet = preparedStatement.executeQuery();
			if (resultSet.first()) {

				Long totalItems = resultSet.getLong(1);
				operation.getMetadata().setTotalItems(resultSet.wasNull() ? null : totalItems);
			}
			else {
				operation.getMetadata().setTotalItems(null);
			}
		} 
		catch (Exception e) {
			throw new ServerException(GenericService.ERROR_OPERATION, String.format(GenericService.ERROR_OPERATION_MESSAGE, operation.getMetadata().getOperation().getOperationType(), operation.getMetadata().getEntity(), e.getMessage()));
		}
		finally {
			JDBCUtils.CloseQuietly(resultSet);
			JDBCUtils.CloseQuietly(preparedStatement);
		}

		return operation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Operation delete(Requestor requestor, Operation operation) throws ServerException {

		final StringBuilder queryDelete = new StringBuilder();
		final StringBuilder queryFrom = new StringBuilder();
		final StringBuilder queryWhere = new StringBuilder();

		EntityAccessPolicy entityAccessPolicy = AccessPolicy.getEntityPolicy(operation.getMetadata().getEntity());

		final EntityJDBCInformation entityInformation = entitiesInformation.get(entityAccessPolicy.getName().getName());

		Long millisecondsSinceEpoch = System.currentTimeMillis();
		Timestamp now = new Timestamp(millisecondsSinceEpoch);
		String deletedColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.DELETED.getSynchronizationField()).getName();

		/*
		 * Delete.
		 */
		queryDelete.append("UPDATE ").append(entityAccessPolicy.getName().getName()).append(" SET ").append(deletedColumn).append("=?"); // DELETED;

		/*
		 * Especificación de la tabla.
		 */
		queryFrom.append(" FROM ").append(entityAccessPolicy.getName().getName());

		/*
		 * Especificación del filtro.
		 */
		queryWhere.append(" WHERE (").append(deletedColumn).append(" IS NULL)");

		AbstractList<String> filterColumns = new ArrayList<String>();
		AbstractList<Object> filterValues = new ArrayList<Object>();
		Map<String, Integer> filterTypes = new HashMap<String, Integer>();

		StringBuilder filter = null;

		if (operation.getMetadata().getKey() != null && operation.getMetadata().getKey().size() > 0) {

			filterTypes = entityInformation.keyFields;
			filter = getKeyString(operation.getMetadata().getEntity(), operation.getMetadata().getKey(), filterColumns, filterValues);
		}
		else if (operation.getMetadata().getFilter() != null) {

			filterTypes = entityInformation.filterFields;
			filter = getFilterSetString(operation.getMetadata().getEntity(), operation.getMetadata().getFilter(), filterColumns, filterValues);
		}
		else {
			throw new ServerException(ERROR_UNCOMPLETE_KEY_FILTER_REQUEST, ERROR_UNCOMPLETE_KEY_FILTER_REQUEST_MESSAGE);
		}

		if (filter != null && filter.length() > 0) {

			queryWhere.append(" AND (").append(filter).append(')');
		}

		int[] types = new int[filterColumns.size()];
		Object[] params = new Object[types.length];

		for (int i=0; i<filterColumns.size(); i++) {

			types[i] = filterTypes.get(filterColumns.get(i));
			params[i] = translateToType(types[i], filterValues.get(i));
		}

		queryDelete.append(queryWhere);

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			/*
			 * Modificación de los registros.
			 */
			preparedStatement = connection.prepareStatement(queryDelete.toString());

			int deletedType = entityInformation.dataFields.get(deletedColumn);
			now.setNanos(0); // Para evitar problemas de compatibilidad entre la escritura en DB y la lectura o selección.
			preparedStatement.setObject(1, now, deletedType);

			if (params != null) {

				for (int i=0; i<params.length; i++) {
					preparedStatement.setObject(i+2, params[i], types[i]);
				}
			}

			operation.getMetadata().setItems(Long.valueOf(preparedStatement.executeUpdate()));

			preparedStatement.close();

			/*
			 * Obtiene los registros marcados como eliminados
			 */
			if (operation.getData() != null && !operation.getData().isEmpty()) {

				StringBuilder querySelect = new StringBuilder();

				querySelect.append("SELECT ");

				/*
				 * Especificación del conjunto de campos de la query.
				 */
				querySelect.append(getDataString(operation.getMetadata().getEntity(), operation.getData().get(0).getAttributes()));

				/*
				 * Especificación de la tabla.
				 */
				querySelect.append(queryFrom);

				/*
				 * Especificación de los filtros.
				 */
				querySelect.append(" WHERE ").append(deletedColumn).append("=? AND (").append(filter).append(')');

				preparedStatement = connection.prepareStatement(querySelect.toString());

				preparedStatement.setObject(1, now, deletedType);

				if (params != null) {

					for (int i=0; i<params.length; i++) {
						preparedStatement.setObject(i+2, params[i], types[i]);
					}
				}

				resultSet = preparedStatement.executeQuery();

				extractData(resultSet, operation);

				resultSet.close();
				preparedStatement.close();
			}

			/*
			 *  Obtención del número total de registros sin marca de eliminación.
			 */
			StringBuilder countQuery = new StringBuilder("SELECT COUNT(*) ").append(queryFrom).append(" WHERE ").append(deletedColumn).append(" IS NULL");
			preparedStatement = connection.prepareStatement(countQuery.toString());

			resultSet = preparedStatement.executeQuery();
			if (resultSet.first()) {

				Long totalItems = resultSet.getLong(1);
				operation.getMetadata().setTotalItems(resultSet.wasNull() ? null : totalItems);
			}
			else {
				operation.getMetadata().setTotalItems(null);
			}
		}
		catch (Exception e) {
			throw new ServerException(GenericService.ERROR_OPERATION, String.format(GenericService.ERROR_OPERATION_MESSAGE, operation.getMetadata().getOperation().getOperationType(), operation.getMetadata().getEntity(), e.getMessage()));
		}
		finally {
			JDBCUtils.CloseQuietly(resultSet);
			JDBCUtils.CloseQuietly(preparedStatement);
		}

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

		javax.sql.DataSource dataSource = DataSources.getDataSourceDataService();
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
					entityJDBCInformation.generatedData = new HashSet<String>();
					ResultSet columns = null;

					try {
						columns = connection.getMetaData().getColumns(entityJDBCInformation.catalog, entityJDBCInformation.schema, entityJDBCInformation.name, null);

						if (columns.first()) {
							do {
								String columnName = columns.getString("COLUMN_NAME");
								int columnType = columns.getInt("DATA_TYPE"); // => SQL type from java.sql.Types
								if (columns.getBoolean("IS_AUTOINCREMENT") || columns.getBoolean("IS_GENERATEDCOLUMN")) {
									entityJDBCInformation.generatedData.add(columnName);
								}

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
					if (entityJDBCInformation.dataFields.size() < entityAccessPolicy.getData().getData().size()) {

						StringBuilder lostFields = null;

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
								if (lostFields == null) {
									lostFields = new StringBuilder();
								}
								else {
									lostFields.append(", ");	
								}

								lostFields.append(fieldName);
							}
						}

						if (lostFields != null) {
							throw new ServerException(GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED, GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No se han encontrado las columnas " + lostFields + " para la tabla "  + table + ".");
						}
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

				// Atributos de las ordenaciones de la entidad.
				if (entityAccessPolicy.getOrdination() != null && entityAccessPolicy.getOrdination().getOrder().size() > 0) {

					AbstractList<EntityOrderAttribute> order = entityAccessPolicy.getOrdination().getOrder();

					StringBuilder illegalAttributes = null;

					for(EntityOrderAttribute orderAttribute : order) {

						if (!entityAccessPolicy.getData().hasAttribute(orderAttribute.getAlias())) {

							if (illegalAttributes == null) {
								illegalAttributes = new StringBuilder(orderAttribute.getAlias());
							}
							else {
								illegalAttributes.append(", ").append(orderAttribute.getAlias());	
							}
						}
					}

					if (illegalAttributes != null) {
						throw new ServerException(GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED, GenericService.ERROR_ACCESS_POLICY_CHECK_FAILED_MESSAGE + " No se han definido los atributos correspondientes en la sección de datos, para los atributos de ordenación <" + illegalAttributes + "> de la entidad "  + entityAccessPolicy.getName().getAlias() + ".");
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
}
