package com.shootr.android.service.dataservice;

import com.shootr.android.data.SessionManager;
import com.shootr.android.service.PhotoService;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.inject.Inject;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

public class ShootrPhotoService implements PhotoService {

    private OkHttpClient client;
    private SessionManager sessionManager;

    @Inject public ShootrPhotoService(OkHttpClient client, SessionManager sessionManager) {
        this.client = client;
        this.sessionManager = sessionManager;
    }

    @Override public String uploadPhotoAndGetUrl(File photoFile) throws IOException, JSONException {
        Timber.d("Uploading photo with file path %s", photoFile.getAbsolutePath());
        RequestBody body = new MultipartBuilder().type(MultipartBuilder.FORM)
          .addPart(Headers.of("Content-Disposition", "form-data; name=\"idUser\""),
            RequestBody.create(MediaType.parse("text/plain"), String.valueOf(sessionManager.getCurrentUserId())))
          .addPart(Headers.of("Content-Disposition", "form-data; name=\"sessionToken\""),
            RequestBody.create(MediaType.parse("text/plain"), sessionManager.getCurrentUser().getSessionToken()))
          .addPart(Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\"photo.jpeg"),
            RequestBody.create(MediaType.parse("image/jpeg"), photoFile))
          .build();

        Request request = new Request.Builder().url("http://tst.shootermessenger.com/shootr-services/rest/upload/img")
          .post(body)
          .build();
        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            String responseString = response.body().string();
            JSONObject responseJson = new JSONObject(responseString);
            String profileThumbnailImageUrl = responseJson.getString("profileThumbnailImageUrl");
            Timber.d("Photo uploaded to url: %s", profileThumbnailImageUrl);
            return profileThumbnailImageUrl;
        } else {
            Timber.e("Response not successful: %d", response.code());
            Timber.e("Response body: %s", response.body().string());
        }
        return null;
    }
}
