package com.shootr.android.ui.model.mappers;

import com.shootr.android.domain.Event;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.util.TimeFormatter;
import javax.inject.Inject;

public class EventModelMapper {

    private final TimeFormatter timeFormatter;

    @Inject public EventModelMapper(TimeFormatter timeFormatter) {
        this.timeFormatter = timeFormatter;
    }

    public EventModel transform(Event event) {
        EventModel eventModel = new EventModel();
        eventModel.setIdEvent(event.getId());
        eventModel.setTitle(event.getTitle());
        long startDateMilliseconds = event.getStartDate().getTime();
        eventModel.setDatetime(timeFormatter.getDateAndTimeTextRelative(startDateMilliseconds));
        return eventModel;
    }

}
