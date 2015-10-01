package com.shootr.android.service.dataservice;

import com.shootr.android.db.mappers.FollowMapper;
import com.shootr.android.db.mappers.ShotEntityMapper;
import com.shootr.android.db.mappers.SuggestedPeopleMapper;
import com.shootr.android.db.mappers.UserMapper;
import com.shootr.android.domain.exception.ShootrError;
import com.shootr.android.domain.exception.ShootrServerException;
import com.shootr.android.domain.utils.TimeUtils;
import com.shootr.android.exception.ServerException;
import com.shootr.android.exception.ShootrDataServiceError;
import com.shootr.android.service.Endpoint;
import com.shootr.android.service.ShootrService;
import com.shootr.android.service.dataservice.dto.UserDtoFactory;
import com.shootr.android.service.dataservice.generic.GenericDto;
import com.shootr.android.util.VersionUpdater;
import com.sloydev.jsonadapters.JsonAdapter;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.io.IOException;
import javax.inject.Inject;
import timber.log.Timber;

public class ShootrDataService implements ShootrService {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final Integer SEARCH_PAGE_LIMIT = 8;
    public static final String DATA_SERVICES_PATH = "/data/rest/generic";

    private final OkHttpClient client;
    private final Endpoint endpoint;
    private final JsonAdapter jsonAdapter;

    private final UserDtoFactory userDtoFactory;

    private final UserMapper userMapper;
    private final SuggestedPeopleMapper suggestedPeopleMapper;
    private final FollowMapper followMapper;
    private final ShotEntityMapper shotEntityMapper;

    private final TimeUtils timeUtils;

    private final VersionUpdater versionUpdater;

    @Inject
    public ShootrDataService(OkHttpClient client,
      Endpoint endpoint,
      JsonAdapter jsonAdapter,
      UserDtoFactory userDtoFactory,
      UserMapper userMapper,
      SuggestedPeopleMapper suggestedPeopleMapper,
      FollowMapper followMapper,
      ShotEntityMapper shotEntityMapper,
      TimeUtils timeUtils,
      VersionUpdater versionUpdater) {
        this.client = client;
        this.endpoint = endpoint;
        this.jsonAdapter = jsonAdapter;
        this.suggestedPeopleMapper = suggestedPeopleMapper;
        this.userDtoFactory = userDtoFactory;
        this.userMapper = userMapper;
        this.followMapper = followMapper;
        this.shotEntityMapper = shotEntityMapper;
        this.timeUtils = timeUtils;
        this.versionUpdater = versionUpdater;
    }

    private GenericDto postRequest(GenericDto dto) throws IOException {
        // Create the request
        String requestJson = jsonAdapter.toJson(dto);
        Timber.d("Executing request: %s", requestJson);
        RequestBody body = RequestBody.create(JSON, requestJson);
        Request request = new Request.Builder().url(getDataservicesUrl()).post(body).build();

        // Execute request
        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            Timber.e(e, "Error executing the request");
            throw new ServerException(ServerException.V999);
        }

        // No response received, failed silently
        if (response == null) {
            Timber.e("Server response is null");
            throw new ServerException(ServerException.V999);
        }

        // Response received, check status and return value
        // TODO check operations field is not empty
        if (response.isSuccessful()) {
            String responseBody = response.body().string();
            Timber.d("Response received: %s", responseBody);
            GenericDto genericDto = jsonAdapter.fromJson(responseBody, GenericDto.class);
            // Check for Data-Service errors

            updateTimeFromServer(genericDto);
            String statusCode = genericDto.getStatusCode();
            if (statusCode == null || !ShootrError.OK.equals(statusCode)) {
                String statusMessage = genericDto.getStatusMessage();
                String statusSubcode = genericDto.getStatusSubcode();

                if (statusSubcode != null) {
                    ShootrError shootrError = new ShootrDataServiceError(statusSubcode, statusMessage);
                    if (shootrError.getErrorCode().equals(ShootrError.ERROR_CODE_UPDATE_REQUIRED)) {
                        versionUpdater.notifyUpdateRequired();
                    }
                    throw new ShootrServerException(shootrError);
                }

                ServerException serverException = new ServerException(statusCode, statusMessage);
                Timber.e(serverException, "Server error with status code %s", statusCode);
                throw serverException;
            }
            return genericDto;
        } else {
            Timber.e("Server response unsuccesfull with code %d: %s", response.code(), response.message());
            throw new ServerException(ServerException.V999);
        }
    }

    private String getDataservicesUrl() {
        return endpoint.getUrl()+ DATA_SERVICES_PATH;
    }

    private void updateTimeFromServer(GenericDto dto) {
        //TODO what to do, what to do. This was a fix, but the real problem is in server ¬¬
        // They are going a minute ahead
    }
}
