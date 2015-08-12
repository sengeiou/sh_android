package com.shootr.android.data.service;

import com.shootr.android.data.api.exception.ApiException;
import com.shootr.android.data.api.service.ShotApiService;
import com.shootr.android.data.api.service.VideoApiService;
import com.shootr.android.data.entity.VideoEmbedEntity;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.service.shot.ShotGateway;
import java.io.IOException;
import javax.inject.Inject;

public class SpecialserviceShotGateway implements ShotGateway {

    private final VideoApiService videoApiService;
    private final ShotApiService shotApiService;

    @Inject public SpecialserviceShotGateway(VideoApiService videoApiService, ShotApiService shotApiService) {
        this.videoApiService = videoApiService;
        this.shotApiService = shotApiService;
    }

    @Override public Shot embedVideoInfo(Shot originalShot) throws IOException {
        VideoEmbedEntity videoEmbedEntity = executeRequest(originalShot);
        return overwriteVideoValues(originalShot, videoEmbedEntity);
    }

    @Override
    public void markNiceShot(String idShot) {
        try {
            shotApiService.markNice(idShot);
        } catch (ApiException | IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override
    public void unmarkNiceShot(String idShot) {
        try {
            shotApiService.unmarkNice(idShot);
        } catch (ApiException | IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    private VideoEmbedEntity executeRequest(Shot originalShot) throws IOException {
        return videoApiService.postEmbdedVideo(originalShot);
    }

    private Shot overwriteVideoValues(Shot originalShot, VideoEmbedEntity videoEmbedEntity) {
        originalShot.setImage(videoEmbedEntity.getImage());
        originalShot.setComment(videoEmbedEntity.getComment());
        originalShot.setVideoUrl(videoEmbedEntity.getVideoUrl());
        originalShot.setVideoTitle(videoEmbedEntity.getVideoTitle());
        originalShot.setVideoDuration(videoEmbedEntity.getVideoDuration());
        return originalShot;
    }
}
