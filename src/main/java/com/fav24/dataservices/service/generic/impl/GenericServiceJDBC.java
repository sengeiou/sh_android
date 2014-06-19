package com.fav24.dataservices.service.generic.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.domain.datasource.DataSources;
import com.fav24.dataservices.domain.generic.DataItem;
import com.fav24.dataservices.domain.generic.Operation;
import com.fav24.dataservices.domain.security.AccessPolicy;
import com.fav24.dataservices.domain.security.EntityAccessPolicy;
import com.fav24.dataservices.domain.security.EntityDataAttribute;
import com.fav24.dataservices.domain.security.EntityDataAttribute.SynchronizationField;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.generic.GenericService;
import com.fav24.dataservices.service.generic.impl.GenericServiceDataSourceInfo.EntityDataSourceInfo;
import com.fav24.dataservices.util.JDBCUtils;


/**
 * Versión JDBC de la interfaz de servicio Generic. 
 */
@Scope("singleton")
@Component
public class GenericServiceJDBC extends GenericServiceBasic<Connection> {

	@Autowired
	private GenericServiceDataSourceInfoJDBC entitiesInformation;


	/**
	 * Constructor por defecto.
	 */
	public GenericServiceJDBC() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Connection startTransaction() throws ServerException {

		javax.sql.DataSource dataSource = DataSources.getDataSourceDataService();

		try {
			return dataSource.getConnection();
		}		
		catch (Throwable t) {
			throw new ServerException(GenericService.ERROR_START_TRANSACTION, String.format(GenericService.ERROR_START_TRANSACTION_MESSAGE, t.getMessage()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void endTransaction(Connection connection, boolean commit) throws ServerException {

		if (connection != null) {

			try {

				if (!connection.getAutoCommit()) {

					if (commit) {

						connection.commit();
					}
					else {

						connection.rollback();
					}
				}
			}
			catch(Throwable t) {
				throw new ServerException(GenericService.ERROR_END_TRANSACTION, String.format(GenericService.ERROR_END_TRANSACTION_MESSAGE, t.getMessage()));	
			}
			finally {
				JDBCUtils.CloseQuietly(connection);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Operation retreave(Connection connection, EntityAccessPolicy entityAccessPolicy, Operation operation) throws ServerException {

		StringBuilder querySelect = new StringBuilder();
		StringBuilder queryFrom = new StringBuilder();
		StringBuilder queryWhere = new StringBuilder();
		StringBuilder queryLimit = new StringBuilder();

		EntityDataSourceInfo entityInformation = entitiesInformation.getEntity(entityAccessPolicy.getName().getName());

		/*
		 * Especificación del conjunto de campos de la query.
		 */
		StringBuilder dataString = null;

		if (operation.getData() != null && operation.getData().size() > 0) {
			dataString = GenericServiceJDBCHelper.getDataString(entityAccessPolicy, operation.getData().get(0).getAttributes());
		}

		if (dataString != null) {
			querySelect.append("SELECT ").append(dataString);
		}

		/*
		 * Especificación de la tabla.
		 */
		queryFrom.append(" FROM ").append(AccessPolicy.getEntityName(operation.getMetadata().getEntity()));

		/*
		 * Exclusión de los registros eliminados.
		 */
		if (operation.getMetadata().getIncludeDeleted() == null || !operation.getMetadata().getIncludeDeleted()) {

			queryWhere.append(" WHERE (");
			queryWhere.append(GenericServiceJDBCHelper.getExcludeDeletedString(entityAccessPolicy)).append(')');
		}

		/*
		 * Especificación del filtro.
		 */
		AbstractList<String> keyColumns = null, filterColumns = null;
		AbstractList<Object> keyValues = null, filterValues = null;

		if (operation.getMetadata().getKey() != null && operation.getMetadata().getKey().size() > 0) {
			keyColumns = new ArrayList<String>();
			keyValues = new ArrayList<Object>();

			StringBuilder key = GenericServiceJDBCHelper.getKeyString(entityAccessPolicy, operation.getMetadata().getKey(), keyColumns, keyValues);

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

			StringBuilder filter = GenericServiceJDBCHelper.getFilterSetString(entityAccessPolicy, operation.getMetadata().getFilter(), filterColumns, filterValues);

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

			queryWhere.append(" ORDER BY ").append(GenericServiceJDBCHelper.getDefaultOrdinationString(entityAccessPolicy.getOrdination()));
		}

		if (dataString != null) {
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
		}
		else {
			operation.getMetadata().setItems(0L);
			operation.getMetadata().setOffset(0L);
		}

		Object[] params = null;
		int[] types = null;

		if (keyColumns != null && keyColumns.size() > 0) {

			types = new int[keyColumns.size()];
			params = new Object[types.length];

			for (int i=0; i<keyColumns.size(); i++) {

				Integer type = entityInformation.keyFields == null ? null : entityInformation.keyFields.get(keyColumns.get(i));
				if (type == null) {
					throw new ServerException(GenericService.ERROR_INVALID_REQUEST_KEY_ATTRIBUTE, String.format(GenericService.ERROR_INVALID_REQUEST_KEY_ATTRIBUTE_MESSAGE, entityAccessPolicy.getName().getAlias(), entityAccessPolicy.getAttributeAlias(keyColumns.get(i))));
				}
				types[i] = type;
				params[i] = GenericServiceJDBCHelper.translateToType(types[i], keyValues.get(i));
			}
		}
		else if (filterColumns != null && filterColumns.size() > 0) {
			types = new int[filterColumns.size()];
			params = new Object[types.length];

			for (int i=0; i<filterColumns.size(); i++) {

				Integer type = entityInformation.filterFields == null ? null : entityInformation.filterFields.get(filterColumns.get(i));
				if (type == null) {
					throw new ServerException(GenericService.ERROR_INVALID_REQUEST_FILTER_ATTRIBUTE, String.format(GenericService.ERROR_INVALID_REQUEST_FILTER_ATTRIBUTE_MESSAGE, entityAccessPolicy.getName().getAlias(), entityAccessPolicy.getAttributeAlias(filterColumns.get(i))));
				}
				types[i] = type;
				params[i] = GenericServiceJDBCHelper.translateToType(types[i], filterValues.get(i));
			}
		}

		/*
		 * Selección de los registros.
		 */
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			if (dataString != null) {

				querySelect.append(queryFrom).append(queryWhere).append(queryLimit);
				preparedStatement = connection.prepareStatement(querySelect.toString());

				if (params != null) {

					for (int i=0; i<params.length; i++) {

						Object value = params[i];

						if (value == null) {

							preparedStatement.setNull(i+1, types[i]);
						}
						else {

							preparedStatement.setObject(i+1, value, types[i]);
						}
					}
				}

				resultSet = preparedStatement.executeQuery();

				operation.getMetadata().setItems(GenericServiceJDBCHelper.extractData(resultSet, entityAccessPolicy, entityInformation, operation.getData()));

				resultSet.close();
				preparedStatement.close();
			}

			// Obtención del número total de registros total sin contar la paginación.
			StringBuilder countQuery = new StringBuilder("SELECT COUNT(*) ").append(queryFrom).append(queryWhere);
			preparedStatement = connection.prepareStatement(countQuery.toString());

			if (params != null) {

				for (int i=0; i<params.length; i++) {

					Object value = params[i];

					if (value == null) {

						preparedStatement.setNull(i+1, types[i]);
					}
					else {

						preparedStatement.setObject(i+1, value, types[i]);
					}
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
		catch (Throwable t) {
			throw new ServerException(GenericService.ERROR_OPERATION, String.format(GenericService.ERROR_OPERATION_MESSAGE, operation.getMetadata().getOperation().getOperationType(), operation.getMetadata().getEntity(), t.getMessage()));
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
	protected Operation create(Connection connection, EntityAccessPolicy entityAccessPolicy, Operation operation) throws ServerException {

		if (operation.getData() == null || operation.getData().size() == 0) {
			throw new ServerException(GenericService.ERROR_INVALID_CREATE_REQUEST, String.format(GenericService.ERROR_INVALID_CREATE_REQUEST_MESSAGE, operation.getMetadata().getEntity()));
		}

		DataItem firsItem = operation.getData().get(0);
		if (firsItem.getAttributes() == null || firsItem.getAttributes().size() == 0) {
			throw new ServerException(GenericService.ERROR_INVALID_CREATE_REQUEST, String.format(GenericService.ERROR_INVALID_CREATE_REQUEST_MESSAGE, operation.getMetadata().getEntity()));
		}

		StringBuilder queryInsert = new StringBuilder();

		EntityDataSourceInfo entityInformation = entitiesInformation.getEntity(entityAccessPolicy.getName().getName());

		/*
		 * Construcción de la sentencia de inserción.
		 */
		String revisionColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.REVISION.getSynchronizationField()).getName(); // REVISION
		String birthColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.BIRTH.getSynchronizationField()).getName(); // BIRTH
		String modifiedColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.MODIFIED.getSynchronizationField()).getName(); // MODIFIED
		String deletedColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.DELETED.getSynchronizationField()).getName(); // DELETED

		queryInsert.append("INSERT INTO ").append(entityAccessPolicy.getName().getName());

		queryInsert.append(" (");

		GenericServiceJDBCHelper.scapeColumn(queryInsert, revisionColumn).append(',');
		GenericServiceJDBCHelper.scapeColumn(queryInsert, birthColumn).append(',');
		GenericServiceJDBCHelper.scapeColumn(queryInsert, modifiedColumn).append(',');
		GenericServiceJDBCHelper.scapeColumn(queryInsert, deletedColumn);

		int initSize = firsItem.getAttributes().size();
		AbstractList<String> inColumns = new ArrayList<String>(initSize);
		AbstractList<String> inAliases = new ArrayList<String>(initSize);
		AbstractList<String> outColumns = new ArrayList<String>(initSize);
		AbstractList<String> outAliases = new ArrayList<String>(initSize);

		StringBuilder columnsToInsert = GenericServiceJDBCHelper.getInsertDataString(entityAccessPolicy, firsItem.getNonSystemAttributes(), entityInformation, inColumns, inAliases, outColumns, outAliases);
		
		if (columnsToInsert == null) {
			throw new ServerException(GenericService.ERROR_INVALID_READONLY_POLICY, String.format(GenericService.ERROR_INVALID_READONLY_POLICY_MESSAGE, operation.getMetadata().getEntity()));	
		}

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

				itemAttributes = item.getAttributes();

				// Se actualiza el contenido del item.
				itemAttributes.put(SynchronizationField.REVISION.getSynchronizationField(), EntityDataAttribute.DEFAFULT_REVISION);
				itemAttributes.put(SynchronizationField.BIRTH.getSynchronizationField(), millisecondsSinceEpoch);
				itemAttributes.put(SynchronizationField.MODIFIED.getSynchronizationField(), millisecondsSinceEpoch);
				itemAttributes.put(SynchronizationField.DELETED.getSynchronizationField(), null);

				// Se asignan los valores a la sentencia.
				preparedStatement.setObject(1, EntityDataAttribute.DEFAFULT_REVISION, revisionColumnType);
				preparedStatement.setObject(2, now, birthColumnType);
				preparedStatement.setObject(3, now, modifiedColumnType);
				preparedStatement.setNull(4, deletedColumnType);

				i=5;

				for (String inAlias : inAliases) {

					Object value = itemAttributes.get(inAlias);

					if (value == null) {

						preparedStatement.setNull(i, entityInformation.dataFields.get(inAlias));
					}
					else {

						preparedStatement.setObject(i, value, entityInformation.dataFields.get(inAlias));
					}

					i++;
				}

				boolean isRefurbishedRow = false;

				try {
					preparedStatement.executeUpdate();
				}
				catch (SQLException e) {

					/*
					 *  Si se trata de una constrain violada, se considera que el registro ya existe y se intenta recuperar en caso de estar marcadao como eliminado.
					 *  En cualquier otro caso, se relanzará la excepción.
					 */
					if (JDBCUtils.IsIntegrityConstraintViolation(entitiesInformation.getProduct(), e)) {

						isRefurbishedRow = true;

						/*
						 * En caso de no estar marcado como eliminado o no ser posible su recuperación por colisión entre claves, se lanzará una excepción.
						 */
						GenericServiceJDBCHelper.recoverRowFromInsert(connection, item, entityAccessPolicy, entityInformation);
					}
					else {
						if (preparedStatement != null) {
							preparedStatement.close();
						}
						throw e;
					}
				}

				// Recogida de los datos generados.
				if (!isRefurbishedRow && generatedKeyColumns.length > 0) {

					resultSet = preparedStatement.getGeneratedKeys();

					if (resultSet.first()) {

						int GeneratedkeyIndex = 1;

						for(String outAlias : outAliases) {

							itemAttributes.put(outAlias, resultSet.getObject(GeneratedkeyIndex++));
						}

						resultSet.close();
					}
				}

				preparedStatement.clearParameters();
			}

			// Información de totales.
			operation.getMetadata().setItems(Long.valueOf(operation.getData().size()));

			// Obtención del número total de registros total de la entidad.
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
		catch (Throwable t) {
			throw new ServerException(GenericService.ERROR_OPERATION, String.format(GenericService.ERROR_OPERATION_MESSAGE, operation.getMetadata().getOperation().getOperationType(), operation.getMetadata().getEntity(), t.getMessage()));
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
	protected Operation delete(Connection connection, EntityAccessPolicy entityAccessPolicy, Operation operation) throws ServerException {

		StringBuilder queryDelete = new StringBuilder();
		StringBuilder queryFrom = new StringBuilder();
		StringBuilder queryWhere = new StringBuilder();

		EntityDataSourceInfo entityInformation = entitiesInformation.getEntity(entityAccessPolicy.getName().getName());

		String deletedColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.DELETED.getSynchronizationField()).getName();

		/*
		 * Delete.
		 */
		queryDelete.append("UPDATE ").append(entityAccessPolicy.getName().getName()).append(" SET ");
		// Se asigna la marca de eliminación.
		GenericServiceJDBCHelper.scapeColumn(queryDelete, deletedColumn).append("=?"); // DELETED;

		/*
		 * Especificación de la tabla.
		 */
		queryFrom.append(" FROM ").append(entityAccessPolicy.getName().getName());

		/*
		 * Especificación del filtro.
		 */
		queryWhere.append(" WHERE (");
		GenericServiceJDBCHelper.scapeColumn(queryWhere, deletedColumn).append(" IS NULL)"); // DELETED;

		AbstractList<String> filterColumns = new ArrayList<String>();
		AbstractList<Object> filterValues = new ArrayList<Object>();
		Map<String, Integer> filterTypes = new HashMap<String, Integer>();

		StringBuilder filter = null;

		if (operation.getMetadata().getKey() != null && operation.getMetadata().getKey().size() > 0) {

			filterTypes = entityInformation.keyFields;
			filter = GenericServiceJDBCHelper.getKeyString(entityAccessPolicy, operation.getMetadata().getKey(), filterColumns, filterValues);
		}
		else if (operation.getMetadata().getFilter() != null) {

			filterTypes = entityInformation.filterFields;
			filter = GenericServiceJDBCHelper.getFilterSetString(entityAccessPolicy, operation.getMetadata().getFilter(), filterColumns, filterValues);
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

			Integer type = filterTypes.get(filterColumns.get(i));
			if (type == null) {

				if (operation.getMetadata().getKey() != null && operation.getMetadata().getKey().size() > 0) {

					throw new ServerException(GenericService.ERROR_INVALID_REQUEST_KEY_ATTRIBUTE, String.format(GenericService.ERROR_INVALID_REQUEST_KEY_ATTRIBUTE_MESSAGE, entityAccessPolicy.getName().getAlias(), entityAccessPolicy.getAttributeAlias(filterColumns.get(i))));
				}
				else {

					throw new ServerException(GenericService.ERROR_INVALID_REQUEST_FILTER_ATTRIBUTE, String.format(GenericService.ERROR_INVALID_REQUEST_FILTER_ATTRIBUTE_MESSAGE, entityAccessPolicy.getName().getAlias(), entityAccessPolicy.getAttributeAlias(filterColumns.get(i))));
				}
			}

			types[i] = type;
			params[i] = GenericServiceJDBCHelper.translateToType(types[i], filterValues.get(i));
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
			Timestamp now = new Timestamp(System.currentTimeMillis());
			now.setNanos(0); // Para evitar problemas de compatibilidad entre la escritura en DB y la lectura o selección.
			preparedStatement.setObject(1, now, deletedType);

			if (params != null) {

				for (int i=0; i<params.length; i++) {

					Object value = params[i];

					if (value == null) {

						preparedStatement.setNull(i+2, types[i]);
					}
					else {

						preparedStatement.setObject(i+2, value, types[i]);
					}
				}
			}

			operation.getMetadata().setItems(Long.valueOf(preparedStatement.executeUpdate()));

			preparedStatement.close();

			/*
			 * Obtiene los registros marcados como eliminados
			 */
			if (operation.getData() != null && !operation.getData().isEmpty()) {

				DataItem referenceDataItem = operation.getData().get(0);

				StringBuilder dataString = GenericServiceJDBCHelper.getDataString(entityAccessPolicy, referenceDataItem.getAttributes());

				if (dataString == null) {

					StringBuilder querySelect = new StringBuilder();

					querySelect.append("SELECT ");

					/*
					 * Especificación del conjunto de campos de la query.
					 */
					querySelect.append(dataString);

					/*
					 * Especificación de la tabla.
					 */
					querySelect.append(queryFrom);

					/*
					 * Especificación de los filtros.
					 */
					querySelect.append(" WHERE ");
					GenericServiceJDBCHelper.scapeColumn(querySelect, deletedColumn).append("=? AND (").append(filter).append(')');

					preparedStatement = connection.prepareStatement(querySelect.toString());

					preparedStatement.setObject(1, now, deletedType);

					if (params != null) {

						for (int i=0; i<params.length; i++) {

							Object value = params[i];

							if (value == null) {

								preparedStatement.setNull(i+2, types[i]);
							}
							else {

								preparedStatement.setObject(i+2, value, types[i]);
							}
						}
					}

					resultSet = preparedStatement.executeQuery();

					operation.getMetadata().setItems(GenericServiceJDBCHelper.extractData(resultSet, entityAccessPolicy, entityInformation, operation.getData()));

					resultSet.close();
					preparedStatement.close();
				}
				else {
					while(operation.getData().size() < operation.getMetadata().getItems()) {
						operation.getData().add(new DataItem(referenceDataItem));
					}
				}
			}
			else {

				if (operation.getData() == null) {
					operation.setData(new ArrayList<DataItem>(operation.getMetadata().getItems().intValue()));
				}

				while(operation.getData().size() < operation.getMetadata().getItems()) {
					operation.getData().add(new DataItem());
				}
			}

			/*
			 *  Obtención del número total de registros sin marca de eliminación.
			 */
			StringBuilder countQuery = new StringBuilder("SELECT COUNT(*) ").append(queryFrom).append(" WHERE ");
			GenericServiceJDBCHelper.scapeColumn(countQuery, deletedColumn).append(" IS NULL");
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
		catch (Throwable t) {
			throw new ServerException(GenericService.ERROR_OPERATION, String.format(GenericService.ERROR_OPERATION_MESSAGE, operation.getMetadata().getOperation().getOperationType(), operation.getMetadata().getEntity(), t.getMessage()));
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
	protected Operation update(Connection connection, EntityAccessPolicy entityAccessPolicy, Operation operation) throws ServerException {

		if (operation.getData() == null || operation.getData().size() == 0) {
			throw new ServerException(GenericService.ERROR_INVALID_UPDATE_REQUEST, String.format(GenericService.ERROR_INVALID_UPDATE_REQUEST_MESSAGE, operation.getMetadata().getEntity()));
		}

		DataItem referenceItem = new DataItem(operation.getData().get(0));

		if (referenceItem.getAttributes() == null || referenceItem.getAttributes().size() == 0) {
			throw new ServerException(GenericService.ERROR_INVALID_UPDATE_REQUEST, String.format(GenericService.ERROR_INVALID_UPDATE_REQUEST_MESSAGE, operation.getMetadata().getEntity()));
		}

		EntityDataSourceInfo entityInformation = entitiesInformation.getEntity(entityAccessPolicy.getName().getName());

		if (entityInformation.primaryKey == null || entityInformation.primaryKey.size() == 0) {
			throw new ServerException(GenericService.ERROR_UPDATE_ENTITY_LACKS_PRIMARY_KEY, String.format(GenericService.ERROR_UPDATE_ENTITY_LACKS_PRIMARY_KEY_MESSAGE, operation.getMetadata().getEntity()));
		}

		/*
		 * Realizar una selección de los registros a modificar.
		 * 
		 * Columnas a incluir: 
		 * - Conjunto completo de atributos de auditoría: #SynchronizationField.REVISION, #SynchronizationField.BIRTH, #SynchronizationField.MODIFIED, #SynchronizationField.DELETED.
		 * - Conjunto de attributos que conforman la clave primaria.
		 * - Atributos con #Direction.OUTPUT. (Para poder informar los atributos de solo salida en la operación de vuelta)
		 * 
		 * Filtrado:
		 * - Campo csys_deleted a IS NULL, para garantizar que únicamente se permiten modificar elementos eliminados.
		 * - Criterios indicados en la petición, para cumplir con lo solicitado.
		 */

		StringBuilder querySelect = new StringBuilder("SELECT ");

		querySelect.append(GenericServiceJDBCHelper.getSelectForUpdateDataString(entityAccessPolicy, referenceItem.getAttributes(), entityInformation));

		StringBuilder queryFrom = new StringBuilder(" FROM ").append(entityAccessPolicy.getName().getName());

		String deletedColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.DELETED.getSynchronizationField()).getName(); // DELETED
		StringBuilder queryWhere = GenericServiceJDBCHelper.scapeColumn(new StringBuilder(" WHERE "), deletedColumn).append(" IS NULL");

		/*
		 * Especificación del filtro.
		 */
		queryWhere.append(" AND (");

		AbstractList<String> columns = new ArrayList<String>();
		AbstractList<Object> values = new ArrayList<Object>();

		boolean useKeys = operation.getMetadata().getKey() != null && operation.getMetadata().getKey().size() > 0;

		if (useKeys) {

			StringBuilder key = GenericServiceJDBCHelper.getKeyString(entityAccessPolicy, operation.getMetadata().getKey(), columns, values);

			queryWhere.append(key);
		}
		else if (operation.getMetadata().getFilter() != null) {

			StringBuilder filter = GenericServiceJDBCHelper.getFilterSetString(entityAccessPolicy, operation.getMetadata().getFilter(), columns, values);

			queryWhere.append(filter);
		}
		else {
			throw new ServerException(ERROR_UNCOMPLETE_KEY_FILTER_REQUEST, ERROR_UNCOMPLETE_KEY_FILTER_REQUEST_MESSAGE);
		}

		queryWhere.append(')');

		int[] types = new int[columns.size()];
		Object[] params = new Object[types.length];

		if (useKeys) {

			for (int i=0; i<columns.size(); i++) {
				types[i] = entityInformation.keyFields.get(columns.get(i));
				params[i] = GenericServiceJDBCHelper.translateToType(types[i], values.get(i));
			}
		}
		else {

			for (int i=0; i<columns.size(); i++) {
				types[i] = entityInformation.filterFields.get(columns.get(i));
				params[i] = GenericServiceJDBCHelper.translateToType(types[i], values.get(i));
			}
		}

		/*
		 * Selección de los registros.
		 */
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			querySelect.append(queryFrom).append(queryWhere);

			preparedStatement = connection.prepareStatement(querySelect.toString());

			for (int i=0; i<params.length; i++) {

				Object value = params[i];

				if (value == null) {

					preparedStatement.setNull(i+1, types[i]);
				}
				else {

					preparedStatement.setObject(i+1, value, types[i]);
				}
			}

			/*
			 *  Sello temporal que se usará para la actualización de todos los registros entrantes que "ganen". 
			 */
			Timestamp now = new Timestamp(System.currentTimeMillis());
			now.setNanos(0);

			resultSet = preparedStatement.executeQuery();

			long numItemns = 0;
			if (resultSet.first()) {

				operation.getData().clear();

				do {

					/*
					 * Modificación registro a registro.
					 */
					DataItem dataItem = new DataItem(referenceItem);

					if (GenericServiceJDBCHelper.update(connection, resultSet, now, dataItem.getAttributes(), entityAccessPolicy, entityInformation)) {

						operation.getData().add(dataItem);
						numItemns++;
					}

				} while (resultSet.next());
			}

			operation.getMetadata().setItems(numItemns);

			resultSet.close();
			preparedStatement.close();

			/*
			 *  Obtención del número total de registros sin marca de eliminación.
			 */
			StringBuilder countQuery = new StringBuilder("SELECT COUNT(*) ").append(queryFrom).append(" WHERE ");

			GenericServiceJDBCHelper.scapeColumn(countQuery, deletedColumn).append(" IS NULL");

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
		catch (Throwable t) {
			throw new ServerException(GenericService.ERROR_OPERATION, String.format(GenericService.ERROR_OPERATION_MESSAGE, operation.getMetadata().getOperation().getOperationType(), operation.getMetadata().getEntity(), t.getMessage()));
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
	protected Operation createUpdate(Connection connection, EntityAccessPolicy entityAccessPolicy, Operation operation) throws ServerException {

		if (operation.getData() == null || operation.getData().size() == 0) {
			throw new ServerException(GenericService.ERROR_INVALID_CREATEUPDATE_REQUEST, String.format(GenericService.ERROR_INVALID_CREATEUPDATE_REQUEST_MESSAGE, operation.getMetadata().getEntity()));
		}

		DataItem firsItem = operation.getData().get(0);
		if (firsItem.getAttributes() == null || firsItem.getAttributes().size() == 0) {
			throw new ServerException(GenericService.ERROR_INVALID_CREATEUPDATE_REQUEST, String.format(GenericService.ERROR_INVALID_CREATEUPDATE_REQUEST_MESSAGE, operation.getMetadata().getEntity()));
		}

		StringBuilder queryInsert = new StringBuilder();

		EntityDataSourceInfo entityInformation = entitiesInformation.getEntity(entityAccessPolicy.getName().getName());

		/*
		 * Construcción de la sentencia de inserción.
		 */
		String revisionColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.REVISION.getSynchronizationField()).getName(); // REVISION
		String birthColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.BIRTH.getSynchronizationField()).getName(); // BIRTH
		String modifiedColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.MODIFIED.getSynchronizationField()).getName(); // MODIFIED
		String deletedColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.DELETED.getSynchronizationField()).getName(); // DELETED

		queryInsert.append("INSERT INTO ").append(entityAccessPolicy.getName().getName());

		queryInsert.append(" (");

		GenericServiceJDBCHelper.scapeColumn(queryInsert, revisionColumn).append(',');
		GenericServiceJDBCHelper.scapeColumn(queryInsert, birthColumn).append(',');
		GenericServiceJDBCHelper.scapeColumn(queryInsert, modifiedColumn).append(',');
		GenericServiceJDBCHelper.scapeColumn(queryInsert, deletedColumn);

		int initSize = firsItem.getAttributes().size();
		AbstractList<String> inColumns = new ArrayList<String>(initSize);
		AbstractList<String> inAliases = new ArrayList<String>(initSize);
		AbstractList<String> outColumns = new ArrayList<String>(initSize);
		AbstractList<String> outAliases = new ArrayList<String>(initSize);

		StringBuilder columnsToInsert = GenericServiceJDBCHelper.getInsertDataString(entityAccessPolicy, firsItem.getNonSystemAttributes(), entityInformation, inColumns, inAliases, outColumns, outAliases);
		
		if (columnsToInsert == null) {
			throw new ServerException(GenericService.ERROR_INVALID_READONLY_POLICY, String.format(GenericService.ERROR_INVALID_READONLY_POLICY_MESSAGE, operation.getMetadata().getEntity()));	
		}
		
		queryInsert.append(',').append(columnsToInsert);

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

				// Se asignan los valores a la sentencia.
				preparedStatement.setObject(1, EntityDataAttribute.DEFAFULT_REVISION, revisionColumnType);
				preparedStatement.setObject(2, now, birthColumnType);
				preparedStatement.setObject(3, now, modifiedColumnType);
				preparedStatement.setNull(4, deletedColumnType);

				i=5;
				itemAttributes = item.getAttributes();

				for (String inAlias : inAliases) {

					Object value = itemAttributes.get(inAlias);

					if (value == null) {

						preparedStatement.setNull(i, entityInformation.dataFields.get(inAlias));
					}
					else {

						preparedStatement.setObject(i, value, entityInformation.dataFields.get(inAlias));
					}

					i++;
				}

				boolean isRefurbishedOrExistingRow = false;

				try {
					preparedStatement.executeUpdate();

					// Se actualiza el contenido del item en caso de no haber ningún problema.
					item.getAttributes().put(SynchronizationField.REVISION.getSynchronizationField(), EntityDataAttribute.DEFAFULT_REVISION);
					item.getAttributes().put(SynchronizationField.BIRTH.getSynchronizationField(), millisecondsSinceEpoch);
					item.getAttributes().put(SynchronizationField.MODIFIED.getSynchronizationField(), millisecondsSinceEpoch);
					item.getAttributes().put(SynchronizationField.DELETED.getSynchronizationField(), null);
				}
				catch (SQLException e) {

					/*
					 *  Si se trata de una constrain violada, se considera que el registro ya existe y se intenta recuperar en caso de estar marcadao como eliminado.
					 *  En caso de no estar eliminado, se modifica el registro localizado si sólo es 1. En caso de localizarse varios, se relanzará la excepción.
					 */
					if (JDBCUtils.IsIntegrityConstraintViolation(entitiesInformation.getProduct(), e)) {

						isRefurbishedOrExistingRow = true;

						/*
						 * En caso de no estar marcado como eliminado o no ser posible su recuperación por colisión entre claves, se lanzará una excepción.
						 */
						GenericServiceJDBCHelper.recoverOrUpdateRowFromInsert(connection, now, item, entityAccessPolicy, entityInformation);
					}
					else {
						if (preparedStatement != null) {
							preparedStatement.close();
						}
						throw e;
					}
				}

				// Recogida de los datos generados.
				if (!isRefurbishedOrExistingRow && generatedKeyColumns.length > 0) {

					resultSet = preparedStatement.getGeneratedKeys();

					if (resultSet.first()) {

						int GeneratedkeyIndex = 1;

						for(String outAlias : outAliases) {

							item.getAttributes().put(outAlias, resultSet.getObject(GeneratedkeyIndex++));
						}

						resultSet.close();
					}
				}

				preparedStatement.clearParameters();
			}

			// Información de totales.
			operation.getMetadata().setItems(Long.valueOf(operation.getData().size()));

			// Obtención del número total de registros total de la entidad.
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
		catch (Throwable t) {
			throw new ServerException(GenericService.ERROR_OPERATION, String.format(GenericService.ERROR_OPERATION_MESSAGE, operation.getMetadata().getOperation().getOperationType(), operation.getMetadata().getEntity(), t.getMessage()));
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
	protected Operation updateCreate(Connection connection, EntityAccessPolicy entityAccessPolicy, Operation operation) throws ServerException {

		if (operation.getData() == null || operation.getData().size() == 0) {
			throw new ServerException(GenericService.ERROR_INVALID_CREATEUPDATE_REQUEST, String.format(GenericService.ERROR_INVALID_CREATEUPDATE_REQUEST_MESSAGE, operation.getMetadata().getEntity()));
		}

		DataItem firsItem = operation.getData().get(0);
		if (firsItem.getAttributes() == null || firsItem.getAttributes().size() == 0) {
			throw new ServerException(GenericService.ERROR_INVALID_CREATEUPDATE_REQUEST, String.format(GenericService.ERROR_INVALID_CREATEUPDATE_REQUEST_MESSAGE, operation.getMetadata().getEntity()));
		}

		EntityDataSourceInfo entityInformation = entitiesInformation.getEntity(entityAccessPolicy.getName().getName());
		Long millisecondsSinceEpoch = System.currentTimeMillis();
		Timestamp now = new Timestamp(millisecondsSinceEpoch);

		NavigableMap<String, Object> itemAttributes;

		try {
			for (DataItem item :  operation.getData()) {
				/*
				 * En caso de no estar marcado como eliminado o no ser posible su recuperación por colisión entre claves, se lanzará una excepción.
				 */
				GenericServiceJDBCHelper.recoverOrUpdateRowFromInsert(connection, now, item, entityAccessPolicy, entityInformation);
			}
		}
		catch(SQLException e) {

		}
		finally {

		}

		StringBuilder queryInsert = new StringBuilder();

		/*
		 * Construcción de la sentencia de inserción.
		 */
		String revisionColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.REVISION.getSynchronizationField()).getName(); // REVISION
		String birthColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.BIRTH.getSynchronizationField()).getName(); // BIRTH
		String modifiedColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.MODIFIED.getSynchronizationField()).getName(); // MODIFIED
		String deletedColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.DELETED.getSynchronizationField()).getName(); // DELETED

		queryInsert.append("INSERT INTO ").append(entityAccessPolicy.getName().getName());

		queryInsert.append(" (");

		GenericServiceJDBCHelper.scapeColumn(queryInsert, revisionColumn).append(',');
		GenericServiceJDBCHelper.scapeColumn(queryInsert, birthColumn).append(',');
		GenericServiceJDBCHelper.scapeColumn(queryInsert, modifiedColumn).append(',');
		GenericServiceJDBCHelper.scapeColumn(queryInsert, deletedColumn);

		int initSize = firsItem.getAttributes().size();
		AbstractList<String> inColumns = new ArrayList<String>(initSize);
		AbstractList<String> inAliases = new ArrayList<String>(initSize);
		AbstractList<String> outColumns = new ArrayList<String>(initSize);
		AbstractList<String> outAliases = new ArrayList<String>(initSize);

		StringBuilder columnsToInsert = GenericServiceJDBCHelper.getInsertDataString(entityAccessPolicy, firsItem.getNonSystemAttributes(), entityInformation, inColumns, inAliases, outColumns, outAliases);
		
		if (columnsToInsert == null) {
			throw new ServerException(GenericService.ERROR_INVALID_READONLY_POLICY, String.format(GenericService.ERROR_INVALID_READONLY_POLICY_MESSAGE, operation.getMetadata().getEntity()));	
		}

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

			int i;
			for (DataItem item :  operation.getData()) {

				// Se asignan los valores a la sentencia.
				preparedStatement.setObject(1, EntityDataAttribute.DEFAFULT_REVISION, revisionColumnType);
				preparedStatement.setObject(2, now, birthColumnType);
				preparedStatement.setObject(3, now, modifiedColumnType);
				preparedStatement.setNull(4, deletedColumnType);

				i=5;
				itemAttributes = item.getAttributes();

				for (String inAlias : inAliases) {

					Object value = itemAttributes.get(inAlias);

					if (value == null) {

						preparedStatement.setNull(i, entityInformation.dataFields.get(inAlias));
					}
					else {

						preparedStatement.setObject(i, value, entityInformation.dataFields.get(inAlias));
					}

					i++;
				}

				boolean isRefurbishedOrExistingRow = false;

				try {
					preparedStatement.executeUpdate();

					// Se actualiza el contenido del item en caso de no haber ningún problema.
					item.getAttributes().put(SynchronizationField.REVISION.getSynchronizationField(), EntityDataAttribute.DEFAFULT_REVISION);
					item.getAttributes().put(SynchronizationField.BIRTH.getSynchronizationField(), millisecondsSinceEpoch);
					item.getAttributes().put(SynchronizationField.MODIFIED.getSynchronizationField(), millisecondsSinceEpoch);
					item.getAttributes().put(SynchronizationField.DELETED.getSynchronizationField(), null);
				}
				catch (SQLException e) {

					/*
					 *  Si se trata de una constrain violada, se considera que el registro ya existe y se intenta recuperar en caso de estar marcadao como eliminado.
					 *  En caso de no estar eliminado, se modifica el registro localizado si sólo es 1. En caso de localizarse varios, se relanzará la excepción.
					 */
					if (JDBCUtils.IsIntegrityConstraintViolation(entitiesInformation.getProduct(), e)) {

						isRefurbishedOrExistingRow = true;

						/*
						 * En caso de no estar marcado como eliminado o no ser posible su recuperación por colisión entre claves, se lanzará una excepción.
						 */
						GenericServiceJDBCHelper.recoverOrUpdateRowFromInsert(connection, now, item, entityAccessPolicy, entityInformation);
					}
					else {
						if (preparedStatement != null) {
							preparedStatement.close();
						}
						throw e;
					}
				}

				// Recogida de los datos generados.
				if (!isRefurbishedOrExistingRow && generatedKeyColumns.length > 0) {

					resultSet = preparedStatement.getGeneratedKeys();

					if (resultSet.first()) {

						int GeneratedkeyIndex = 1;

						for(String outAlias : outAliases) {

							item.getAttributes().put(outAlias, resultSet.getObject(GeneratedkeyIndex++));
						}

						resultSet.close();
					}
				}

				preparedStatement.clearParameters();
			}

			// Información de totales.
			operation.getMetadata().setItems(Long.valueOf(operation.getData().size()));

			// Obtención del número total de registros total de la entidad.
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
		catch (Throwable t) {
			throw new ServerException(GenericService.ERROR_OPERATION, String.format(GenericService.ERROR_OPERATION_MESSAGE, operation.getMetadata().getOperation().getOperationType(), operation.getMetadata().getEntity(), t.getMessage()));
		}
		finally {
			JDBCUtils.CloseQuietly(resultSet);
			JDBCUtils.CloseQuietly(preparedStatement);
		}

		return operation;
	}
}
