package com.shootr.android.service.dataservice.dto;

import android.support.v4.util.ArrayMap;
import com.shootr.android.constant.Constants;
import com.shootr.android.constant.ServiceConstants;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.db.DatabaseContract;
import com.shootr.android.db.DatabaseContract.FollowTable;
import com.shootr.android.db.DatabaseContract.UserTable;
import com.shootr.android.db.mappers.FollowMapper;
import com.shootr.android.db.mappers.UserMapper;
import com.shootr.android.service.dataservice.generic.FilterDto;
import com.shootr.android.service.dataservice.generic.GenericDto;
import com.shootr.android.service.dataservice.generic.MetadataDto;
import com.shootr.android.service.dataservice.generic.OperationDto;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

import static com.shootr.android.service.dataservice.generic.FilterBuilder.and;
import static com.shootr.android.service.dataservice.generic.FilterBuilder.or;
import static com.shootr.android.service.dataservice.generic.FilterBuilder.orModifiedOrDeletedAfter;

public class UserDtoFactory {

    public static final long MAX_FOLLOWS_ITEMS = 100L;

    public static final Integer GET_FOLLOWERS = 0;
    public static final Integer GET_FOLLOWING = 1;

    private static final String ENTITY_LOGOUT = "LogoutMongo";
    private static final String ALIAS_LOGOUT = "Logout";
    private static final String ENTITY_CHECKIN = "CheckInMongo";
    private static final String ENTITY_SUGGESTED_PEOPLE = "SuggestedPeopleMongo";
    private static final String ALIAS_CHECKIN = "CHECKIN";
    private static final String ALIAS_SUGGESTED_PEOPLE = "SUGGESTED_PEOPLE";
    private static final String ALIAS_GETUSERBYID = "GET_USERBYID";
    private static final String ALIAS_GETUSERBYUSERNAME = "GET_USERBYUSERNAME";
    private static final String ALIAS_SEARCH_USERS = " ALIAS_FIND_FRIENDS";
    private static final String ALIAS_UPDATE_PROFILE = "CREATE_USER";
    public static final String CHECK_IN_ID_USER = "idUser";
    public static final String SUGGESTED_PEOPLE_ID_USER = "idUser";
    public static final String CHECK_IN_ID_STREAM_CHECKED = "idStream";

    private UtilityDtoFactory utilityDtoFactory;
    UserMapper userMapper;
    FollowMapper followMapper;

    @Inject public UserDtoFactory(UtilityDtoFactory utilityDtoFactory, UserMapper userMapper, FollowMapper followMapper) {
        this.utilityDtoFactory = utilityDtoFactory;
        this.userMapper = userMapper;
        this.followMapper = followMapper;
    }

    public GenericDto getCheckinOperationDto(String idUser, String idStream) {
        MetadataDto metadataDto = new MetadataDto.Builder().entity(ENTITY_CHECKIN)
          .putKey(CHECK_IN_ID_USER, idUser)
          .putKey(CHECK_IN_ID_STREAM_CHECKED, idStream)
          .operation(ServiceConstants.OPERATION_RETRIEVE)
          .build();

        OperationDto operationDto = new OperationDto.Builder().metadata(metadataDto).setData(null).build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_CHECKIN, operationDto);
    }

    public GenericDto getUserByUsername(String username){
        FilterDto filter = and(UserTable.USER_NAME).completlyContains(username) //
                .and(UserTable.DELETED).isEqualTo(null) //
                .build();

        MetadataDto metadata = new MetadataDto.Builder() //
                .operation(Constants.OPERATION_RETRIEVE) //
                .entity(UserTable.TABLE) //
                .includeDeleted(false) //
                .filter(filter) //
                .build();

        OperationDto operation = new OperationDto.Builder() //
                .metadata(metadata) //
                .putData(userMapper.toDto(null)) //
                .build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GETUSERBYUSERNAME, operation);
    }

    public GenericDto getUserByUserId(String userId){
        OperationDto od  = new OperationDto();
        Map<String, Object> key = new HashMap<>();
        key.put(UserTable.ID,userId);
        MetadataDto md = new MetadataDto(Constants.OPERATION_RETRIEVE,UserTable.TABLE,true,null,0L,1L,key);
        od.setMetadata(md);
        Map<String, Object>[] array = new HashMap[1];
        array[0] = userMapper.reqRestUsersToDto(null);
        od.setData(array);
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GETUSERBYID, od);
    }

    public GenericDto searchUserOperation(String searchString, Integer pageLimit, Integer pageOffset) {
        FilterDto filter = and(
          orModifiedOrDeletedAfter(0L),
          or(UserTable.NAME).contains(searchString)
          .or(UserTable.USER_NAME)
          .contains(searchString)).build();

        MetadataDto md = new MetadataDto.Builder().operation(Constants.OPERATION_RETRIEVE)
          .entity(UserTable.TABLE)
          .items(pageLimit)
          .offset(pageOffset)
          .filter(filter)
          .build();

        OperationDto od = new OperationDto.Builder().metadata(md).putData(userMapper.reqRestUsersToDto(null)).build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_SEARCH_USERS, od);
    }

    public GenericDto saveUserDto(UserEntity userEntity) {
        MetadataDto md = new MetadataDto.Builder()
          .operation(ServiceConstants.OPERATION_UPDATE)
          .entity(UserTable.TABLE)
          .putKey(UserTable.ID, userEntity.getIdUser())
          .build();

        OperationDto op = new OperationDto.Builder()
          .metadata(md)
          .putData(userMapper.reqRestUsersToDto(userEntity))
          .build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_UPDATE_PROFILE, op);
    }

    public GenericDto getLogoutOperationDto(String idUser, String idDevice) {
        if (idUser == null) {
            throw new IllegalArgumentException("IdUser must not be null");
        }
        if (idDevice == null) {
            throw new IllegalArgumentException("IdDevice must not be null");
        }
        Map<String, Object> keys = new ArrayMap<>(2);
        keys.put(DatabaseContract.DeviceTable.ID_USER, idUser);
        keys.put(DatabaseContract.DeviceTable.ID_DEVICE, idDevice);

        MetadataDto metadata = new MetadataDto.Builder() //
          .operation(Constants.OPERATION_RETRIEVE) //
          .entity(ENTITY_LOGOUT) //
          .includeDeleted(false) //
          .setKeys(keys)
          .build();

        Map<String, Object> dto = new HashMap<>();

        OperationDto operation = new OperationDto.Builder() //
          .metadata(metadata) //
          .putData(dto) //
          .build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_LOGOUT, operation);
    }

    public GenericDto getSuggestedPeople(String currentUserId) {
        MetadataDto metadataDto = new MetadataDto.Builder().entity(ENTITY_SUGGESTED_PEOPLE)
          .putKey(SUGGESTED_PEOPLE_ID_USER, currentUserId)
          .operation(ServiceConstants.OPERATION_RETRIEVE)
          .items(MAX_FOLLOWS_ITEMS)
          .build();

        OperationDto operationDto = new OperationDto.Builder().metadata(metadataDto).setData(null).build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_SUGGESTED_PEOPLE, operationDto);
    }
}