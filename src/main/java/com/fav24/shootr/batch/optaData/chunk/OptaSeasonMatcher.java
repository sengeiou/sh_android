package com.fav24.shootr.batch.optaData.chunk;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fav24.shootr.batch.common.PersistAction;
import com.fav24.shootr.batch.optaData.rest.DTO.DTOtoDomainTransformer;
import com.fav24.shootr.batch.optaData.rest.DTO.SeasonDTO;
import com.fav24.shootr.dao.domain.Season;
import com.fav24.shootr.util.DateUtil;

/**
 * Determinate wich operations are required with opta/domain teams (insert, update o nothing)
 */
@Component
public class OptaSeasonMatcher {

	@Autowired
	private DTOtoDomainTransformer dTOtoDomainTransformer;

	public void match(List<SeasonDTO> dtos, List<Season> domains, OptaSeasonItem item) {
		for(SeasonDTO dto: dtos) {
			PersistAction action = getPersistAction(domains, dto);
			if (action.op == PersistAction.UPDATE) {
				item.getUpdateSeasons().add(dTOtoDomainTransformer.seasonDTOtoDomain(dto, item.getCompetition().getIdCompetition(), action.domainId));
			} else if (action.op == PersistAction.CREATE) {
				item.getNewSeasons().add(dTOtoDomainTransformer.seasonDTOtoDomain(dto, item.getCompetition().getIdCompetition(), null));
			}
		}
	}

	PersistAction getPersistAction(List<Season> domains, SeasonDTO dto) {
		for (Season domain : domains) {
			if (areSeasonsTheSame(dto, domain)) {
				if (isOptaSeasonDateOlderThanOurs(domain, dto)) {
					return new PersistAction(domain.getIdSeason(), PersistAction.UPDATE);
				}
				return new PersistAction(PersistAction.IGNORE);
			}
		}
		return new PersistAction(PersistAction.CREATE);
	}

	boolean areSeasonsTheSame(SeasonDTO dto, Season domain) {
		return domain.getIdSeasonOpta().equals(dto.getSeason_id());
	}

	boolean isOptaSeasonDateOlderThanOurs(Season domain, SeasonDTO dto) {
		Date lastUpdate = DateUtil.format(dto.getLast_updated(), "yyyy-MM-dd HH:mm:ss", "CET");
		return (lastUpdate != null) && (domain.getLastUpdated() == null || lastUpdate.compareTo(domain.getLastUpdated()) > 0);
	}
	
	public void setdTOtoDomainTransformer(DTOtoDomainTransformer dTOtoDomainTransformer) {
		this.dTOtoDomainTransformer = dTOtoDomainTransformer;
	}
}
