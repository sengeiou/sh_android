package com.fav24.dataservices.mapper;

import java.util.ArrayList;

import com.fav24.dataservices.domain.generic.Filter;
import com.fav24.dataservices.domain.generic.FilterItem;
import com.fav24.dataservices.domain.generic.Filter.NexusType;
import com.fav24.dataservices.dto.generic.FilterDto;
import com.fav24.dataservices.dto.generic.FilterItemDto;
import com.fav24.dataservices.exception.ServerException;


/**
 * Clase encargada del mapeo entre el objeto de transferencia FilterDto y el objeto de dominio Filter.
 */
public class FilterDtoToFilter extends Mapper<FilterDto, Filter> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Filter map(FilterDto origin) throws ServerException {

		Filter filter = new Filter();

		filter.setNexus(NexusType.fromString(origin.getNexus()));

		if (origin.getFilterItems() != null) {
			filter.setFilterItems(new ArrayList<FilterItem>(origin.getFilterItems().length));

			for (FilterItemDto filterItem : origin.getFilterItems()) {
				filter.getFilterItems().add((FilterItem)Mapper.Map(filterItem));
			}
		}

		if (origin.getFilters() != null) {
			filter.setFilters(new ArrayList<Filter>(origin.getFilters().length));

			for (FilterDto filterDto : origin.getFilters()) {

				if (filterDto != null) {
					filter.getFilters().add((Filter)Mapper.Map(filterDto));
				}
			}
		}

		return filter;
	}
}
