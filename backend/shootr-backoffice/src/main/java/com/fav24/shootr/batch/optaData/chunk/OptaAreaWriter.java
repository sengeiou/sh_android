package com.fav24.shootr.batch.optaData.chunk;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fav24.shootr.dao.AreaDAO;
import com.fav24.shootr.dao.domain.Area;

@Component
public class OptaAreaWriter implements ItemWriter<Area> {
	private static Logger logger = Logger.getLogger(OptaAreaWriter.class);

	@Autowired
	private AreaDAO areaDAO;

	@Autowired
	private OptaDataShared loadOptaDataShared;

	@Override
	public void write(List<? extends Area> areas) throws Exception {
		for (Area area : areas) {
			if(area != null) {
				// buscamos en las areas existentes
				Area existingArea = loadOptaDataShared.getAreas().get(area.getIdAreaOpta());
				if (existingArea == null) {
					// Area nueva
					Long idArea = areaDAO.insertArea(area);
					area.setIdArea(idArea);
					loadOptaDataShared.getAreas().put(area.getIdAreaOpta(), area);
					logger.info("Inserted "+area);
				} else if (!existingArea.equals(area)) {
					// Area modificada
					areaDAO.updateArea(area);
					loadOptaDataShared.getAreas().put(area.getIdAreaOpta(), area);
					logger.info("Updated "+area);
				}
			}
		}

	}
}
