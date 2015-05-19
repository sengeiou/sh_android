package com.shootr.android.data.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.service.shot.ShotGateway;
import com.shootr.android.service.Endpoint;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.io.IOException;
import javax.inject.Inject;
import org.json.JSONException;
import org.json.JSONObject;

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
        embedVideoEndpoint = endpoint.getUrl() + "/shootr-services/rest/video/embed/";
    }

    @Override public Shot embedVideoInfo(Shot originalShot) {
        RequestBody body = buildRequestBody(originalShot.getComment());
        Request request = new Request.Builder().url(embedVideoEndpoint).post(body).build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            String responseBody = response.body().string();
            JSONObject jsonBody = new JSONObject(responseBody);
            String comment = jsonBody.getString("comment");
            String image = jsonBody.getString("image");
            String videoUrl = jsonBody.getString("videoUrl");
            String videoTitle = jsonBody.getString("videoTitle");
            Long videoDuration = jsonBody.getLong("videoDuration");

            originalShot.setComment(comment);
            originalShot.setVideoUrl(videoUrl);
            originalShot.setVideoTitle(videoTitle);
            originalShot.setVideoDuration(videoDuration);
            originalShot.setImage(image);
        } catch (IOException | JSONException e) {
            //TODO throw proper error
            return originalShot;
        }
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
