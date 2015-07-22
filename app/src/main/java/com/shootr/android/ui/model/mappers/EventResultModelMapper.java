package com.shootr.android.ui.model.mappers;

import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.EventResultModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

@Singleton
public class EventResultModelMapper {

    private final EventModelMapper eventModelMapper;

    @Inject public EventResultModelMapper(EventModelMapper eventModelMapper) {
        this.eventModelMapper = eventModelMapper;
    }

    public EventResultModel transform(StreamSearchResult streamSearchResult) {
        if (streamSearchResult == null) {
            return null;
        }
        checkNotNull(streamSearchResult.getStream());
        EventModel eventModel = eventModelMapper.transform(streamSearchResult.getStream());

        EventResultModel resultModel = new EventResultModel();
        resultModel.setEventModel(eventModel);
        resultModel.setWatchers(streamSearchResult.getWatchersNumber());
        return resultModel;
    }

    public List<EventResultModel> transform(List<StreamSearchResult> streamSearchResults) {
        List<EventResultModel> models = new ArrayList<>(streamSearchResults.size());
        for (StreamSearchResult streamSearchResult : streamSearchResults) {
            models.add(transform(streamSearchResult));
        }
        return models;
    }
}
