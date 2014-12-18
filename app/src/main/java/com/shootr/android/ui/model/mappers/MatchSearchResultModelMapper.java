package com.shootr.android.ui.model.mappers;

import com.shootr.android.domain.MatchEntity;
import com.shootr.android.ui.model.MatchSearchResultModel;
import com.shootr.android.util.TimeFormatter;
import javax.inject.Inject;

public class MatchSearchResultModelMapper {

    private final TimeFormatter timeFormatter;

    @Inject public MatchSearchResultModelMapper(TimeFormatter timeFormatter) {
        this.timeFormatter = timeFormatter;
    }

    public MatchSearchResultModel transform(MatchEntity matchEntity, boolean addedAlready) {
        MatchSearchResultModel matchResultModel = new MatchSearchResultModel();
        matchResultModel.setTitle(matchEntity.getLocalTeamName() + "-" + matchEntity.getVisitorTeamName());
        matchResultModel.setDatetime(timeFormatter.getDateAndTimeTextGeneric(matchEntity.getMatchDate().getTime()));
        matchResultModel.setIdMatch(matchEntity.getIdMatch());
        matchResultModel.setAddedAlready(addedAlready);
        return matchResultModel;
    }
}
