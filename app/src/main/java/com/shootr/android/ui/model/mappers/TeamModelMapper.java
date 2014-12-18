package com.shootr.android.ui.model.mappers;

import com.shootr.android.domain.TeamEntity;
import com.shootr.android.ui.model.TeamModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class TeamModelMapper {

    @Inject public TeamModelMapper() {
    }

    public TeamModel transform(TeamEntity teamEntity) {
        TeamModel teamModel = new TeamModel();
        teamModel.setIdTeam(teamEntity.getIdTeam());
        teamModel.setName(teamEntity.getClubName());
        return teamModel;
    }

    public List<TeamModel> transform(List<TeamEntity> matchEntities) {
        List<TeamModel> matchModels = new ArrayList<>();
        for (TeamEntity matchEntity : matchEntities) {
            matchModels.add(transform(matchEntity));
        }
        return matchModels;
    }
}
