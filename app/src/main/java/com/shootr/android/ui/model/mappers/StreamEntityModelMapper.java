package com.shootr.android.ui.model.mappers;

import com.shootr.android.data.entity.StreamEntity;
import com.shootr.android.ui.model.StreamModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

@Deprecated
public class StreamEntityModelMapper {

    @Inject public StreamEntityModelMapper() {

    }

    public StreamModel toStreamModel(StreamEntity streamEntity) {
        StreamModel streamModel = new StreamModel();
        streamModel.setTitle(streamEntity.getTitle());
        streamModel.setIdStream(streamEntity.getIdStream());
        return streamModel;
    }

    public List<StreamModel> toStreamModel(List<StreamEntity> streamEntities) {
        List<StreamModel> streamModels = new ArrayList<>();
        for (StreamEntity streamEntity : streamEntities) {
            streamModels.add(toStreamModel(streamEntity));
        }
        return streamModels;
    }
}
