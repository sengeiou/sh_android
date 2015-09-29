package com.shootr.android.service.dataservice;

import com.shootr.android.data.entity.DeviceEntity;
import com.shootr.android.data.entity.StreamEntity;
import com.shootr.android.data.entity.SuggestedPeopleEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.db.mappers.DeviceMapper;
import com.shootr.android.db.mappers.FollowMapper;
import com.shootr.android.db.mappers.ShotEntityMapper;
import com.shootr.android.db.mappers.StreamEntityMapper;
import com.shootr.android.db.mappers.SuggestedPeopleMapper;
import com.shootr.android.db.mappers.UserMapper;
import com.shootr.android.domain.exception.ShootrError;
import com.shootr.android.domain.exception.ShootrServerException;
import com.shootr.android.domain.utils.TimeUtils;
import com.shootr.android.exception.ServerException;
import com.shootr.android.exception.ShootrDataServiceError;
import com.shootr.android.service.Endpoint;
import com.shootr.android.service.PaginatedResult;
import com.shootr.android.service.ShootrService;
import com.shootr.android.service.dataservice.dto.DeviceDtoFactory;
import com.shootr.android.service.dataservice.dto.StreamDtoFactory;
import com.shootr.android.service.dataservice.dto.UserDtoFactory;
import com.shootr.android.service.dataservice.generic.GenericDto;
import com.shootr.android.service.dataservice.generic.MetadataDto;
import com.shootr.android.service.dataservice.generic.OperationDto;
import com.shootr.android.util.VersionUpdater;
import com.sloydev.jsonadapters.JsonAdapter;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final StreamDtoFactory streamDtoFactory;
    private final DeviceDtoFactory deviceDtoFactory;

    private final UserMapper userMapper;
    private final SuggestedPeopleMapper suggestedPeopleMapper;
    private final FollowMapper followMapper;
    private final ShotEntityMapper shotEntityMapper;
    private final StreamEntityMapper streamEntityMapper;
    private final DeviceMapper deviceMapper;

    private final TimeUtils timeUtils;

    private final VersionUpdater versionUpdater;

    @Inject
    public ShootrDataService(OkHttpClient client, Endpoint endpoint, JsonAdapter jsonAdapter, UserDtoFactory userDtoFactory,
      DeviceDtoFactory deviceDtoFactory, UserMapper userMapper,
      SuggestedPeopleMapper suggestedPeopleMapper, FollowMapper followMapper, ShotEntityMapper shotEntityMapper,
      StreamDtoFactory streamDtoFactory, DeviceMapper deviceMapper, StreamEntityMapper streamEntityMapper,
      TimeUtils timeUtils, VersionUpdater versionUpdater) {
        this.client = client;
        this.endpoint = endpoint;
        this.jsonAdapter = jsonAdapter;
        this.suggestedPeopleMapper = suggestedPeopleMapper;
        this.streamDtoFactory = streamDtoFactory;
        this.userDtoFactory = userDtoFactory;
        this.deviceDtoFactory = deviceDtoFactory;
        this.userMapper = userMapper;
        this.followMapper = followMapper;
        this.shotEntityMapper = shotEntityMapper;
        this.deviceMapper = deviceMapper;
        this.streamEntityMapper = streamEntityMapper;
        this.timeUtils = timeUtils;
        this.versionUpdater = versionUpdater;
    }

    @Override
    public UserEntity getUserByIdUser(String idUser) throws IOException {
        GenericDto requestDto = userDtoFactory.getUserByUserId(idUser);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
        }else if (ops[0].getMetadata()!=null) {
            Map<String, Object> dataItem = ops[0].getData()[0];
            return userMapper.fromDto(dataItem);
        }
        return null;
    }

    @Override
    public PaginatedResult<List<UserEntity>> searchUsersByNameOrNickNamePaginated(String searchQuery,
      int pageOffset) throws IOException{
        List<UserEntity> users = new ArrayList<>();
        GenericDto requestDto = userDtoFactory.searchUserOperation(searchQuery, SEARCH_PAGE_LIMIT, pageOffset);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
        }else{
            MetadataDto metadata = ops[0].getMetadata();
            if(metadata!=null){
                Long items = metadata.getItems();
                for (int i = 0; i < items; i++) {
                    Map<String, Object> dataItem = ops[0].getData()[i];
                    users.add(userMapper.fromDto(dataItem));
                }
                if(metadata.getTotalItems()!=null){
                    int totalItems = metadata.getTotalItems().intValue();
                    return new PaginatedResult<>(users).setPageLimit(SEARCH_PAGE_LIMIT)
                            .setPageOffset(pageOffset)
                            .setTotalItems(totalItems);
                }
            }
        }
        return null;
    }

    @Override
    public DeviceEntity updateDevice(DeviceEntity device) throws IOException {
        GenericDto requestDto = deviceDtoFactory.getUpdateDeviceOperationDto(device);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
            return null;
        }
        Map<String, Object> dataItem = ops[0].getData()[0];
        DeviceEntity deviceReceived = deviceMapper.fromDto(dataItem);
        return deviceReceived;
    }

    @Override public DeviceEntity getDeviceByUniqueId(String uniqueDeviceId) throws IOException {

        return null;
    }

    @Override public StreamEntity saveStream(StreamEntity streamEntity) throws IOException {
        GenericDto requestDto = streamDtoFactory.saveStream(streamEntity);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
            return null;
        }
        StreamEntity streamsReceived = null;
        if (ops.length > 0) {
            streamsReceived = streamEntityMapper.fromDto(ops[0].getData()[0]);
        }
        return streamsReceived;
    }

    @Override public List<StreamEntity> getStreamsByIds(List<String> streamIds) throws IOException {
        List<StreamEntity> streamsReceived = new ArrayList<>();
        GenericDto requestDto = streamDtoFactory.getStreamsNotEndedByIds(streamIds);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if(ops == null || ops.length<1){
            Timber.e("Received 0 operations");
        }else{
            MetadataDto metadata = ops[0].getMetadata();
            Long items = metadata.getItems();
            for (int i = 0; i < items; i++) {
                Map<String, Object> dataItem = ops[0].getData()[i];
                streamsReceived.add(streamEntityMapper.fromDto(dataItem));
            }
        }
        return streamsReceived;
    }

    @Override public StreamEntity getStreamById(String idStream) throws IOException {
        GenericDto requestDto = streamDtoFactory.getStreamById(idStream);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if(ops == null || ops.length<1){
            Timber.e("Received 0 operations");
            return null;
        }
        StreamEntity streamsReceived = null;
        if(ops.length>0){
            streamsReceived = streamEntityMapper.fromDto(ops[0].getData()[0]);
        }
        return streamsReceived;

    }

    @Override public UserEntity saveUserProfile(UserEntity userEntity) throws IOException {
        GenericDto requestDto = userDtoFactory.saveUserDto(userEntity);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
        } else {
            Map<String, Object> resultDto = ops[0].getData()[0];
            return userMapper.fromDto(resultDto);
        }
        return null;
    }

    @Override public void performCheckin(String idUser, String idStream) throws IOException {
        GenericDto checkinDto = userDtoFactory.getCheckinOperationDto(idUser, idStream);
        postRequest(checkinDto);
    }

    @Override
    public UserEntity getUserByUsername(String username) throws IOException {
        GenericDto requestDto = userDtoFactory.getUserByUsername(username);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
        }else {
            Map<String, Object>[] data = ops[0].getData();
            if (data.length > 0) {
                Map<String, Object> dataItem = data[0];
                return userMapper.fromDto(dataItem);
            }
        }
        return null;
    }

    @Override public void logout(String idUser, String idDevice) throws IOException {
        GenericDto logoutOperationDto = userDtoFactory.getLogoutOperationDto(idUser, idDevice);
        postRequest(logoutOperationDto);
    }

    @Override public List<SuggestedPeopleEntity> getSuggestedPeople(String currentUserId) throws IOException {
        List<SuggestedPeopleEntity> suggestedPeopleEntities = new ArrayList<>();
        GenericDto requestDto = userDtoFactory.getSuggestedPeople(currentUserId);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
        } else {
            MetadataDto md = ops[0].getMetadata();
            Long items = md.getItems();
            for (int i = 0; i < items; i++) {
                Map<String, Object> dataItem = ops[0].getData()[i];
                suggestedPeopleEntities.add(suggestedPeopleMapper.fromDto(dataItem));
            }
        }
        return suggestedPeopleEntities;
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
