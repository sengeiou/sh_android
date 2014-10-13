package com.fav24.shootr.batch.optaMatches.chunk;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fav24.shootr.batch.common.PersistAction;
import com.fav24.shootr.batch.optaData.rest.DTO.DTOtoDomainTransformer;
import com.fav24.shootr.batch.optaData.rest.DTO.MatchDTO;
import com.fav24.shootr.batch.optaData.rest.DTO.RoundDTO;
import com.fav24.shootr.dao.domain.Match;
import com.fav24.shootr.util.DateUtil;

@Component
public class OptaMatchMatcher {

	@Autowired
	private DTOtoDomainTransformer dTOtoDomainTransformer;

	/**
	 * Determinate wich operations are required with opta/domain matches (insert, update o nothing)
	 * @param dtos
	 * @param domains
	 * @param idSeason
	 * @return
	 */
	public OptaMatchItem match(List<RoundDTO> dtos, List<Match> domains, Long idSeason) {
		OptaMatchItem item = new OptaMatchItem();
		for (RoundDTO roundDTO : dtos) {
			for(MatchDTO optaMatch: roundDTO.getMatches()) {
				PersistAction action = getPersistAction(domains, idSeason, optaMatch);
				if (action.op == PersistAction.UPDATE) {
					item.getUpdateMatches().add(dTOtoDomainTransformer.matchDTOtoDomain(optaMatch, idSeason, action.domainId));
				} else if (action.op == PersistAction.CREATE) {
					item.getNewMatches().add(dTOtoDomainTransformer.matchDTOtoDomain(optaMatch, idSeason, null));
				}
			}
		}
		return item;
	}

	PersistAction getPersistAction(List<Match> domains, Long idSeason, MatchDTO optaMatch) {
		for (Match domain : domains) {
			if (areMatchesTheSame(optaMatch, domain)) {
				if (isOptaMatchDateOlderThanOurs(domain, optaMatch)) {
					return new PersistAction(domain.getIdMatch(), PersistAction.UPDATE);
				}
				return new PersistAction(PersistAction.IGNORE);
			}
		}
		return new PersistAction(PersistAction.CREATE);
	}

	boolean areMatchesTheSame(MatchDTO optaMatch, Match domain) {
		return domain.getIdMatchOpta().equals(optaMatch.getMatch_id());
	}

	boolean isOptaMatchDateOlderThanOurs(Match domain, MatchDTO optaMatch) {
		Date lastUpdate = DateUtil.format(optaMatch.getLast_updated(), "yyyy-MM-dd HH:mm:ss", "CET");
		return (lastUpdate != null) && (domain.getLastUpdated() == null || lastUpdate.compareTo(domain.getLastUpdated()) > 0);
	}

	public void setdTOtoDomainTransformer(DTOtoDomainTransformer dTOtoDomainTransformer) {
		this.dTOtoDomainTransformer = dTOtoDomainTransformer;
	}
}
