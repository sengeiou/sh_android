package com.fav24.dataservices.service.generic.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.domain.Requestor;
import com.fav24.dataservices.domain.datasource.DataSources;
import com.fav24.dataservices.domain.generic.DataItem;
import com.fav24.dataservices.domain.generic.Operation;
import com.fav24.dataservices.domain.security.AccessPolicy;
import com.fav24.dataservices.domain.security.EntityAccessPolicy;
import com.fav24.dataservices.domain.security.EntityDataAttribute;
import com.fav24.dataservices.domain.security.EntityDataAttribute.Direction;
import com.fav24.dataservices.domain.security.EntityDataAttribute.SynchronizationField;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.generic.GenericService;
import com.fav24.dataservices.service.generic.impl.GenericServiceJDBCInformation.EntityJDBCInformation;
import com.fav24.dataservices.util.JDBCUtils;


/**
 * Versión JDBC de la interfaz de servicio Generic. 
 */
@Scope("singleton")
@Component
public class GenericServiceJDBC extends GenericServiceBasic<Connection> {

	@Autowired
	private GenericServiceJDBCInformation entitiesInformation;


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
		catch (Exception e) {
			throw new ServerException(GenericService.ERROR_START_TRANSACTION, String.format(GenericService.ERROR_START_TRANSACTION_MESSAGE, e.getMessage()));
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
			catch(Exception e) {
				throw new ServerException(GenericService.ERROR_END_TRANSACTION, String.format(GenericService.ERROR_END_TRANSACTION_MESSAGE, e.getMessage()));	
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
	protected Operation retreave(Connection connection, Requestor requestor, Operation operation) throws ServerException {

		StringBuilder querySelect = new StringBuilder();
		StringBuilder queryFrom = new StringBuilder();
		StringBuilder queryWhere = new StringBuilder();
		StringBuilder queryLimit = new StringBuilder();

		EntityAccessPolicy entityAccessPolicy = AccessPolicy.getEntityPolicy(operation.getMetadata().getEntity());

		EntityJDBCInformation entityInformation = entitiesInformation.getEntity(entityAccessPolicy.getName().getName());

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
			queryWhere.append(GenericServiceJDBCHelper.getExcludeDeletedString(operation.getMetadata())).append(')');
		}

		/*
		 * Especificación del filtro.
		 */
		AbstractList<String> keyColumns = null, filterColumns = null;
		AbstractList<Object> keyValues = null, filterValues = null;

		if (operation.getMetadata().getKey() != null && operation.getMetadata().getKey().size() > 0) {
			keyColumns = new ArrayList<String>();
			keyValues = new ArrayList<Object>();

			StringBuilder key = GenericServiceJDBCHelper.getKeyString(operation.getMetadata().getEntity(), operation.getMetadata().getKey(), keyColumns, keyValues);

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

			StringBuilder filter = GenericServiceJDBCHelper.getFilterSetString(operation.getMetadata().getEntity(), operation.getMetadata().getFilter(), filterColumns, filterValues);

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
				types[i] = entityInformation.keyFields.get(keyColumns.get(i));
				params[i] = GenericServiceJDBCHelper.translateToType(types[i], keyValues.get(i));
			}
		}
		else if (filterColumns != null && filterColumns.size() > 0) {
			types = new int[filterColumns.size()];
			params = new Object[types.length];

			for (int i=0; i<filterColumns.size(); i++) {
				types[i] = entityInformation.filterFields.get(filterColumns.get(i));
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
						preparedStatement.setObject(i+1, params[i], types[i]);
					}
				}

				resultSet = preparedStatement.executeQuery();

				operation.getMetadata().setItems(GenericServiceJDBCHelper.extractData(resultSet, entityAccessPolicy, operation.getData()));

				resultSet.close();
				preparedStatement.close();
			}

			// Obtención del número total de registros total sin contar la paginación.
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
	protected Operation create(Connection connection, Requestor requestor, Operation operation) throws ServerException {

		if (operation.getData() == null || operation.getData().size() == 0) {
			throw new ServerException(GenericService.ERROR_INVALID_CREATE_REQUEST, String.format(GenericService.ERROR_INVALID_CREATE_REQUEST_MESSAGE, operation.getMetadata().getEntity()));
		}

		DataItem firsItem = operation.getData().get(0);
		if (firsItem.getAttributes() == null || firsItem.getAttributes().size() == 0) {
			throw new ServerException(GenericService.ERROR_INVALID_CREATE_REQUEST, String.format(GenericService.ERROR_INVALID_CREATE_REQUEST_MESSAGE, operation.getMetadata().getEntity()));
		}

		StringBuilder queryInsert = new StringBuilder();

		EntityAccessPolicy entityAccessPolicy = AccessPolicy.getEntityPolicy(operation.getMetadata().getEntity());

		EntityJDBCInformation entityInformation = entitiesInformation.getEntity(entityAccessPolicy.getName().getName());

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

		queryInsert.append(',').append(GenericServiceJDBCHelper.getInsertDataString(entityAccessPolicy, firsItem.getNonSystemAttributes(), entityInformation, inColumns, inAliases, outColumns, outAliases));

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

				// Se actualiza el contenido del item.
				item.getAttributes().put(SynchronizationField.REVISION.getSynchronizationField(), EntityDataAttribute.DEFAFULT_REVISION);
				item.getAttributes().put(SynchronizationField.BIRTH.getSynchronizationField(), millisecondsSinceEpoch);
				item.getAttributes().put(SynchronizationField.MODIFIED.getSynchronizationField(), millisecondsSinceEpoch);
				item.getAttributes().put(SynchronizationField.DELETED.getSynchronizationField(), null);

				// Se asignan los valores a la sentencia.
				preparedStatement.setObject(1, EntityDataAttribute.DEFAFULT_REVISION, revisionColumnType);
				preparedStatement.setObject(2, now, birthColumnType);
				preparedStatement.setObject(3, now, modifiedColumnType);
				preparedStatement.setObject(4, null, deletedColumnType);

				i=5;
				itemAttributes = item.getAttributes();

				for (String inAlias : inAliases) {

					preparedStatement.setObject(i++, itemAttributes.get(inAlias));
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
	protected Operation delete(Connection connection, Requestor requestor, Operation operation) throws ServerException {

		StringBuilder queryDelete = new StringBuilder();
		StringBuilder queryFrom = new StringBuilder();
		StringBuilder queryWhere = new StringBuilder();

		EntityAccessPolicy entityAccessPolicy = AccessPolicy.getEntityPolicy(operation.getMetadata().getEntity());

		EntityJDBCInformation entityInformation = entitiesInformation.getEntity(entityAccessPolicy.getName().getName());

		String deletedColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.DELETED.getSynchronizationField()).getName();

		/*
		 * Delete.
		 */
		queryDelete.append("UPDATE ").append(entityAccessPolicy.getName().getName()).append(" SET ");
		// Se asigna la marca de eliminación.
		queryDelete.append(deletedColumn).append("=?"); // DELETED;

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
			filter = GenericServiceJDBCHelper.getKeyString(operation.getMetadata().getEntity(), operation.getMetadata().getKey(), filterColumns, filterValues);
		}
		else if (operation.getMetadata().getFilter() != null) {

			filterTypes = entityInformation.filterFields;
			filter = GenericServiceJDBCHelper.getFilterSetString(operation.getMetadata().getEntity(), operation.getMetadata().getFilter(), filterColumns, filterValues);
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
					preparedStatement.setObject(i+2, params[i], types[i]);
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
					querySelect.append(" WHERE ").append(deletedColumn).append("=? AND (").append(filter).append(')');

					preparedStatement = connection.prepareStatement(querySelect.toString());

					preparedStatement.setObject(1, now, deletedType);

					if (params != null) {

						for (int i=0; i<params.length; i++) {
							preparedStatement.setObject(i+2, params[i], types[i]);
						}
					}

					resultSet = preparedStatement.executeQuery();

					operation.getMetadata().setItems(GenericServiceJDBCHelper.extractData(resultSet, entityAccessPolicy, operation.getData()));

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
	protected Operation update(Connection connection, Requestor requestor, Operation operation) throws ServerException {

		if (operation.getData() == null || operation.getData().size() == 0) {
			throw new ServerException(GenericService.ERROR_INVALID_UPDATE_REQUEST, String.format(GenericService.ERROR_INVALID_UPDATE_REQUEST_MESSAGE, operation.getMetadata().getEntity()));
		}

		DataItem referenceItem = new DataItem(operation.getData().get(0));

		if (referenceItem.getAttributes() == null || referenceItem.getAttributes().size() == 0) {
			throw new ServerException(GenericService.ERROR_INVALID_UPDATE_REQUEST, String.format(GenericService.ERROR_INVALID_UPDATE_REQUEST_MESSAGE, operation.getMetadata().getEntity()));
		}

		EntityAccessPolicy entityAccessPolicy = AccessPolicy.getEntityPolicy(operation.getMetadata().getEntity());
		EntityJDBCInformation entityInformation = entitiesInformation.getEntity(entityAccessPolicy.getName().getName());

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

		// - Conjunto completo de atributos de auditoría: #SynchronizationField.REVISION, #SynchronizationField.BIRTH, #SynchronizationField.MODIFIED, #SynchronizationField.DELETED.
		String revisionColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.REVISION.getSynchronizationField()).getName(); // REVISION
		String birthColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.BIRTH.getSynchronizationField()).getName(); // BIRTH
		String modifiedColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.MODIFIED.getSynchronizationField()).getName(); // MODIFIED
		String deletedColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.DELETED.getSynchronizationField()).getName(); // DELETED

		querySelect.append(revisionColumn).append(',').append(birthColumn).append(',').append(modifiedColumn).append(',').append(deletedColumn);

		// - Conjunto de attributos que conforman la clave primaria.
		Iterator<String> primaryKeyColumns = entityInformation.primaryKey.keySet().iterator();

		while (primaryKeyColumns.hasNext()) {

			querySelect.append(',').append(primaryKeyColumns.next());
		}

		// - Atributos con #Direction.OUTPUT. (Para poder informar los atributos de solo salida en la operación de vuelta)
		NavigableMap<String, Object> attributes = referenceItem.getAttributes();
		Iterator<String> attributeAliases = attributes.keySet().iterator();

		while (attributeAliases.hasNext()) {

			EntityDataAttribute dataAttribute = entityAccessPolicy.getData().getAttribute(attributeAliases.next());

			if (dataAttribute == null) {
				entityAccessPolicy.checkAttributesAccesibility(new ArrayList<String>(attributes.keySet()));
			}
			else {

				if (!SynchronizationField.isSynchronizationField(dataAttribute.getAlias())) {

					if (!entityInformation.primaryKey.containsKey(dataAttribute.getName())) {

						if (dataAttribute.getDirection() == Direction.OUTPUT || dataAttribute.getDirection() == Direction.BOTH) {
							querySelect.append(',').append(dataAttribute.getName());
						}
					}
				}
			}
		}

		StringBuilder queryFrom = new StringBuilder(" FROM ").append(entityAccessPolicy.getName().getName());

		StringBuilder queryWhere = new StringBuilder(" WHERE ").append(deletedColumn).append(" IS NULL");

		/*
		 * Especificación del filtro.
		 */
		queryWhere.append(" AND (");

		AbstractList<String> columns = new ArrayList<String>();
		AbstractList<Object> values = new ArrayList<Object>();

		boolean useKeys = operation.getMetadata().getKey() != null && operation.getMetadata().getKey().size() > 0;

		if (useKeys) {

			StringBuilder key = GenericServiceJDBCHelper.getKeyString(operation.getMetadata().getEntity(), operation.getMetadata().getKey(), columns, values);

			queryWhere.append(key);
		}
		else if (operation.getMetadata().getFilter() != null) {

			StringBuilder filter = GenericServiceJDBCHelper.getFilterSetString(operation.getMetadata().getEntity(), operation.getMetadata().getFilter(), columns, values);

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
				preparedStatement.setObject(i+1, params[i], types[i]);
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
					DataItem dataItem = update(connection, resultSet, now, referenceItem, entityAccessPolicy, entityInformation);

					if (dataItem != null) {

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
	 * Modifica los datos del registro en curso, en caso de ser necesario, y añade el cambio a la estructura de la operación, manteniendo el valor del número de elementos modificados.
	 *   
	 * @param connection Conexión a la base de datos, con la transacción abierta para que el posible update, entre dentro.
	 * @param resultSet Registro de base de datos sobre el que se evalua su posible actualización.
	 * @param referenceItem Elemento de referencia que contiene la información a actualizar.
	 * @param entityAccessPolicy Políticas de acceso de la entidad sobre la que se realiza el update.
	 * @param entityInformation Información extraida de la fuente de datos, acerca de la entidad sobre la que se realiza el update.
	 * 
	 * @return un nuevo registro resultado del mecanismo de modificación. Este registro contiene, en la mayoría de los casos, información tanto del lado entrante como el existente.
	 * @throws SQLException 
	 */
	private DataItem update(Connection connection, ResultSet resultSet, Timestamp now, DataItem referenceItem, EntityAccessPolicy entityAccessPolicy, EntityJDBCInformation entityInformation) throws SQLException {

		String revisionColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.REVISION.getSynchronizationField()).getName(); // REVISION
		String birthColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.BIRTH.getSynchronizationField()).getName(); // BIRTH
		String modifiedColumn = entityAccessPolicy.getData().getAttribute(SynchronizationField.MODIFIED.getSynchronizationField()).getName(); // MODIFIED

		int revisionColumnType = entityInformation.dataFields.get(revisionColumn);
		int modifiedColumnType = entityInformation.dataFields.get(modifiedColumn);

		DataItem updatedItem = new DataItem(referenceItem);
		NavigableMap<String, Object> updatedAttributes = updatedItem.getAttributes();

		Timestamp localBirth = resultSet.getTimestamp(birthColumn);
		localBirth.setNanos(0);

		Timestamp localModified = resultSet.getTimestamp(modifiedColumn);
		localModified.setNanos(0);

		Timestamp remoteModified = new Timestamp((Long)referenceItem.getAttributes().get(SynchronizationField.MODIFIED.getSynchronizationField()));
		remoteModified.setNanos(0);

		Long localRevision = resultSet.getLong(revisionColumn);
		Long remoteRevision = ((Number)referenceItem.getAttributes().get(SynchronizationField.REVISION.getSynchronizationField())).longValue();

		// Comprobación de si gana el registro local, o el remoto.
		if (incommingItemWins(localModified, remoteModified, 
				localRevision, remoteRevision, 
				entityAccessPolicy.getPositiveRevisionThreshold(), entityAccessPolicy.getNegativeRevisionThreshold())) {

			// Gana el registro entrante.
			Long finalRevision = localRevision == remoteRevision ? remoteRevision + 1 : remoteRevision;

			// Propagación de la información de auditoría.
			updatedAttributes.put(SynchronizationField.REVISION.getSynchronizationField(), finalRevision);
			updatedAttributes.put(SynchronizationField.BIRTH.getSynchronizationField(), localBirth.getTime());
			updatedAttributes.put(SynchronizationField.MODIFIED.getSynchronizationField(), now.getTime());
			updatedAttributes.put(SynchronizationField.DELETED.getSynchronizationField(), null);

			// Propagación de la información de solo salida.
			AbstractList<Integer> types = new ArrayList<Integer>();
			AbstractList<Object> params = new ArrayList<Object>();

			StringBuilder queryUpdate = new StringBuilder("UPDATE ").append(entityAccessPolicy.getName().getName()).append(" SET ");
			queryUpdate.append(revisionColumn).append("=?,").append(modifiedColumn).append("=?");

			types.add(revisionColumnType);
			params.add(finalRevision);

			types.add(modifiedColumnType);
			params.add(now);

			for (String attributeAlias : updatedAttributes.keySet()) {

				if (!SynchronizationField.isSynchronizationField(attributeAlias)) {

					EntityDataAttribute dataAttribute = entityAccessPolicy.getData().getAttribute(attributeAlias);

					if (dataAttribute != null) {

						if (dataAttribute.getDirection() == Direction.OUTPUT) {

							Object value = resultSet.getObject(dataAttribute.getName());

							updatedAttributes.put(attributeAlias, resultSet.wasNull() ? null : value);
						}
						else if (!entityInformation.primaryKey.containsKey(dataAttribute.getName())) {

							types.add(entityInformation.dataFields.get(dataAttribute.getName()));
							params.add(updatedAttributes.get(attributeAlias));

							queryUpdate.append(',').append(dataAttribute.getName()).append("=?");
						}
					}
				}
			}

			// Concatenación del filtro por clave primaira.
			queryUpdate.append(" WHERE ");

			// - Conjunto de attributos que conforman la clave primaria.
			Iterator<Entry<String, Integer>> primaryKeyColumns = entityInformation.primaryKey.entrySet().iterator();

			Entry<String, Integer> primaryKeyColumn = primaryKeyColumns.next();
			queryUpdate.append(primaryKeyColumn.getKey()).append("=?");

			types.add(primaryKeyColumn.getValue());
			params.add(resultSet.getObject(primaryKeyColumn.getKey()));

			while (primaryKeyColumns.hasNext()) {

				primaryKeyColumn = primaryKeyColumns.next();

				queryUpdate.append(" AND ").append(primaryKeyColumn.getKey()).append("=?");

				types.add(primaryKeyColumn.getValue());
				params.add(resultSet.getObject(primaryKeyColumn.getKey()));
			}

			PreparedStatement preparedStatement = null;

			try {
				preparedStatement = connection.prepareStatement(queryUpdate.toString());

				for (int i=0; i<params.size(); i++) {
					preparedStatement.setObject(i+1, params.get(i), types.get(i));
				}

				if (preparedStatement.executeUpdate() != 1) {
					updatedItem = null;
				}
			}
			catch (SQLException e) {
				throw e;
			}
			finally {
				JDBCUtils.CloseQuietly(preparedStatement);
			}
		}
		else { // Gana el registro existente.

			// Propagación de la información de auditoría.
			updatedAttributes.put(SynchronizationField.REVISION.getSynchronizationField(), localRevision);
			updatedAttributes.put(SynchronizationField.BIRTH.getSynchronizationField(), localBirth.getTime());
			updatedAttributes.put(SynchronizationField.MODIFIED.getSynchronizationField(), localModified.getTime());
			updatedAttributes.put(SynchronizationField.DELETED.getSynchronizationField(), null);

			// Propagación de la información de salida.
			for (String attributeAlias : updatedAttributes.keySet()) {

				if (!SynchronizationField.isSynchronizationField(attributeAlias)) {

					EntityDataAttribute dataAttribute = entityAccessPolicy.getData().getAttribute(attributeAlias);

					if (dataAttribute != null && (dataAttribute.getDirection() == Direction.OUTPUT || dataAttribute.getDirection() == Direction.BOTH)) {

						Object value = resultSet.getObject(dataAttribute.getName());

						updatedAttributes.put(attributeAlias, resultSet.wasNull() ? null : value);
					}
				}
			}
		}

		return updatedItem;
	}
}
