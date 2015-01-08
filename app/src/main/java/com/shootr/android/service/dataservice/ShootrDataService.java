package com.shootr.android.service.dataservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shootr.android.db.mappers.DeviceMapper;
import com.shootr.android.db.mappers.FollowMapper;
import com.shootr.android.db.mappers.MatchMapper;
import com.shootr.android.db.mappers.ShotMapper;
import com.shootr.android.db.mappers.TeamMapper;
import com.shootr.android.db.mappers.UserMapper;
import com.shootr.android.db.mappers.WatchMapper;
import com.shootr.android.data.entity.DeviceEntity;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.entity.MatchEntity;
import com.shootr.android.data.entity.ShotEntity;
import com.shootr.android.data.entity.TeamEntity;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.data.entity.WatchEntity;
import com.shootr.android.exception.ServerException;
import com.shootr.android.exception.ShootrDataServiceError;
import com.shootr.android.exception.ShootrError;
import com.shootr.android.service.Endpoint;
import com.shootr.android.service.PaginatedResult;
import com.shootr.android.service.ShootrServerException;
import com.shootr.android.service.ShootrService;
import com.shootr.android.service.dataservice.dto.DeviceDtoFactory;
import com.shootr.android.service.dataservice.dto.MatchDtoFactory;
import com.shootr.android.service.dataservice.dto.ShotDtoFactory;
import com.shootr.android.service.dataservice.dto.TeamDtoFactory;
import com.shootr.android.service.dataservice.dto.TimelineDtoFactory;
import com.shootr.android.service.dataservice.dto.UserDtoFactory;
import com.shootr.android.service.dataservice.generic.GenericDto;
import com.shootr.android.service.dataservice.generic.MetadataDto;
import com.shootr.android.service.dataservice.generic.OperationDto;
import com.shootr.android.service.dataservice.generic.RequestorDto;
import com.shootr.android.util.SecurityUtils;
import com.shootr.android.util.TimeUtils;
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
    public static final long DEFAULT_LIMIT = 10L;
    private static final Integer SEARCH_PAGE_LIMIT = 8;

    private OkHttpClient client;
    private Endpoint endpoint;
    private ObjectMapper mapper;

    private UserDtoFactory userDtoFactory;
    private TimelineDtoFactory timelineDtoFactory;
    private ShotDtoFactory shotDtoFactory;
    private MatchDtoFactory matchDtoFactory;
    private DeviceDtoFactory deviceDtoFactory;
    private TeamDtoFactory teamDtoFactory;

    private UserMapper userMapper;
    private FollowMapper followMapper;
    private ShotMapper shotMapper;
    private MatchMapper matchMapper;
    private DeviceMapper deviceMapper;
    private WatchMapper watchMapper;
    private TeamMapper teamMapper;
    private TimeUtils timeUtils;

    @Inject
    public ShootrDataService(OkHttpClient client, Endpoint endpoint, ObjectMapper mapper, UserDtoFactory userDtoFactory,
      TimelineDtoFactory timelineDtoFactory, ShotDtoFactory shotDtoFactory, DeviceDtoFactory deviceDtoFactory,
      TeamDtoFactory teamDtoFactory, UserMapper userMapper, FollowMapper followMapper, ShotMapper shotMapper,
      MatchDtoFactory matchDtoFactory, DeviceMapper deviceMapper, WatchMapper watchMapper, MatchMapper matchMapper,
      TeamMapper teamMapper, TimeUtils timeUtils) {
        this.client = client;
        this.endpoint = endpoint;
        this.mapper = mapper;
        this.teamDtoFactory = teamDtoFactory;
        this.matchDtoFactory = matchDtoFactory;
        this.userDtoFactory = userDtoFactory;
        this.timelineDtoFactory = timelineDtoFactory;
        this.shotDtoFactory = shotDtoFactory;
        this.deviceDtoFactory = deviceDtoFactory;
        this.userMapper = userMapper;
        this.followMapper = followMapper;
        this.shotMapper = shotMapper;
        this.deviceMapper = deviceMapper;
        this.matchMapper = matchMapper;
        this.watchMapper = watchMapper;
        this.teamMapper = teamMapper;

        this.timeUtils = timeUtils;
    }

    @Override
    public UserEntity login(String id, String password) throws IOException {
        GenericDto loginDto = userDtoFactory.getLoginOperationDto(id, SecurityUtils.encodePassword(password));
        GenericDto responseDto = postRequest(loginDto);
        OperationDto[] ops = responseDto.getOps();
        Map<String, Object>[] data = ops[0].getData();
        return userMapper.fromDto(data[0]);
    }

    @Override public List<UserEntity> getFollowing(Long idUser, Long lastModifiedDate) throws IOException {
        List<UserEntity> following = new ArrayList<>();
        boolean includeDeleted = lastModifiedDate > 0L;
        GenericDto requestDto = userDtoFactory.getFollowingsOperationDto(idUser, 0L, lastModifiedDate, includeDeleted);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();

        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
        } else if (ops[0].getMetadata().getTotalItems() > 0) {

            Map<String, Object>[] data = ops[0].getData();
            for (Map<String, Object> d : data) {
                UserEntity user = userMapper.fromDto(d);
                following.add(user);
            }
        }
        return following;
    }


    @Override public List<UserEntity> getFollowers(Long idUserFollowed, Long lastModifiedDate) throws IOException {
        List<UserEntity> followers = new ArrayList<>();
        GenericDto requestDto = userDtoFactory.getFollowersOperationDto(idUserFollowed, 0L, lastModifiedDate, false);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
        }else if (ops[0].getMetadata().getTotalItems() > 0) {
            Map<String, Object>[] data = ops[0].getData();
            for(Map<String,Object> d:data){
                UserEntity user = userMapper.fromDto(d);
                if(user.getCsysDeleted()==null){
                    followers.add(user);
                }
            }
        }
        return followers;
    }

    @Override
    public ShotEntity getShotById(Long idShot) throws IOException {
        GenericDto requestDto = shotDtoFactory.getSingleShotOperationDto(idShot);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
        }else if(ops[0].getMetadata().getTotalItems() > 0) {
            Map<String, Object> data = ops[0].getData()[0];
            return shotMapper.fromDto(data);
        }
        return null;
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
        }else if(ops[0].getMetadata().getTotalItems() > 0) {
            Map<String, Object>[] data = ops[0].getData();
            for (Map<String, Object> aData : data) {
                ShotEntity shot = shotMapper.fromDto(aData);
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
        }else if(ops[0].getMetadata().getTotalItems() > 0) {
            Map<String, Object>[] data = ops[0].getData();
            for (Map<String, Object> aData : data) {
                ShotEntity shot = shotMapper.fromDto(aData);
                olderShots.add(shot);
            }
        }
        return olderShots;
    }

    @Override
    public List<ShotEntity> getShotsByUserIdList(List<Long> followingUserIds, Long lastModifiedDate) throws IOException {
        List<ShotEntity> shots = new ArrayList<>();
        GenericDto genericDto = timelineDtoFactory.getAllShotsOperationDto(followingUserIds, DEFAULT_LIMIT);
        GenericDto responseDto = postRequest(genericDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
        }else if (ops[0].getMetadata().getTotalItems() > 0) {
            Map<String, Object>[] data = ops[0].getData();
            for (Map<String, Object> aData : data) {
                ShotEntity shot = shotMapper.fromDto(aData);
                shots.add(shot);
            }
        }
        return shots;
    }

    @Override
    public ShotEntity postNewShot(Long idUser, String comment) throws IOException {
        return postNewShotWithImage(idUser, comment, null);
    }

    @Override
    public ShotEntity postNewShotWithImage(Long idUser, String comment, String imageUrl) throws IOException {
        GenericDto requestDto = shotDtoFactory.getNewShotOperationDto(idUser, comment, imageUrl);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
        }else if (ops[0].getMetadata().getTotalItems() > 0) {
            Map<String, Object> dataItem = ops[0].getData()[0];
            return shotMapper.fromDto(dataItem);
        }
        return null;
    }

    @Override
    public UserEntity getUserByIdUser(Long idUser) throws IOException {
        GenericDto requestDto = userDtoFactory.getUserByUserId(idUser);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
        }else if (ops[0].getMetadata().getTotalItems() > 0) {
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
            Long items = metadata.getItems();
            for (int i = 0; i < items; i++) {
                Map<String, Object> dataItem = ops[0].getData()[i];
                users.add(userMapper.fromDto(dataItem));
            }
            int totalItems = metadata.getTotalItems().intValue();
            return new PaginatedResult<>(users).setPageLimit(SEARCH_PAGE_LIMIT)
              .setPageOffset(pageOffset)
              .setTotalItems(totalItems);
        }
        return null;
    }

    @Override public FollowEntity getFollowByIdUserFollowed(Long idCurrentUser,Long idUser) throws IOException {
        GenericDto requestDto = userDtoFactory.getFollowUserDtoByIdUser(idCurrentUser, idUser);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
            return null;
        }
        Map<String,Object> dataItem = ops[0].getData()[0];
        FollowEntity followReceived = followMapper.fromDto(dataItem);
        return followReceived;
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
        Map<String,Object> dataItem = ops[0].getData()[0];
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
        Map<String,Object> dataItem = ops[0].getData()[0];
        FollowEntity followReceived = followMapper.fromDto(dataItem);
        return followReceived;
    }

    @Override public MatchEntity getNextMatchWhereMyFavoriteTeamPlays(Long idFavoriteTeam) throws IOException {
        GenericDto requestDto = matchDtoFactory.getLastMatchWhereMyFavoriteTeamPlays(idFavoriteTeam);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if(ops == null || ops.length<1){
            Timber.e("Received 0 operations");
            return null;
        }
        Map<String,Object> dataItem  = ops[0].getData()[0];
        MatchEntity matchReceived = matchMapper.fromDto(dataItem);
        if (matchReceived.getIdMatch() != null) {
            return matchReceived;
        }
        return null;
    }


    @Override public List<WatchEntity> getWatchesFromUsers(List<Long> usersIds, Long idUser) throws IOException {
        List<WatchEntity> watchesReceived = new ArrayList<>();
        GenericDto requestDto = matchDtoFactory.getWatchFromUsers(usersIds, idUser);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if(ops == null || ops.length<1){
            Timber.e("Received 0 operations");
        }else{
            MetadataDto metadata = ops[0].getMetadata();
            Long items = metadata.getItems();
            for (int i = 0; i < items; i++) {
                Map<String, Object> dataItem = ops[0].getData()[i];
                watchesReceived.add(watchMapper.fromDto(dataItem));
            }
        }
        return watchesReceived;
    }

    @Override public List<MatchEntity> getMatchesByIds(List<Long> matchIds) throws IOException {
        List<MatchEntity> matchesReceived = new ArrayList<>();
        GenericDto requestDto = matchDtoFactory.getMatchesNotEndedByIds(matchIds);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if(ops == null || ops.length<1){
            Timber.e("Received 0 operations");
        }else{
            MetadataDto metadata = ops[0].getMetadata();
            Long items = metadata.getItems();
            for (int i = 0; i < items; i++) {
                Map<String, Object> dataItem = ops[0].getData()[i];
                matchesReceived.add(matchMapper.fromDto(dataItem));
            }
        }
        return matchesReceived;
    }

    @Override public WatchEntity setWatchStatus(WatchEntity watch) throws IOException {
        GenericDto requestDto = matchDtoFactory.setWatchStatusGenericDto(watch);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if(ops == null || ops.length<1){
            Timber.e("Received 0 operations");
            return null;
        }
        WatchEntity watchReceived = null;
        if(ops.length>0){
                watchReceived = watchMapper.fromDto(ops[0].getData()[0]);
        }
        return watchReceived;
    }

    @Override public WatchEntity getWatchStatus(Long idUser, Long idMatch) throws IOException {
        GenericDto requestDto = matchDtoFactory.getWatchByKeys(idUser, idMatch);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if(ops == null || ops.length<1){
            Timber.e("Received 0 operations");
            return null;
        }
        WatchEntity watchReceived = null;
        if(ops.length>0){
            watchReceived = watchMapper.fromDto(ops[0].getData()[0]);
        }
        return watchReceived;
    }

    @Override public MatchEntity getMatchByIdMatch(Long idMatch) throws IOException {
        GenericDto requestDto = matchDtoFactory.getMatchByIdMatch(idMatch);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if(ops == null || ops.length<1){
            Timber.e("Received 0 operations");
            return null;
        }
        MatchEntity matchReceived = null;
        if(ops.length>0){
            matchReceived = matchMapper.fromDto(ops[0].getData()[0]);
        }
        return matchReceived;

    }

    @Override public List<TeamEntity> getTeamsByIdTeams(List<Long> teamIds) throws IOException{
        GenericDto requestDto = matchDtoFactory.getTeamsFromTeamIds(teamIds);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        List<TeamEntity> teamEntities = new ArrayList<>();
        if(ops == null || ops.length<1){
            Timber.e("Received 0 operations");
        }else{
            MetadataDto md = ops[0].getMetadata();
            Long items = md.getItems();
            for (int i = 0; i < items; i++) {
                Map<String, Object> dataItem = ops[0].getData()[i];
                teamEntities.add(teamMapper.fromDto(dataItem));
            }
        }
        return teamEntities;
    }

    @Override public List<ShotEntity> getLatestsShotsFromIdUser(Long idUser, Long latestShotNumber) throws IOException {
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
                Map<String,Object> dataItem = ops[0].getData()[i];
                shotEntities.add(shotMapper.fromDto(dataItem));
            }
        }
        return shotEntities;
    }

    @Override public List<MatchEntity> searchMatches(String queryText) throws IOException {
        List<MatchEntity> matchesFound = new ArrayList<>();
        GenericDto requestDto = matchDtoFactory.searchMatches(queryText);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if(ops == null || ops.length<1){
            Timber.e("Received 0 operations");
        }else{
            MetadataDto metadata = ops[0].getMetadata();
            Long items = metadata.getItems();
            for (int i = 0; i < items; i++) {
                Map<String, Object> dataItem = ops[0].getData()[i];
                matchesFound.add(matchMapper.fromDto(dataItem));
            }
        }
        return matchesFound;
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

    @Override public List<TeamEntity> searchTeams(String queryText) throws IOException {
        List<TeamEntity> teamsFound = new ArrayList<>();
        GenericDto requestDto = teamDtoFactory.searchTeamsOperation(queryText);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if(ops == null || ops.length<1){
            Timber.e("Received 0 operations");
        }else{
            MetadataDto metadata = ops[0].getMetadata();
            Long items = metadata.getItems();
            for (int i = 0; i < items; i++) {
                Map<String, Object> dataItem = ops[0].getData()[i];
                teamsFound.add(teamMapper.fromDto(dataItem));
            }
        }
        return teamsFound;
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

                boolean isHookException = ServerException.G025.equals(statusCode);
                if (isHookException && statusSubcode != null) {
                    ShootrError shootrError = new ShootrDataServiceError(statusSubcode, statusMessage);
                    ShootrServerException hookException = new ShootrServerException (shootrError);
                    throw hookException;
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
