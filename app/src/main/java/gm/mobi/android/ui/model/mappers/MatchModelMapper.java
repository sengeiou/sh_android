package gm.mobi.android.ui.model.mappers;

import gm.mobi.android.db.objects.MatchEntity;
import gm.mobi.android.ui.model.MatchModel;
import gm.mobi.android.util.TimeUtils;

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
