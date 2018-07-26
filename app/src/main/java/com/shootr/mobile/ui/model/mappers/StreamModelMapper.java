package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.stream.LandingStreams;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.model.LandingStreamsModel;
import com.shootr.mobile.ui.model.StreamModel;
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
        streamModel.setLandscapePicture(stream.getLandscapePicture());
        streamModel.setAmIAuthor(stream.getAuthorId().equals(sessionRepository.getCurrentUserId()));
        streamModel.setAuthorId(stream.getAuthorId());
        streamModel.setAuthorUsername(stream.getAuthorUsername());
        streamModel.setDescription(stream.getDescription());
        streamModel.setTopic(stream.getTopic());
        streamModel.setMediaCount(stream.getMediaCount() != null ? stream.getMediaCount() : 0);
        streamModel.setRemoved(stream.isRemoved());
        streamModel.setTotalFollowers(stream.getTotalFollowers());
        streamModel.setTotalWatchers(stream.getTotalWatchers());
        streamModel.setReadWriteMode(stream.getReadWriteMode() == null ||
            stream.getReadWriteMode().equals(StreamMode.PUBLIC) ? 0 : 1);
        streamModel.setContributorCount(stream.getContributorCount());
        streamModel.setCurrentUserContributor(stream.isCurrentUserContributor());
        streamModel.setVerifiedUser(stream.isVerifiedUser());
        streamModel.setFollowing(stream.isFollowing());
        streamModel.setStrategic(stream.isStrategic());
        streamModel.setMuted(stream.isMuted());
        streamModel.setViews(stream.getViews());
        streamModel.setCanFixItem((stream.canFixItem()));
        streamModel.setCanPinItem((stream.canPinItem()));
        streamModel.setCanWrite((stream.canWrite()));
        streamModel.setCanReply((stream.canReply()));
        streamModel.setCanPostPromoted(stream.canPostPromoted());
        streamModel.setShareLink(stream.getShareLink());
        streamModel.setVideoUrl(stream.getVideoUrl());
        return streamModel;
    }

    public List<StreamModel> transform(List<Stream> streams) {
        List<StreamModel> models = new ArrayList<>(streams.size());
        for (Stream stream : streams) {
            models.add(transform(stream));
        }
        return models;
    }

    public LandingStreamsModel transformLandingStreams(LandingStreams landingStreams) {
        ArrayList<StreamModel> userStreams = new ArrayList<>();
        for (Stream stream : landingStreams.getUserStreams().getStreams()) {
            StreamModel streamModel = transform(stream);
            streamModel.setShowRankPosition(false);
            streamModel.setShowBadge(stream.shouldShowBadge());
            streamModel.setShouldHideStream(true);
            userStreams.add(streamModel);
        }

        ArrayList<StreamModel> hotStreams = new ArrayList<>();

        for (Stream stream : landingStreams.getHotStreams().getStreams()) {
            StreamModel streamModel = transform(stream);
            int position = landingStreams.getHotStreams().getStreams().indexOf(stream);
            streamModel.setShowRankPosition(true);
            streamModel.setPosition(position + 1);
            streamModel.setShouldHideStream(false);
            hotStreams.add(streamModel);
        }

        LandingStreamsModel landingStreamsModel = new LandingStreamsModel();

        landingStreamsModel.setHotStreams(hotStreams);
        landingStreamsModel.setUserStreams(userStreams);

        return landingStreamsModel;
    }
}
