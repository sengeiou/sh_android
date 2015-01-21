package com.shootr.android.ui.model.mappers;

import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.EventResultModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EventResultModelMapper {

    private final EventModelMapper eventModelMapper;

    @Inject public EventResultModelMapper(EventModelMapper eventModelMapper) {
        this.eventModelMapper = eventModelMapper;
    }

    public EventResultModel transform(EventSearchResult eventSearchResult) {
        EventModel eventModel = eventModelMapper.transform(eventSearchResult.getEvent());

        EventResultModel resultModel = new EventResultModel();
        resultModel.setEventModel(eventModel);
        resultModel.setWatchers(eventSearchResult.getWatchersNumber());
        return resultModel;
    }

    public List<EventResultModel> transform(List<EventSearchResult> eventSearchResults) {
        List<EventResultModel> models = new ArrayList<>(eventSearchResults.size());
        for (EventSearchResult eventSearchResult : eventSearchResults) {
            models.add(transform(eventSearchResult));
        }
        return models;
    }
}
