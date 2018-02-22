package com.shootr.mobile.service;

import com.shootr.mobile.data.api.service.LogsApiService;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.io.IOException;
import javax.inject.Inject;
import org.json.JSONException;
import org.json.JSONObject;
import timber.log.Timber;

public class ShootrLogsService implements LogsApiService {

  private OkHttpClient client;
  private SessionRepository sessionRepository;

  @Inject public ShootrLogsService(OkHttpClient client, SessionRepository sessionRepository) {
    this.client = client;
    this.sessionRepository = sessionRepository;

    this.client = new OkHttpClient();
  }

  @Override public String uploadLogs(byte[] logs) {
    Timber.d("Uploading Logins: Connecting to server");

    Response response = null;
    try {
      RequestBody body = buildRequestBody(logs);
      response = executeLogRequest(body);

      return parseResponse(response);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  private RequestBody buildRequestBody(byte[] logs) {
    return RequestBody.create(MediaType.parse("application/vnd.deflate"), logs);
  }

  private Response executeLogRequest(RequestBody body) throws IOException {
    return executeRequest(body, sessionRepository.getLogAddress());
  }

  private Response executeRequest(RequestBody body, String endpoint) throws IOException {
    Request request = new Request.Builder().url(endpoint)
        .addHeader("Content-Type", "application/vnd.deflate")
        .post(body)
        .build();

    Response response = client.newCall(request).execute();
    Timber.d("Uploading Logins: %s - %s", response.message(), endpoint);
    return response;
  }

  private String parseResponse(Response response) throws IOException {
    try {
      JSONObject responseJson = getJsonFromResponse(response);
      Timber.d("ERROR Uploading Logins: %s", responseJson.toString());
      return responseJson.toString();
    } catch (JSONException e) {
      return response.networkResponse().toString();
    }
  }

  private JSONObject getJsonFromResponse(Response response) throws IOException, JSONException {
    String responseString = response.body().string();
    return new JSONObject(responseString);
  }
}
