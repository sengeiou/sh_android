package com.shootr.android.ui.model.mappers;

import com.shootr.android.domain.Event;
import com.shootr.android.ui.model.MatchModel;
import com.shootr.android.util.TimeFormatter;
import javax.inject.Inject;

public class EventModelMapper {

    private final TimeFormatter timeFormatter;

    @Inject public EventModelMapper(TimeFormatter timeFormatter) {
        this.timeFormatter = timeFormatter;
    }

    public MatchModel transform(Event event) {
        MatchModel matchModel = new MatchModel();
        matchModel.setIdMatch(event.getId());
        matchModel.setTitle(event.getTitle());
        long startDateMilliseconds = event.getStartDate().getTime();
        matchModel.setDatetime(timeFormatter.getDateAndTimeTextRelative(startDateMilliseconds));
        return matchModel;
    }

}
