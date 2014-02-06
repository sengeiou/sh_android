package com.fav24.dataservices.mapper;

import com.fav24.dataservices.domain.Filter;
import com.fav24.dataservices.dto.FilterDto;
import com.fav24.dataservices.dto.FilterItemDto;
import com.fav24.dataservices.exception.ServerException;


/**
 * 
 * Clase encargada del mapeo entre el objeto de transferencia Filter y el objeto de dominio FilterDto.
 * 
 * @author Fav24
 *
 */
public class FilterToFilterDto extends Mapper<Filter, FilterDto> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected FilterDto map(Filter origin) throws ServerException {

		FilterDto filter = new FilterDto();

		filter.setNexus(origin.getNexus().getNexusType());

		if (origin.getFilterItems() != null) {
			filter.setFilterItems(new FilterItemDto[origin.getFilterItems().size()]);

			for (int i=0; i<origin.getFilterItems().size(); i++) {
				filter.getFilterItems()[i] = (FilterItemDto) Mapper.Map(origin.getFilterItems().get(i));
			}
		}

		if (origin.getFilters() != null) {
			filter.setFilters(new FilterDto[origin.getFilters().size()]);

			for (int i=0; i<origin.getFilters().size(); i++) {
				filter.getFilters()[i] = (FilterDto) Mapper.Map(origin.getFilters().get(i));
			}
		}

		return filter;
	}
}
