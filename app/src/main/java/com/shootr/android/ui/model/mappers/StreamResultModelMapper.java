package com.shootr.android.ui.model.mappers;

import com.shootr.android.domain.StreamSearchResult;
import com.shootr.android.ui.model.StreamModel;
import com.shootr.android.ui.model.StreamResultModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

import static com.shootr.android.domain.utils.Preconditions.checkNotNull;

@Singleton
public class StreamResultModelMapper {

    private final StreamModelMapper streamModelMapper;

    @Inject public StreamResultModelMapper(StreamModelMapper streamModelMapper) {
        this.streamModelMapper = streamModelMapper;
    }

    public StreamResultModel transform(StreamSearchResult streamSearchResult) {
        if (streamSearchResult == null) {
            return null;
        }
        checkNotNull(streamSearchResult.getStream());
        StreamModel streamModel = streamModelMapper.transform(streamSearchResult.getStream());

        StreamResultModel resultModel = new StreamResultModel();
        resultModel.setStreamModel(streamModel);
        resultModel.setWatchers(streamSearchResult.getWatchersNumber());
        resultModel.setIsWatching(streamSearchResult.isWatching());
        return resultModel;
    }

    public List<StreamResultModel> transform(List<StreamSearchResult> streamSearchResults) {
        List<StreamResultModel> models = new ArrayList<>(streamSearchResults.size());
        for (StreamSearchResult streamSearchResult : streamSearchResults) {
            models.add(transform(streamSearchResult));
        }
        return models;
    }
}
