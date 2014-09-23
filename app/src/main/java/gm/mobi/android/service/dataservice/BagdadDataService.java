package gm.mobi.android.service.dataservice;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.manager.SyncTableManager;
import gm.mobi.android.db.mappers.FollowMapper;
import gm.mobi.android.db.mappers.ShotMapper;
import gm.mobi.android.db.mappers.UserMapper;
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.db.objects.Shot;
import gm.mobi.android.db.objects.User;
import gm.mobi.android.exception.ServerException;
import gm.mobi.android.service.BagdadService;
import gm.mobi.android.service.Endpoint;
import gm.mobi.android.service.dataservice.dto.TimeLineDtoFactory;
import gm.mobi.android.service.dataservice.dto.UserDtoFactory;
import gm.mobi.android.service.dataservice.generic.GenericDto;
import gm.mobi.android.service.dataservice.generic.OperationDto;
import gm.mobi.android.util.SecurityUtils;
import timber.log.Timber;

public class BagdadDataService implements BagdadService {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient client;
    private Endpoint endpoint;
    private ObjectMapper mapper;

    @Inject
    public BagdadDataService(OkHttpClient client, Endpoint endpoint, ObjectMapper mapper) {
        this.client = client;
        this.endpoint = endpoint;
        this.mapper = mapper;
    }

    @Override
    public User login(String id, String password) throws IOException {
        GenericDto loginDto = UserDtoFactory.getLoginOperationDto(id, SecurityUtils.encodePassword(password));
        GenericDto responseDto = postRequest(loginDto);
        OperationDto[] ops = responseDto.getOps();
        if (ops == null || ops.length < 1) {
            Timber.e("Received 0 operations");
            return null;
        }
        Map<String, Object>[] data = ops[0].getData();
        return UserMapper.fromDto(data[0]);
    }

    public List<Follow> getFollows(Integer idUser, Context context, SQLiteDatabase db, int typeFollow) throws IOException{
        List<Follow> follows = new ArrayList<>();
        Long date = SyncTableManager.getLastModifiedDate(context, db, GMContract.FollowTable.TABLE);
        GenericDto genericDto = UserDtoFactory.getFollowOperationDto(idUser, 1000L, context,typeFollow, date);
        GenericDto responseDto = postRequest(genericDto);
        OperationDto[] ops = responseDto.getOps();
        if(ops == null || ops.length<1){
            Timber.e("Received 0 operations");
            return null;
        }
        if(ops.length>0){
            Map<String, Object>[] data = ops[0].getData();
            for(int i=0;i<data.length;i++){
                Follow f = FollowMapper.fromDto(data[i]);
                follows.add(f);
            }
        }
        return follows;
    }

    @Override
    public List<User> getUsersByUserIdList(List<Integer> userIds, Context context, SQLiteDatabase db) throws IOException {
        List<User> users = new ArrayList<>();
        Long date = SyncTableManager.getLastModifiedDate(context,db, GMContract.UserTable.TABLE);
        GenericDto genericDto = UserDtoFactory.getUsersOperationDto(userIds,1000L,context,date);
        GenericDto responseDto = postRequest(genericDto);
        OperationDto[] ops = responseDto.getOps();
        if(ops == null || ops.length<1){
            Timber.e("Received 0 operations");
            return null;
        }
        if(ops.length>0){
            Map<String, Object>[] data = ops[0].getData();
            for(int i=0;i<data.length;i++){
                User user = UserMapper.fromDto(data[i]);
                users.add(user);
            }
        }
        return users;
    }

    @Override
    public List<Shot> getNewShots(List<Integer> followingUserIds, Context context, SQLiteDatabase db, Shot lastNewShot) throws IOException {
        List<Shot> newerShots = new ArrayList<>();
        Long date = SyncTableManager.getLastModifiedDate(context,db,GMContract.ShotTable.TABLE);
        GenericDto genericDto = TimeLineDtoFactory.getNewerShotsOperationDto(followingUserIds,date,lastNewShot,context);
        GenericDto responseDto = postRequest(genericDto);
        OperationDto[] ops = responseDto.getOps();
        if(ops == null || ops.length<1){
            Timber.e("Received 0 operations");
            return null;
        }else{
            Map<String,Object>[] data = ops[0].getData();
            for(int i = 0; i<data.length;i++){
                Shot shot = ShotMapper.fromDto(data[i]);
                newerShots.add(shot);
            }
        }
        return newerShots;
    }

    @Override
    public List<Shot> getOlderShots(List<Integer> followingUserIds, Context context, SQLiteDatabase db, Shot lastOlderShot) throws IOException {
        List<Shot> newerShots = new ArrayList<>();
        Long date = SyncTableManager.getLastModifiedDate(context,db,GMContract.ShotTable.TABLE);
        GenericDto genericDto = TimeLineDtoFactory.getOlderShotsOperationDto(followingUserIds,date,lastOlderShot,context);
        GenericDto responseDto = postRequest(genericDto);
        OperationDto[] ops = responseDto.getOps();
        if(ops == null || ops.length<1){
            Timber.e("Received 0 operations");
            return null;
        }else{
            Map<String,Object>[] data = ops[0].getData();
            for(int i = 0; i<data.length;i++){
                Shot shot = ShotMapper.fromDto(data[i]);
                newerShots.add(shot);
            }
        }
        return newerShots;
    }

    @Override
    public List<Shot> getShotsByUserIdList( List<Integer> followingUserIds, Context context, SQLiteDatabase db) throws IOException {
        List<Shot> shots = new ArrayList<>();
        Long date = SyncTableManager.getLastModifiedDate(context, db, GMContract.ShotTable.TABLE);
        GenericDto genericDto = TimeLineDtoFactory.getShotsOperationDto(followingUserIds, date, context);
        GenericDto responseDto = postRequest(genericDto);
        OperationDto[] ops = responseDto.getOps();
        if(ops == null || ops.length<1){
            Timber.e("Received 0 operations");
            return null;
        }else{
            Map<String, Object>[] data = ops[0].getData();
            for(int i=0;i<data.length;i++){
                Shot shot = ShotMapper.fromDto(data[i]);
                shots.add(shot);
            }
        }
        return shots;
    }

    private GenericDto postRequest(GenericDto dto) throws IOException {
        // Create the request
        String requestJson = mapper.writeValueAsString(dto);
        Timber.d("Executing request: %s", requestJson);
        RequestBody body = RequestBody.create(JSON, requestJson);
        Request request = new Request.Builder()
                .url(endpoint.getUrl())
                .post(body)
                .build();

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


