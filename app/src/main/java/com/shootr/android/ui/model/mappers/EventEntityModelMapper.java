package com.shootr.android.ui.model.mappers;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.util.TimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

@Deprecated
public class EventEntityModelMapper {

    private final TimeFormatter timeFormatter;

    @Inject public EventEntityModelMapper(TimeFormatter timeFormatter) {
        this.timeFormatter = timeFormatter;
    }

    public EventModel toEventModel(EventEntity eventEntity) {
        EventModel eventModel = new EventModel();
        eventModel.setTitle(eventEntity.getTitle());
        eventModel.setDatetime(timeFormatter.getDateAndTimeTextRelative(eventEntity.getBeginDate().getTime()));
        eventModel.setIdEvent(eventEntity.getIdEvent().toString());
        return eventModel;
    }

    public List<EventModel> toEventModel(List<EventEntity> eventEntities) {
        List<EventModel> eventModels = new ArrayList<>();
        for (EventEntity eventEntity : eventEntities) {
            eventModels.add(toEventModel(eventEntity));
        }
        return eventModels;
    }
}
