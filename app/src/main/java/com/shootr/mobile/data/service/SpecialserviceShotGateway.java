package com.shootr.mobile.data.service;

import com.shootr.mobile.data.api.service.VideoApiService;
import com.shootr.mobile.data.entity.VideoEmbedEntity;
import com.shootr.mobile.domain.model.shot.BaseMessage;
import com.shootr.mobile.domain.service.shot.ShotGateway;
import java.io.IOException;
import javax.inject.Inject;

public class SpecialserviceShotGateway implements ShotGateway {

    private final VideoApiService videoApiService;

    @Inject public SpecialserviceShotGateway(VideoApiService videoApiService) {
        this.videoApiService = videoApiService;
    }

    @Override public BaseMessage embedVideoInfo(BaseMessage originalShot) throws IOException {
        VideoEmbedEntity videoEmbedEntity = executeRequest(originalShot);
        return overwriteVideoValues(originalShot, videoEmbedEntity);
    }

    private VideoEmbedEntity executeRequest(BaseMessage originalShot) throws IOException {
        return videoApiService.postEmbdedVideo(originalShot);
    }

    private BaseMessage overwriteVideoValues(BaseMessage originalShot, VideoEmbedEntity videoEmbedEntity) {
        if (videoEmbedEntity.getVideoUrl() != null) {
            originalShot.setImage(videoEmbedEntity.getImage());
            originalShot.setComment(videoEmbedEntity.getComment());
            originalShot.setVideoUrl(videoEmbedEntity.getVideoUrl());
            originalShot.setVideoTitle(videoEmbedEntity.getVideoTitle());
            originalShot.setVideoDuration(videoEmbedEntity.getVideoDuration());
        }
        return originalShot;
    }
}
