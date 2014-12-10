package com.shootr.android.service.dataservice;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shootr.android.data.SessionManager;
import com.shootr.android.exception.ShootrError;
import com.shootr.android.service.PhotoService;
import com.shootr.android.service.ShootrPhotoUploadError;
import com.shootr.android.service.ShootrServerException;
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

    // TODO invertir dependencia
    public static final String ENDPOINT_UPLOAD_PHOTO_PROFILE =
      "http://tst.shootermessenger.com/shootr-services/rest/upload/img/profile";

    public static final String ENDPOINT_UPLOAD_PHOTO_SHOT =
      "http://tst.shootermessenger.com/shootr-services/rest/upload/img/shot";

    private OkHttpClient client;
    private SessionManager sessionManager;
    private ObjectMapper objectMapper;

    @Inject public ShootrPhotoService(OkHttpClient client, SessionManager sessionManager, ObjectMapper objectMapper) {
        this.client = client;
        this.sessionManager = sessionManager;
        this.objectMapper = objectMapper;
    }

    @Override public String uploadProfilePhotoAndGetUrl(File photoFile) throws IOException, JSONException {
        Timber.d("Uploading photo with file path %s", photoFile.getAbsolutePath());
        RequestBody body = buildRequestBody(photoFile);

        Response response = executeProfileRequest(body);

        return parseProfileResponse(response);
    }

    @Override public String uploadShotImageAndGetUrl(File imageFile) throws IOException {
        Timber.d("Uploading image with file path %s", imageFile.getAbsolutePath());
        RequestBody body = buildRequestBody(imageFile);
        Response response = executeShotImageRequest(body);
        return parseShotImageResponse(response);
    }

    private RequestBody buildRequestBody(File photoFile) {
        return new MultipartBuilder().type(MultipartBuilder.FORM)
          .addPart(Headers.of("Content-Disposition", "form-data; name=\"idUser\""),
            RequestBody.create(MediaType.parse("text/plain"), String.valueOf(sessionManager.getCurrentUserId())))
          .addPart(Headers.of("Content-Disposition", "form-data; name=\"sessionToken\""),
            RequestBody.create(MediaType.parse("text/plain"), sessionManager.getSessionToken()))
          .addPart(Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\"photo.jpeg"),
            RequestBody.create(MediaType.parse("image/jpeg"), photoFile))
          .build();
    }



    private Response executeProfileRequest(RequestBody body) throws IOException {
        return executeRequest(body, ENDPOINT_UPLOAD_PHOTO_PROFILE);
    }

    private Response executeShotImageRequest(RequestBody body) throws IOException {
        return executeRequest(body, ENDPOINT_UPLOAD_PHOTO_SHOT);
    }

    private Response executeRequest(RequestBody body, String endpoint) throws IOException {
        Request request = new Request.Builder()
          .url(endpoint)
          .post(body)
          .build();

        return client.newCall(request).execute();
    }

    private String parseShotImageResponse(Response response) throws IOException {
        try {
            JSONObject responseJson = getJsonFromResponse(response);
            return readJsonFromShotImage(responseJson);
        } catch (JSONException e) {
            ShootrError
              jsonError = new ShootrPhotoUploadError(ShootrError.ERROR_CODE_UNKNOWN_ERROR, "JSONException", "Error while parsing response JSON. Response received:"+ response.body().string());
            throw new ShootrServerException(jsonError);
        }
    }

    private String parseProfileResponse(Response response) throws IOException {
        try {
            JSONObject responseJson = getJsonFromResponse(response);
            return readJsonFromProfilePhoto(responseJson);
        } catch (JSONException e) {
            ShootrError
              jsonError = new ShootrPhotoUploadError(ShootrError.ERROR_CODE_UNKNOWN_ERROR, "JSONException", "Error while parsing response JSON. Response received:"+ response.body().string());
            throw new ShootrServerException(jsonError);
        }
    }

    private String readJsonFromProfilePhoto(JSONObject jsonObject) throws JSONException, IOException {
        String profileThumbnailImageUrl = getThumbnailUrlFromJson(jsonObject);

        boolean imageUploadedSuccessfully = profileThumbnailImageUrl != null && !profileThumbnailImageUrl.isEmpty();
        if (imageUploadedSuccessfully) {
            Timber.d("Photo uploaded to url: %s", profileThumbnailImageUrl);
            return profileThumbnailImageUrl;
        } else {
            return throwParsedError(jsonObject);
        }
    }


    private String readJsonFromShotImage(JSONObject jsonObject) throws IOException, JSONException {
        String imageUrl = getImageUrlFromJson(jsonObject);

        boolean imageUploadedSuccessfully = imageUrl != null && !imageUrl.isEmpty();
        if (imageUploadedSuccessfully) {
            Timber.d("Image uploaded to url: %s", imageUrl);
            return imageUrl;
        } else {
            return throwParsedError(jsonObject);
        }
    }

    private String throwParsedError(JSONObject jsonObject) throws IOException, JSONException {
        ShootrPhotoUploadError
          shootrError = objectMapper.readValue(jsonObject.getString("status"), ShootrPhotoUploadError.class);
        ShootrServerException shootrServerException = new ShootrServerException(shootrError);
        Timber.e(shootrServerException, "Photo not received, ShootrError: %s - %s", shootrError.getErrorCode(),
          shootrError.getMessage());
        throw shootrServerException;
    }

    private String getThumbnailUrlFromJson(JSONObject responseJson) {
        return responseJson.optString("profileThumbnailImageUrl");
    }

    private String getImageUrlFromJson(JSONObject responseJson) {
        return responseJson.optString("shotImageUrl");
    }

    private JSONObject getJsonFromResponse(Response response) throws IOException, JSONException {
        String responseString = response.body().string();
        return new JSONObject(responseString);
    }
}
