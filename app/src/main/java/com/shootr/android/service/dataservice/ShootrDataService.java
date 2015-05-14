package com.shootr.android.service.dataservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shootr.android.data.entity.DeviceEntity;
import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.entity.EventSearchEntity;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.entity.UserCreateAccountEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.db.mappers.DeviceMapper;
import com.shootr.android.db.mappers.EventEntityMapper;
import com.shootr.android.db.mappers.FollowMapper;
import com.shootr.android.db.mappers.ShotEntityMapper;
import com.shootr.android.db.mappers.UserMapper;
import com.shootr.android.domain.TimelineParameters;
import com.shootr.android.domain.exception.ShootrError;
import com.shootr.android.domain.exception.ShootrServerException;
import com.shootr.android.domain.utils.TimeUtils;
import com.shootr.android.exception.ServerException;
import com.shootr.android.exception.ShootrDataServiceError;
import com.shootr.android.service.Endpoint;
import com.shootr.android.service.PaginatedResult;
import com.shootr.android.service.ShootrService;
import com.shootr.android.service.dataservice.dto.DeviceDtoFactory;
import com.shootr.android.service.dataservice.dto.EventDtoFactory;
import com.shootr.android.service.dataservice.dto.ShotDtoFactory;
import com.shootr.android.service.dataservice.dto.TimelineDtoFactory;
import com.shootr.android.service.dataservice.dto.UserDtoFactory;
import com.shootr.android.service.dataservice.generic.GenericDto;
import com.shootr.android.service.dataservice.generic.MetadataDto;
import com.shootr.android.service.dataservice.generic.OperationDto;
import com.shootr.android.service.dataservice.generic.RequestorDto;
import com.shootr.android.util.SecurityUtils;
import com.shootr.android.util.VersionUpdater;
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
    public static final long DEFAULT_LIMIT = 100L;
    private static final Integer SEARCH_PAGE_LIMIT = 8;

    private final OkHttpClient client;
    private final Endpoint endpoint;
    private final ObjectMapper mapper;

    private final UserDtoFactory userDtoFactory;
    private final TimelineDtoFactory timelineDtoFactory;
    private final ShotDtoFactory shotDtoFactory;
    private final EventDtoFactory eventDtoFactory;
    private final DeviceDtoFactory deviceDtoFactory;

    private final UserMapper userMapper;
    private final FollowMapper followMapper;
    private final ShotEntityMapper shotEntityMapper;
    private final EventEntityMapper eventEntityMapper;
    private final DeviceMapper deviceMapper;

    private final TimeUtils timeUtils;

    private final VersionUpdater versionUpdater;

    @Inject
    public ShootrDataService(OkHttpClient client, Endpoint endpoint, ObjectMapper mapper, UserDtoFactory userDtoFactory,
      TimelineDtoFactory timelineDtoFactory, ShotDtoFactory shotDtoFactory, DeviceDtoFactory deviceDtoFactory,
      UserMapper userMapper, FollowMapper followMapper, ShotEntityMapper shotEntityMapper,
      EventDtoFactory eventDtoFactory, DeviceMapper deviceMapper, EventEntityMapper eventEntityMapper, TimeUtils timeUtils,
      VersionUpdater versionUpdater) {
        this.client = client;
        this.endpoint = endpoint;
        this.mapper = mapper;
        this.eventDtoFactory = eventDtoFactory;
        this.userDtoFactory = userDtoFactory;
        this.timelineDtoFactory = timelineDtoFactory;
        this.shotDtoFactory = shotDtoFactory;
        this.deviceDtoFactory = deviceDtoFactory;
        this.userMapper = userMapper;
        this.followMapper = followMapper;
        this.shotEntityMapper = shotEntityMapper;
        this.deviceMapper = deviceMapper;
        this.eventEntityMapper = eventEntityMapper;

        this.timeUtils = timeUtils;
        this.versionUpdater = versionUpdater;
    }

    @Override
    public UserEntity login(String id, String password) throws IOException {
        GenericDto loginDto = userDtoFactory.getLoginOperationDto(id, SecurityUtils.encodePassword(password));
        GenericDto responseDto = postRequest(loginDto);
        OperationDto[] ops = responseDto.getOps();
        Map<String, Object>[] data = ops[0].getData();
        return userMapper.fromDto(data[0]);
    }

    @Override public List<UserEntity> getFollowing(String idUser, Long lastModifiedDate) throws IOException {
        List<FollowEntity> follows = new ArrayList<>();
        List<String> userIds = new ArrayList<>();
        boolean includeDeleted = lastModifiedDate > 0L;
        GenericDto requestDto = userDtoFactory.getFollowingsOperationDto(idUser, 0L, lastModifiedDate, includeDeleted);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        Map<String, Object>[] data = ops[0].getData();
        for (Map<String, Object> d : data) {
            FollowEntity followEntity = followMapper.fromDto(d);
            follows.add(followEntity);
            userIds.add(followEntity.getFollowedUser());
        }
        List<UserEntity> usersById = new ArrayList<>();
        if(userIds.size()>0){
            usersById = getUsersById(userIds);
        }
        //TODO order
        return usersById;
    }

    @Override public List<UserEntity> getUsersById(List<String> userIds) throws IOException{
        List<UserEntity> users = new ArrayList<>();
        GenericDto requestDto = userDtoFactory.getUsersOperationDto(userIds);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
        }else if (ops[0].getMetadata() != null) {
            Map<String, Object>[] data = ops[0].getData();
            for(Map<String,Object> d:data){
                UserEntity user = userMapper.fromDto(d);
                users.add(user);
            }
        }
        return users;
    }

    @Override public List<UserEntity> getFollowers(String idUserFollowed, Long lastModifiedDate) throws IOException {
        List<FollowEntity> follows = new ArrayList<>();
        List<String> userIds = new ArrayList<>();
        GenericDto requestDto = userDtoFactory.getFollowersOperationDto(idUserFollowed, 0L, lastModifiedDate, false);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        Map<String, Object>[] data = ops[0].getData();
        for(Map<String,Object> d:data){
            FollowEntity followEntity = followMapper.fromDto(d);
            follows.add(followEntity);
            userIds.add(followEntity.getIdUser());
        }
        List<UserEntity> usersById = new ArrayList<>();
        if(userIds.size()>0){
            usersById = getUsersById(userIds);
        }
        //TODO order
        return usersById;
    }

    @Override
    public ShotEntity getShotById(String idShot) throws IOException {
        GenericDto requestDto = shotDtoFactory.getSingleShotOperationDto(idShot);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
        }else if(ops[0].getMetadata() != null) {
            Map<String, Object> data = ops[0].getData()[0];
            return shotEntityMapper.fromDto(data);

        }
        return null;
    }

    @Override public List<ShotEntity> getShotsByParameters(TimelineParameters parameters) throws IOException {
        GenericDto genericDto = timelineDtoFactory.getTimelineOperationDto(parameters);
        GenericDto responseDto = postRequest(genericDto);
        OperationDto[] ops = responseDto.getOps();

        List<ShotEntity> resultShots = new ArrayList<>();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
        }else if(ops[0].getMetadata() != null) {
            Map<String, Object>[] data = ops[0].getData();
            for (Map<String, Object> aData : data) {
                ShotEntity shot = shotEntityMapper.fromDto(aData);
                resultShots.add(shot);
            }
        }
        return resultShots;
    }

    @Override
    public List<ShotEntity> getNewShots(List<Long> followingUserIds, Long newestShotDate) throws IOException {
        List<ShotEntity> newerShots = new ArrayList<>();
        GenericDto genericDto =
          timelineDtoFactory.getNewerShotsOperationDto(followingUserIds, newestShotDate, DEFAULT_LIMIT);
        GenericDto responseDto = postRequest(genericDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
        }else if(ops[0].getMetadata() != null) {
            Map<String, Object>[] data = ops[0].getData();
            for (Map<String, Object> aData : data) {
                ShotEntity shot = shotEntityMapper.fromDto(aData);
                newerShots.add(shot);
            }

        }
        return newerShots;
    }

    @Override
    public List<ShotEntity> getOlderShots(List<Long> followingUserIds, Long lastModifiedDate) throws IOException {
        List<ShotEntity> olderShots = new ArrayList<>();
        GenericDto genericDto =
          timelineDtoFactory.getOlderShotsOperationDto(followingUserIds, lastModifiedDate, DEFAULT_LIMIT);
        GenericDto responseDto = postRequest(genericDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
        }else if(ops[0].getMetadata() != null) {
            Map<String, Object>[] data = ops[0].getData();
            for (Map<String, Object> aData : data) {
                ShotEntity shot = shotEntityMapper.fromDto(aData);
                olderShots.add(shot);
            }

        }
        return olderShots;
    }

    @Override
    public List<ShotEntity> getShotsByUserIdList(List<Long> followingUserIds, Long lastModifiedDate) throws IOException {
        List<ShotEntity> shots = new ArrayList<>();
        GenericDto genericDto = timelineDtoFactory.getNewerShotsOperationDto(followingUserIds, lastModifiedDate, DEFAULT_LIMIT);
        GenericDto responseDto = postRequest(genericDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
        }else if (ops[0].getMetadata() != null) {
            Map<String, Object>[] data = ops[0].getData();
            for (Map<String, Object> aData : data) {
                ShotEntity shot = shotEntityMapper.fromDto(aData);
                shots.add(shot);
            }
        }
        return shots;
    }

    @Override public List<ShotEntity> getRepliesToShot(String shotId) throws IOException {
        List<ShotEntity> shots = new ArrayList<>();
        GenericDto genericDto = shotDtoFactory.getRepliesOperationDto(shotId);
        GenericDto responseDto = postRequest(genericDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
        }else if (ops[0].getMetadata() != null) {
            Map<String, Object>[] data = ops[0].getData();
            for (Map<String, Object> aData : data) {
                ShotEntity shot = shotEntityMapper.fromDto(aData);
                shots.add(shot);
                }
        }
        return shots;
    }

    @Override public ShotEntity postNewShotWithImage(ShotEntity shotTemplate) throws IOException {
        GenericDto requestDto = shotDtoFactory.getNewShotOperationDto(shotTemplate);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
        }else if (ops[0].getMetadata() != null) {
            Map<String, Object> dataItem = ops[0].getData()[0];
            return shotEntityMapper.fromDto(dataItem);
        }
        return null;
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

    @Override public FollowEntity getFollowByIdUserFollowed(String idCurrentUser,String idUser) throws IOException {
        GenericDto requestDto = userDtoFactory.getFollowUserDtoByIdUser(idCurrentUser, idUser);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
            return null;
        }
        if(ops[0] != null){
            if(ops[0].getData() != null && ops[0].getData().length > 0){
                Map<String, Object> dataItem = ops[0].getData()[0];
                FollowEntity followReceived = followMapper.fromDto(dataItem);
                return followReceived;
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

    @Override public FollowEntity followUser(FollowEntity follow) throws IOException {
        GenericDto requestDto = userDtoFactory.followUserDto(follow);
        GenericDto reponseDto = postRequest(requestDto);
        OperationDto[] ops = reponseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
            return null;
        }
        Map<String, Object> dataItem = ops[0].getData()[0];
        FollowEntity followReceived = followMapper.fromDto(dataItem);
        return followReceived;
    }

    @Override public FollowEntity unfollowUser(FollowEntity follow) throws IOException {
        GenericDto requestDto = userDtoFactory.unfollowUserDto(follow);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
            return null;
        }
        Map<String, Object> dataItem = ops[0].getData()[0];
        FollowEntity followReceived = followMapper.fromDto(dataItem);
        return followReceived;
    }

    @Override public EventEntity saveEvent(EventEntity eventEntity) throws IOException {
        GenericDto requestDto = eventDtoFactory.saveEvent(eventEntity);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
            return null;
        }
        EventEntity eventsReceived = null;
        if (ops.length > 0) {
            eventsReceived = eventEntityMapper.fromDto(ops[0].getData()[0]);
        }
        return eventsReceived;
    }

    @Override public List<EventEntity> getEventsByIds(List<Long> eventIds) throws IOException {
        List<EventEntity> eventsReceived = new ArrayList<>();
        GenericDto requestDto = eventDtoFactory.getEventsNotEndedByIds(eventIds);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if(ops == null || ops.length<1){
            Timber.e("Received 0 operations");
        }else{
            MetadataDto metadata = ops[0].getMetadata();
            Long items = metadata.getItems();
            for (int i = 0; i < items; i++) {
                Map<String, Object> dataItem = ops[0].getData()[i];
                eventsReceived.add(eventEntityMapper.fromDto(dataItem));
            }
        }
        return eventsReceived;
    }

    @Override public EventEntity getEventById(String idEvent) throws IOException {
        GenericDto requestDto = eventDtoFactory.getEventById(idEvent);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if(ops == null || ops.length<1){
            Timber.e("Received 0 operations");
            return null;
        }
        EventEntity eventsReceived = null;
        if(ops.length>0){
            eventsReceived = eventEntityMapper.fromDto(ops[0].getData()[0]);
        }
        return eventsReceived;

    }

    @Override public List<ShotEntity> getLatestsShotsFromIdUser(String idUser, Long latestShotNumber) throws IOException {
        List<ShotEntity> shotEntities = new ArrayList<>();
        GenericDto requestDto = shotDtoFactory.getLatestShotsFromIdUser(idUser, latestShotNumber);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if(ops == null || ops.length<1){
            Timber.e("Received 0 operations");
        }else{
            MetadataDto md = ops[0].getMetadata();
            Long items = md.getItems();
            for(int i = 0; i< items; i++){
                Map<String, Object> dataItem = ops[0].getData()[i];
                shotEntities.add(shotEntityMapper.fromDto(dataItem));
            }
        }
        return shotEntities;
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

    @Override public List<EventSearchEntity> getEventSearch(String query, Map<String, Integer> eventsWatchesCounts)
      throws IOException {
        List<EventSearchEntity> eventSearchResults = new ArrayList<>();
        GenericDto requestDto = eventDtoFactory.getSearchEventDto(query, eventsWatchesCounts);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if(ops == null || ops.length<1){
            Timber.e("Received 0 operations");
        }else{
            MetadataDto metadata = ops[0].getMetadata();
            Long items = metadata.getItems();
            for (int i = 0; i < items; i++) {
                Map<String, Object> dataItem = ops[0].getData()[i];
                eventSearchResults.add(eventEntityMapper.fromSearchDto(dataItem));
            }
        }
        return eventSearchResults;
    }

    @Override public void performCheckin(String idUser, String idEvent) throws IOException {
        GenericDto checkinDto = userDtoFactory.getCheckinOperationDto(idUser, idEvent);
        GenericDto responseDto = postRequest(checkinDto);
        // We are done... right? Any errors should have been thrown already. I'm not expecting any value
    }

    @Override public void createAccount(UserCreateAccountEntity userCreateAccountEntity) throws IOException{
        GenericDto createAccountDto = userDtoFactory.getCreateAccountOperationDto(userCreateAccountEntity);
        GenericDto responseDto = postRequest(createAccountDto);
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

    private GenericDto postRequest(GenericDto dto) throws IOException {
        // Create the request
        String requestJson = mapper.writeValueAsString(dto);
        Timber.d("Executing request: %s", requestJson);
        RequestBody body = RequestBody.create(JSON, requestJson);
        Request request = new Request.Builder().url(endpoint.getUrl()).post(body).build();

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
            GenericDto genericDto = mapper.readValue(responseBody, GenericDto.class);
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

    private void updateTimeFromServer(GenericDto dto) {
        Long serverTime = dto.getRequestor().getReq()[RequestorDto.POSITION_SYSTEM_TIME];
        if (serverTime != null) {
            timeUtils.setCurrentTime(serverTime);
        }
    }
}
