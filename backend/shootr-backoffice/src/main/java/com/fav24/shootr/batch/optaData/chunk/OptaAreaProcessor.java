package com.fav24.shootr.batch.optaData.chunk;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fav24.shootr.batch.optaData.rest.DTO.AreaDTO;
import com.fav24.shootr.batch.optaData.rest.DTO.DTOtoDomainTransformer;
import com.fav24.shootr.dao.domain.Area;

@Component
public class OptaAreaProcessor implements ItemProcessor<AreaDTO, Area> {

	@Autowired
	private DTOtoDomainTransformer dTOtoDomainTransformer;

	@Override
    public Area process(AreaDTO areaDTO) throws Exception {
        return (areaDTO.getCountrycode() != null && !"".equals(areaDTO.getCountrycode())) ? dTOtoDomainTransformer.areaDTOtoDomain(areaDTO) : null;
    }
}
