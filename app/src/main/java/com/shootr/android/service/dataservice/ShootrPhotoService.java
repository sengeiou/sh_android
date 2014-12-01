package com.shootr.android.service.dataservice;

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
    public static final String ENDPOINT_UPLOAD_PHOTO =
      "http://tst.shootermessenger.com/shootr-services/rest/upload/img";

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

        Response response = executeRequest(body);

        return parseResponse(response);
    }

    private String parseResponse(Response response) throws IOException {
        try {
            if (response.isSuccessful()) {
                JSONObject responseJson = getJsonFromResponse(response);

                String profileThumbnailImageUrl = getPhotoUrlFromJson(responseJson);

                boolean imageUploadedSuccessfully = profileThumbnailImageUrl != null && !profileThumbnailImageUrl.isEmpty();
                if (imageUploadedSuccessfully) {
                    Timber.d("Photo uploaded to url: %s", profileThumbnailImageUrl);
                    return profileThumbnailImageUrl;
                } else {
                    ShootrPhotoUploadError
                      shootrError = objectMapper.readValue(responseJson.getString("status"), ShootrPhotoUploadError.class);
                    ShootrServerException shootrServerException = new ShootrServerException(shootrError);
                    Timber.e(shootrServerException, "Photo not received, ShootrError: %s - %s",
                      shootrError.getErrorCode(), shootrError.getMessage());
                    throw shootrServerException;
                }
            } else {
                Timber.e("Response not successful: %d", response.code());
                Timber.e("Photo not received.");
                JSONObject responseJson = getJsonFromResponse(response);
                ShootrPhotoUploadError
                  shootrError = objectMapper.readValue(responseJson.getString("status"), ShootrPhotoUploadError.class);
                throw new ShootrServerException(shootrError);
            }
        } catch (JSONException e) {
            ShootrError
              jsonError = new ShootrPhotoUploadError(ShootrError.ERROR_CODE_UNKNOWN_ERROR, "JSONException", "Error while parsing response JSON. Response received:"+ response.body().string());
            throw new ShootrServerException(jsonError);
        }
    }

    private String getPhotoUrlFromJson(JSONObject responseJson) {
        return responseJson.optString("profileThumbnailImageUrl");
    }

    private JSONObject getJsonFromResponse(Response response) throws IOException, JSONException {
        String responseString = response.body().string();
        return new JSONObject(responseString);
    }

    private Response executeRequest(RequestBody body) throws IOException {
        Request request = new Request.Builder()
          .url(ENDPOINT_UPLOAD_PHOTO)
          .post(body)
          .build();

        return client.newCall(request).execute();
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
}
