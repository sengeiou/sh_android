package com.shootr.android.ui.model.mappers;

import com.shootr.android.data.entity.TeamEntity;
import com.shootr.android.ui.model.TeamModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class TeamModelMapper {

    @Inject public TeamModelMapper() {
    }

    public TeamModel transform(TeamEntity teamEntity) {
        TeamModel teamModel = new TeamModel();
        teamModel.setIdTeam(teamEntity.getIdTeam().toString());
        teamModel.setName(teamEntity.getClubName());
        return teamModel;
    }

    public List<TeamModel> transform(List<TeamEntity> teamEntities) {
        List<TeamModel> teamModels = new ArrayList<>();
        for (TeamEntity teamEntity : teamEntities) {
            teamModels.add(transform(teamEntity));
        }
        return teamModels;
    }
}
