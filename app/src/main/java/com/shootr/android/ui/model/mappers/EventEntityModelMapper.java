package com.shootr.android.ui.model.mappers;

import com.shootr.android.data.entity.StreamEntity;
import com.shootr.android.ui.model.EventModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

@Deprecated
public class EventEntityModelMapper {

    @Inject public EventEntityModelMapper() {

    }

    public EventModel toEventModel(StreamEntity streamEntity) {
        EventModel eventModel = new EventModel();
        eventModel.setTitle(streamEntity.getTitle());
        eventModel.setIdEvent(streamEntity.getIdEvent());
        return eventModel;
    }

    public List<EventModel> toEventModel(List<StreamEntity> eventEntities) {
        List<EventModel> eventModels = new ArrayList<>();
        for (StreamEntity streamEntity : eventEntities) {
            eventModels.add(toEventModel(streamEntity));
        }
        return eventModels;
    }
}
