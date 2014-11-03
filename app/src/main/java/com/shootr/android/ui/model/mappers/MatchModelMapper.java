package com.shootr.android.ui.model.mappers;

import com.shootr.android.db.objects.MatchEntity;
import com.shootr.android.ui.model.MatchModel;
import com.shootr.android.util.TimeUtils;

public class MatchModelMapper {

    public MatchModel toMatchModel(MatchEntity matchEntity) {
        MatchModel matchModel = new MatchModel();
        matchModel.setTitle(matchEntity.getLocalTeamName() + "-" + matchEntity.getVisitorTeamName());
        matchModel.setDatetime(TimeUtils.formatShortTime(matchEntity.getMatchDate()));
        matchModel.setIdMatch(matchEntity.getIdMatch());
        matchModel.setLocalTeamId(matchEntity.getIdLocalTeam());
        matchModel.setVisitorTeamId(matchEntity.getIdVisitorTeam());
        matchModel.setLocalTeamName(matchEntity.getLocalTeamName());
        matchModel.setVisitorTeamName(matchEntity.getVisitorTeamName());
        return matchModel;
    }
}
