package gm.mobi.android.service.dataservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import gm.mobi.android.db.mappers.FollowMapper;
import gm.mobi.android.db.mappers.ShotMapper;
import gm.mobi.android.db.mappers.TeamMapper;
import gm.mobi.android.db.mappers.UserMapper;
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.db.objects.Team;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.exception.ServerException;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.service.Endpoint;
import gm.mobi.android.service.dataservice.dto.ShotDtoFactory;
import gm.mobi.android.service.dataservice.dto.TimelineDtoFactory;
import gm.mobi.android.service.dataservice.dto.UserDtoFactory;
import gm.mobi.android.service.dataservice.generic.GenericDto;
import gm.mobi.android.service.dataservice.generic.OperationDto;
import gm.mobi.android.util.SecurityUtils;
import timber.log.Timber;

public class BagdadDataService implements BagdadService {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final long DEFAULT_LIMIT = 10L;

    private OkHttpClient client;
    private Endpoint endpoint;
    private ObjectMapper mapper;
    private UserDtoFactory userDtoFactory;

    private TimelineDtoFactory timelineDtoFactory;
    private ShotDtoFactory shotDtoFactory;
    private UserMapper userMapper;
    private FollowMapper followMapper;
    private ShotMapper shotMapper;
    private TeamMapper teamMapper;

    @Inject
    public BagdadDataService(OkHttpClient client, Endpoint endpoint, ObjectMapper mapper, UserDtoFactory userDtoFactory,
      TimelineDtoFactory timelineDtoFactory, ShotDtoFactory shotDtoFactory, UserMapper userMapper,
      FollowMapper followMapper, ShotMapper shotMapper, TeamMapper teamMapper) {
        this.client = client;
        this.endpoint = endpoint;
        this.mapper = mapper;
        this.userDtoFactory = userDtoFactory;
        this.timelineDtoFactory = timelineDtoFactory;
        this.shotDtoFactory = shotDtoFactory;
        this.userMapper = userMapper;
        this.followMapper = followMapper;
        this.shotMapper = shotMapper;
        this.teamMapper = teamMapper;
    }

    @Override
    public User login(String id, String password) throws IOException {
        GenericDto loginDto = userDtoFactory.getLoginOperationDto(id, SecurityUtils.encodePassword(password));
        GenericDto responseDto = postRequest(loginDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
            return null;
        }
        Map<String, Object>[] data = ops[0].getData();
        return userMapper.fromDto(data[0]);
    }

    @Override
    public List<Follow> getFollows(Long idUser, Long lastModifiedDate, int typeFollow, boolean includeDeleted)
      throws IOException {
        List<Follow> follows = new ArrayList<>();
        GenericDto requestDto =
          userDtoFactory.getFollowOperationDto(idUser, 0L, typeFollow, lastModifiedDate, includeDeleted);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
            return null;
        }

        if (ops.length > 0 && ops[0].getMetadata().getTotalItems() > 0) {
            Map<String, Object>[] data = ops[0].getData();
            for (int i = 0; i < data.length; i++) {
                Follow f = followMapper.fromDto(data[i]);
                follows.add(f);
            }
        }
        return follows;
    }

    @Override
    public List<User> getUsersByUserIdList(List<Long> userIds) throws IOException {
        List<User> users = new ArrayList<>();
        GenericDto genericDto = userDtoFactory.getUsersOperationDto(userIds, 1000L, 0L);
        GenericDto responseDto = postRequest(genericDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
            return null;
        }
        if (ops.length > 0 && ops[0].getMetadata().getTotalItems() > 0) {
            Map<String, Object>[] data = ops[0].getData();
            for (int i = 0; i < data.length; i++) {
                User user = userMapper.fromDto(data[i]);
                users.add(user);
            }
        }
        return users;
    }

    @Override
    public List<Shot> getNewShots(List<Long> followingUserIds, Long newestShotDate) throws IOException {
        List<Shot> newerShots = new ArrayList<>();
        GenericDto genericDto =
          timelineDtoFactory.getNewerShotsOperationDto(followingUserIds, newestShotDate, DEFAULT_LIMIT);
        GenericDto responseDto = postRequest(genericDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
            return null;
        }
        if (ops.length > 0 && ops[0].getMetadata().getTotalItems() > 0) {
            Map<String, Object>[] data = ops[0].getData();
            for (int i = 0; i < data.length; i++) {
                Shot shot = shotMapper.fromDto(data[i]);
                newerShots.add(shot);
            }
        }
        return newerShots;
    }

    @Override
    public List<Shot> getOlderShots(List<Long> followingUserIds, Long lastModifiedDate) throws IOException {
        List<Shot> olderShots = new ArrayList<>();
        GenericDto genericDto =
          timelineDtoFactory.getOlderShotsOperationDto(followingUserIds, lastModifiedDate, DEFAULT_LIMIT);
        GenericDto responseDto = postRequest(genericDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
            return null;
        }
        if (ops.length > 0 && ops[0].getMetadata().getTotalItems() > 0) {
            Map<String, Object>[] data = ops[0].getData();
            for (int i = 0; i < data.length; i++) {
                Shot shot = shotMapper.fromDto(data[i]);
                olderShots.add(shot);
            }
        }
        return olderShots;
    }

    @Override
    public List<Shot> getShotsByUserIdList(List<Long> followingUserIds, Long lastModifiedDate) throws IOException {
        List<Shot> shots = new ArrayList<>();
        GenericDto genericDto = timelineDtoFactory.getAllShotsOperationDto(followingUserIds, DEFAULT_LIMIT);
        GenericDto responseDto = postRequest(genericDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
            return null;
        }
        if (ops.length > 0 && ops[0].getMetadata().getTotalItems() > 0) {
            Map<String, Object>[] data = ops[0].getData();
            for (int i = 0; i < data.length; i++) {
                Shot shot = shotMapper.fromDto(data[i]);
                shots.add(shot);
            }
        }
        return shots;
    }

    @Override
    public Shot postNewShot(Long idUser, String comment) throws IOException {
        GenericDto requestDto = shotDtoFactory.getNewShotOperationDto(idUser, comment);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
            return null;
        }
        if (ops.length > 0 && ops[0].getMetadata().getTotalItems() > 0) {
            Map<String, Object> dataItem = ops[0].getData()[0];
            return shotMapper.fromDto(dataItem);
        } else {
            return null;
        }
    }

    @Override
    public User getUserByIdUser(Long idUser) throws IOException {
        GenericDto requestDto = userDtoFactory.getUserByUserId(idUser);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
            return null;
        }
        if (ops.length > 0 && ops[0].getMetadata().getTotalItems() > 0) {
            Map<String, Object> dataItem = ops[0].getData()[0];
            return userMapper.fromDto(dataItem);
        } else {
            return null;
        }
    }

    @Override
    public Team getTeamByIdTeam(Long idTeam) throws IOException {
        GenericDto requestDto = userDtoFactory.getTeamByTeamId(idTeam);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operation");
            return null;
        }
        if (ops.length > 0 && ops[0].getMetadata().getTotalItems() > 0) {
            Map<String, Object> dataItem = ops[0].getData()[0];
            return teamMapper.fromDto(dataItem);
        } else {
            return null;
        }
    }

    @Override
    public List<Team> getTeamsByIdTeams(Set<Long> teamIds, Long lastModifiedDate) throws IOException {
        List<Team> teams = new ArrayList<>();
        GenericDto requestDto = userDtoFactory.getTeamsByTeamIds(teamIds);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operation");
            return null;
        }

        if (ops.length > 0 && ops[0].getMetadata().getTotalItems() > 0) {
            for (int i = 0; i < ops[0].getMetadata().getTotalItems(); i++) {
                Map<String, Object> dataItem = ops[0].getData()[i];
                teams.add(teamMapper.fromDto(dataItem));
            }
        } else {
            return null;
        }
        return teams;
    }

    public List<User> searchUsersByNameOrNickName(String searchString) throws IOException{
        List<User> users = new ArrayList<>();
        GenericDto requestDto = userDtoFactory.searchUserOperation(searchString);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
            return null;
        }
        Long totalItems = ops[0].getMetadata().getTotalItems();
        if (ops.length > 0 && totalItems > 0) {
            for (int i = 0; i < totalItems; i++) {
                Map<String, Object> dataItem = ops[0].getData()[i];
                users.add(userMapper.fromDto(dataItem));
            }
        } else {
            return null;
        }
        return users;
    }

    @Override
    public Follow getFollowRelationship(Long idUser, Long idCurrentUser, int typeFollow) throws IOException {
        GenericDto requestDto =
          userDtoFactory.getFollowOperationForGettingRelationship(idUser, idCurrentUser, typeFollow);
        GenericDto responseDto = postRequest(requestDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
            return null;
        }
        Long totalItems = ops[0].getMetadata().getTotalItems();
        if (ops.length > 0 && totalItems > 0) {
            Map<String, Object> dataItem = ops[0].getData()[0];
            return followMapper.fromDto(dataItem);
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
        if (response.isSuccessful()) {
            String responseBody = response.body().string();
            Timber.d("Response received: %s", responseBody);
            GenericDto genericDto = mapper.readValue(responseBody, GenericDto.class);
            // Check for Data-Service errors
            String statusCode = genericDto.getStatusCode();
            String statusMessage = genericDto.getStatusMessage();
            if (statusCode == null || !statusCode.equals(ServerException.OK)) {
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
}


