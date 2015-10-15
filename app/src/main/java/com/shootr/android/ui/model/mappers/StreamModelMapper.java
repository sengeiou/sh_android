package com.shootr.android.ui.model.mappers;

import com.shootr.android.domain.Stream;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.ui.model.StreamModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class StreamModelMapper {

    private final SessionRepository sessionRepository;

    @Inject public StreamModelMapper(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public StreamModel transform(Stream stream) {
        StreamModel streamModel = new StreamModel();
        streamModel.setIdStream(stream.getId());
        streamModel.setTitle(stream.getTitle());
        streamModel.setPicture(stream.getPicture());
        streamModel.setShortTitle(stream.getShortTitle());
        streamModel.setAmIAuthor(stream.getAuthorId().equals(sessionRepository.getCurrentUserId()));
        streamModel.setAuthorId(stream.getAuthorId());
        streamModel.setAuthorUsername(stream.getAuthorUsername());
        streamModel.setDescription(stream.getDescription());
        streamModel.setMediaCount(stream.getMediaCount() != null ? stream.getMediaCount() : 0);
        streamModel.setRemoved(stream.isRemoved());
        return streamModel;
    }

    public List<StreamModel> transform(List<Stream> streams) {
        List<StreamModel> models = new ArrayList<>(streams.size());
        for (Stream stream : streams) {
            models.add(transform(stream));
        }
        return models;
    }
}
