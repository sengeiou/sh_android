package com.shootr.android.data.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shootr.android.data.entity.VideoEmbedEntity;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.exception.ShootrServerException;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.service.shot.ShotGateway;
import com.shootr.android.service.Endpoint;
import com.shootr.android.service.ShootrEmbedVideoError;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.io.IOException;
import javax.inject.Inject;

public class SpecialserviceShotGateway implements ShotGateway {

    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    private final SessionRepository sessionRepository;

    private String embedVideoEndpoint;

    @Inject public SpecialserviceShotGateway(OkHttpClient okHttpClient, ObjectMapper objectMapper, Endpoint endpoint,
      SessionRepository sessionRepository) {
        this.okHttpClient = okHttpClient;
        this.objectMapper = objectMapper;
        this.sessionRepository = sessionRepository;
        embedVideoEndpoint = endpoint.getUrl() + "/media/rest/video/embed/";
    }

    @Override public Shot embedVideoInfo(Shot originalShot) throws IOException {
        VideoEmbedEntity videoEmbedEntity = executeRequest(originalShot);
        return overwriteVideoValues(originalShot, videoEmbedEntity);
    }

    private VideoEmbedEntity executeRequest(Shot originalShot) throws IOException {
        RequestBody body = buildRequestBody(originalShot.getComment());
        Request request = new Request.Builder().url(embedVideoEndpoint).post(body).build();
        String responseBody;
        try {
            Response response = okHttpClient.newCall(request).execute();
            responseBody = response.body().string();
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
        VideoEmbedEntity videoEmbedEntity = entityFromBody(responseBody);
        if (videoEmbedEntity != null) {
            return videoEmbedEntity;
        } else {
            throw parseErrorToException(responseBody);
        }
    }

    private ShootrServerException parseErrorToException(String responseBody) throws IOException {
        ShootrEmbedVideoError error = objectMapper.readValue(responseBody, ShootrEmbedVideoError.class);
        throw new ShootrServerException(error);
    }

    private VideoEmbedEntity entityFromBody(String responseBody) {
        try {
            return objectMapper.readValue(responseBody, VideoEmbedEntity.class);
        } catch (IOException e) {
            return null;
        }
    }

    private Shot overwriteVideoValues(Shot originalShot, VideoEmbedEntity videoEmbedEntity) {
        originalShot.setImage(videoEmbedEntity.getImage());
        originalShot.setComment(videoEmbedEntity.getComment());
        originalShot.setVideoUrl(videoEmbedEntity.getVideoUrl());
        originalShot.setVideoTitle(videoEmbedEntity.getVideoTitle());
        originalShot.setVideoDuration(videoEmbedEntity.getVideoDuration());
        return originalShot;
    }

    private RequestBody buildRequestBody(String shotComment) {
        return new MultipartBuilder().type(MultipartBuilder.FORM)
          .addPart(Headers.of("Content-Disposition", "form-data; name=\"idUser\""),
            RequestBody.create(MediaType.parse("text/plain"), sessionRepository.getCurrentUserId()))
          .addPart(Headers.of("Content-Disposition", "form-data; name=\"sessionToken\""),
            RequestBody.create(MediaType.parse("text/plain"), sessionRepository.getSessionToken()))
          .addPart(Headers.of("Content-Disposition", "form-data; name=\"comment\""),
            RequestBody.create(MediaType.parse("text/plain"), shotComment))
          .build();
    }
}
