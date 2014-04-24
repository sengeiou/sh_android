package com.fav24.dataservices.service.generic.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractList;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.fav24.dataservices.domain.generic.DataItem;
import com.fav24.dataservices.domain.generic.Operation;

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

			DataItem referenceDataItem = new DataItem(data.get(0));

			if (referenceDataItem != null && referenceDataItem.getAttributes() != null && referenceDataItem.getAttributes().size() > 0) {

				int itemIndex = 0;

				if (rs.first()) {
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

							Object value = rs.getObject(i++);

							dataItem.getAttributes().put(attributeAlias, rs.wasNull() ? null : value);
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
