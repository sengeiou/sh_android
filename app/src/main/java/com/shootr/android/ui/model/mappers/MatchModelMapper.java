package com.shootr.android.ui.model.mappers;

import com.shootr.android.data.entity.MatchEntity;
import com.shootr.android.ui.model.MatchModel;
import com.shootr.android.util.TimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MatchModelMapper {

    private final TimeFormatter timeFormatter;

    public MatchModelMapper() {
        timeFormatter = new TimeFormatter(); // TODO inject
    }

    public MatchModel toMatchModel(MatchEntity matchEntity) {
        MatchModel matchModel = new MatchModel();
        matchModel.setTitle(matchEntity.getLocalTeamName() + "-" + matchEntity.getVisitorTeamName());
        matchModel.setDatetime(timeFormatter.getDateAndTimeTextRelative(matchEntity.getMatchDate().getTime()));
        matchModel.setIdMatch(matchEntity.getIdMatch());
        matchModel.setLocalTeamId(matchEntity.getIdLocalTeam());
        matchModel.setVisitorTeamId(matchEntity.getIdVisitorTeam());
        matchModel.setLocalTeamName(matchEntity.getLocalTeamName());
        matchModel.setVisitorTeamName(matchEntity.getVisitorTeamName());
        return matchModel;
    }

    public List<MatchModel> toMatchModel(List<MatchEntity> matchEntities) {
        List<MatchModel> matchModels = new ArrayList<>();
        for (MatchEntity matchEntity : matchEntities) {
            matchModels.add(toMatchModel(matchEntity));
        }
        return matchModels;
    }
}
