package com.shootr.mobile.service;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrError;
import com.shootr.mobile.domain.exception.ShootrServerException;
import com.shootr.mobile.domain.repository.PhotoService;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.sloydev.jsonadapters.JsonAdapter;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.io.File;
import java.io.IOException;
import javax.inject.Inject;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

public class ShootrPhotoService implements PhotoService {

    public final String uploadProfilePhotoEndpoint;
    public final String uploadShotPhotoEndpoint;
    public final String uploadStreamPhotoEndpoint;

    private OkHttpClient client;
    private SessionRepository sessionRepository;
    private JsonAdapter jsonAdapter;

    @Inject public ShootrPhotoService(OkHttpClient client, SessionRepository sessionRepository, JsonAdapter jsonAdapter,
      Endpoint endpoint) {
        this.client = client;
        this.sessionRepository = sessionRepository;
        this.jsonAdapter = jsonAdapter;
        uploadProfilePhotoEndpoint = endpoint.getUrl() + "/media/upload/img/profile";
        uploadShotPhotoEndpoint = endpoint.getUrl() + "/media/upload/img/shot";
        uploadStreamPhotoEndpoint = endpoint.getUrl() + "/media/upload/img/stream";
    }

    @Override public String uploadProfilePhotoAndGetUrl(File photoFile) {
        Timber.d("Uploading photo with file path %s", photoFile.getAbsolutePath());
        RequestBody body = buildRequestBody(photoFile);

        Response response = null;
        try {
            response = executeProfileRequest(body);
            return parseProfileResponse(response);
        } catch (IOException e) {
            throw new ServerCommunicationException(e);
        }
    }

    @Override public String uploadShotImageAndGetUrl(File imageFile) throws IOException {
        Timber.d("Uploading image with file path %s", imageFile.getAbsolutePath());
        RequestBody body = buildRequestBody(imageFile);
        Response response = executeShotImageRequest(body);
        return parseShotImageResponse(response);
    }

    @Override public String uploadStreamImageAndGetUrl(File imageFile, String idStream) throws IOException {
        Timber.d("Uploading image with file path %s", imageFile.getAbsolutePath());
        RequestBody body = buildStreamRequestBody(imageFile, idStream);
        Response response = executeStreamImageRequest(body);
        return parseStreamImageResponse(response);
    }

    private RequestBody buildRequestBody(File photoFile) {
        return new MultipartBuilder().type(MultipartBuilder.FORM)
          .addPart(Headers.of("Content-Disposition", "form-data; name=\"idUser\""),
            RequestBody.create(MediaType.parse("text/plain"), sessionRepository.getCurrentUserId()))
          .addPart(Headers.of("Content-Disposition", "form-data; name=\"sessionToken\""),
            RequestBody.create(MediaType.parse("text/plain"), sessionRepository.getSessionToken()))
          .addPart(Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\"photo.jpeg"),
            RequestBody.create(MediaType.parse("image/jpeg"), photoFile))
          .build();
    }

    private RequestBody buildStreamRequestBody(File photoFile, String idStream) {
        return new MultipartBuilder().type(MultipartBuilder.FORM)
          .addPart(Headers.of("Content-Disposition", "form-data; name=\"idUser\""),
            RequestBody.create(MediaType.parse("text/plain"), sessionRepository.getCurrentUserId()))
          .addPart(Headers.of("Content-Disposition", "form-data; name=\"idStream\""),
            RequestBody.create(MediaType.parse("text/plain"), idStream))
          .addPart(Headers.of("Content-Disposition", "form-data; name=\"sessionToken\""),
            RequestBody.create(MediaType.parse("text/plain"), sessionRepository.getSessionToken()))
          .addPart(Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\"photo.jpeg"),
            RequestBody.create(MediaType.parse("image/jpeg"), photoFile))
          .build();
    }

    private Response executeProfileRequest(RequestBody body) throws IOException {
        return executeRequest(body, uploadProfilePhotoEndpoint);
    }

    private Response executeShotImageRequest(RequestBody body) throws IOException {
        return executeRequest(body, uploadShotPhotoEndpoint);
    }

    private Response executeStreamImageRequest(RequestBody body) throws IOException {
        return executeRequest(body, uploadStreamPhotoEndpoint);
    }

    private Response executeRequest(RequestBody body, String endpoint) throws IOException {
        Request request = new Request.Builder().url(endpoint).post(body).build();

        return client.newCall(request).execute();
    }

    private String parseShotImageResponse(Response response) throws IOException {
        try {
            JSONObject responseJson = getJsonFromResponse(response);
            return readJsonFromShotImage(responseJson);
        } catch (JSONException e) {
            ShootrError jsonError = new ShootrPhotoUploadError(ShootrError.ERROR_CODE_UNKNOWN_ERROR,
              "JSONException",
              "Error while parsing response JSON. Response received:" + response.body().string());
            throw new ShootrServerException(jsonError);
        }
    }

    private String parseProfileResponse(Response response) throws IOException {
        try {
            JSONObject responseJson = getJsonFromResponse(response);
            return readJsonFromProfilePhoto(responseJson);
        } catch (JSONException e) {
            ShootrError jsonError = new ShootrPhotoUploadError(ShootrError.ERROR_CODE_UNKNOWN_ERROR,
              "JSONException",
              "Error while parsing response JSON. Response received:" + response.body().string());
            throw new ShootrServerException(jsonError);
        }
    }

    private String parseStreamImageResponse(Response response) throws IOException {
        try {
            JSONObject responseJson = getJsonFromResponse(response);
            return readJsonFromStreamImage(responseJson);
        } catch (JSONException e) {
            ShootrError jsonError = new ShootrPhotoUploadError(ShootrError.ERROR_CODE_UNKNOWN_ERROR,
              "JSONException",
              "Error while parsing response JSON. Response received:" + response.body().string());
            throw new ShootrServerException(jsonError);
        }
    }

    private String readJsonFromProfilePhoto(JSONObject jsonObject) throws JSONException, IOException {
        String profileThumbnailImageUrl = getProfileThumbnailUrlFromJson(jsonObject);

        boolean imageUploadedSuccessfully = profileThumbnailImageUrl != null && !profileThumbnailImageUrl.isEmpty();
        if (imageUploadedSuccessfully) {
            Timber.d("Photo uploaded to url: %s", profileThumbnailImageUrl);
            return profileThumbnailImageUrl;
        } else {
            return throwParsedError(jsonObject);
        }
    }

    private String readJsonFromShotImage(JSONObject jsonObject) throws IOException, JSONException {
        String imageUrl = getShotImageUrlFromJson(jsonObject);

        boolean imageUploadedSuccessfully = imageUrl != null && !imageUrl.isEmpty();
        if (imageUploadedSuccessfully) {
            Timber.d("Image uploaded to url: %s", imageUrl);
            return imageUrl;
        } else {
            return throwParsedError(jsonObject);
        }
    }

    private String readJsonFromStreamImage(JSONObject jsonObject) throws IOException, JSONException {
        String imageUrl = getStreamImageUrlFromJson(jsonObject);

        boolean imageUploadedSuccessfully = imageUrl != null && !imageUrl.isEmpty();
        if (imageUploadedSuccessfully) {
            Timber.d("Image uploaded to url: %s", imageUrl);
            return imageUrl;
        } else {
            return throwParsedError(jsonObject);
        }
    }

    private String throwParsedError(JSONObject jsonObject) throws IOException, JSONException {
        ShootrPhotoUploadError shootrError =
          jsonAdapter.fromJson(jsonObject.getString("status"), ShootrPhotoUploadError.class);
        ShootrServerException shootrServerException = new ShootrServerException(shootrError);
        Timber.e(shootrServerException,
          "Photo not received, ShootrError: %s - %s",
          shootrError.getErrorCode(),
          shootrError.getMessage());
        throw shootrServerException;
    }

    private String getProfileThumbnailUrlFromJson(JSONObject responseJson) {
        return responseJson.optString("profileThumbnailImageUrl");
    }

    private String getShotImageUrlFromJson(JSONObject responseJson) {
        return responseJson.optString("shotImageUrl");
    }

    private String getStreamImageUrlFromJson(JSONObject responseJson) {
        return responseJson.optString("streamImageUrl");
    }

    private JSONObject getJsonFromResponse(Response response) throws IOException, JSONException {
        String responseString = response.body().string();
        return new JSONObject(responseString);
    }
}
