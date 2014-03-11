package com.fav24.dataservices.mapper;

import com.fav24.dataservices.domain.generic.FilterItem;
import com.fav24.dataservices.domain.generic.FilterItem.ComparatorType;
import com.fav24.dataservices.dto.generic.FilterItemDto;


/**
 * 
 * Clase encargada del mapeo entre el objeto de transferencia FilterItemDto y el objeto de dominio FilterItem.
 * 
 * @author Fav24
 *
 */
public class FilterItemDtoToFilterItem extends Mapper<FilterItemDto, FilterItem> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected FilterItem map(FilterItemDto origin) {

		FilterItem filterItem = new FilterItem();

		filterItem.setComparator(ComparatorType.fromString(origin.getComparator()));
		filterItem.setName(origin.getName());
		filterItem.setValue(origin.getValue());

		return filterItem;
	}
}
