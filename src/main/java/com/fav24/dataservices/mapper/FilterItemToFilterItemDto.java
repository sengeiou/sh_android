package com.fav24.dataservices.mapper;

import com.fav24.dataservices.domain.generic.FilterItem;
import com.fav24.dataservices.dto.generic.FilterItemDto;


/**
 * 
 * Clase encargada del mapeo entre el objeto de dominio FilterItem y el objeto de transferencia FilterItemDto.
 * 
 * @author Fav24
 *
 */
public class FilterItemToFilterItemDto extends Mapper<FilterItem, FilterItemDto> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected FilterItemDto map(FilterItem origin) {

		FilterItemDto filterItem = new FilterItemDto();

		filterItem.setComparator(origin.getComparator().getComparatorType());
		filterItem.setName(origin.getName());
		filterItem.setValue(origin.getValue());

		return filterItem;
	}
}
