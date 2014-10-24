package com.fav24.shootr.batch.optaData.chunk;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fav24.shootr.batch.common.PersistAction;
import com.fav24.shootr.batch.optaData.rest.DTO.DTOtoDomainTransformer;
import com.fav24.shootr.batch.optaData.rest.DTO.TeamDTO;
import com.fav24.shootr.dao.domain.Team;

/**
 * Determinate wich operations are required with opta/domain teams (insert, update o nothing)
 */
@Component
public class OptaTeamMatcher {

	@Autowired
	private DTOtoDomainTransformer dTOtoDomainTransformer;

	public void match(List<TeamDTO> dtos, List<Team> domains, OptaTeamItem item) {
		for (TeamDTO optaTeam : dtos) {
			PersistAction action = getPersistAction(domains, optaTeam);
			if (action.op == PersistAction.CREATE) {
				item.getNewTeams().add(dTOtoDomainTransformer.teamDTOtoDomain(optaTeam, item.getArea().getIdArea()));
			}
		}
	}

	PersistAction getPersistAction(List<Team> domains, TeamDTO optaTeam) {
		for (Team domain : domains) {
			if (areTeamsTheSame(optaTeam, domain) || !isOptaTeamTypeDefault(optaTeam)) {
				return new PersistAction(PersistAction.IGNORE);
			}
		}
		return new PersistAction(PersistAction.CREATE);
	}

	boolean areTeamsTheSame(TeamDTO optaTeam, Team domain) {
		return domain.getIdTeamOpta().equals(optaTeam.getTeam_id());
	}

	boolean isOptaTeamTypeDefault(TeamDTO optaTeam) {
		return "default".equalsIgnoreCase(optaTeam.getTeamtype());
	}

	public void setdTOtoDomainTransformer(DTOtoDomainTransformer dTOtoDomainTransformer) {
		this.dTOtoDomainTransformer = dTOtoDomainTransformer;
	}
}
