package gm.mobi.android.ui.model.mappers;

import android.app.Application;
import android.content.Context;
import gm.mobi.android.db.objects.MatchEntity;
import gm.mobi.android.ui.model.MatchModel;
import gm.mobi.android.util.TimeFormatter;
import gm.mobi.android.util.TimeUtils;

public class MatchModelMapper {

    private Application context;
    private final TimeFormatter timeFormatter;

    public MatchModelMapper(Application context) {
        this.context = context;
        timeFormatter = new TimeFormatter();
    }

    public MatchModel toMatchModel(MatchEntity matchEntity) {
        MatchModel matchModel = new MatchModel();
        matchModel.setTitle(matchEntity.getLocalTeamName() + "-" + matchEntity.getVisitorTeamName());
        matchModel.setDatetime(timeFormatter.getDateAndTimeText(matchEntity.getMatchDate().getTime()));
        matchModel.setIdMatch(matchEntity.getIdMatch());
        matchModel.setLocalTeamId(matchEntity.getIdLocalTeam());
        matchModel.setVisitorTeamId(matchEntity.getIdVisitorTeam());
        matchModel.setLocalTeamName(matchEntity.getLocalTeamName());
        matchModel.setVisitorTeamName(matchEntity.getVisitorTeamName());
        matchModel.setLive(matchEntity.getStatus().equals(1L));
        return matchModel;
    }
}
