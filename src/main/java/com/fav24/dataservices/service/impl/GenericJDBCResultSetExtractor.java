package com.fav24.dataservices.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractList;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.fav24.dataservices.domain.DataItem;
import com.fav24.dataservices.domain.Operation;

public class GenericJDBCResultSetExtractor implements ResultSetExtractor<Operation> {

	private Operation operation;

	/**
	 * Constructor con parámetro.
	 * 
	 * @param operation Operación a popular.
	 */
	public GenericJDBCResultSetExtractor(Operation operation) {
		this.operation = operation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Operation extractData(ResultSet rs) throws SQLException, DataAccessException {

		long numItems = 0;
		AbstractList<DataItem> data = operation.getData();

		if (data != null && data.size() > 0) {

			DataItem referenceDataItem = data.get(0);

			if (referenceDataItem != null && referenceDataItem.getAttributes() != null && referenceDataItem.getAttributes().size() > 0) {

				if (rs.first()) {
					do
					{
						NavigableMap<String, Object> attributes = new TreeMap<String, Object>();
						DataItem dataItem = new DataItem(attributes);
						data.add(dataItem);

						int i=0;
						for (String attributeAlias : dataItem.getAttributes().keySet()) {
							attributes.put(attributeAlias, rs.getObject(i++));
						}

						numItems++;
					}while(rs.next());
				}
			}
		}

		operation.getMetadata().setItems(numItems);

		return operation;
	}
}
