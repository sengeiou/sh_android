package com.shootr.android.service.dataservice.dto;

import android.support.v4.util.ArrayMap;
import com.shootr.android.constant.Constants;
import com.shootr.android.constant.ServiceConstants;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.entity.UserCreateAccountEntity;
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
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

import static com.shootr.android.service.dataservice.generic.FilterBuilder.and;
import static com.shootr.android.service.dataservice.generic.FilterBuilder.or;
import static com.shootr.android.service.dataservice.generic.FilterBuilder.orModifiedOrDeletedAfter;

public class UserDtoFactory {

    public static final long MAX_FOLLOWS_ITEMS = 100L;
    static final int NUMBER_OF_DAYS_AGO = 7;

    public static final Integer GET_FOLLOWERS = 0;
    public static final Integer GET_FOLLOWING = 1;

    public static final int FOLLOW_TYPE = 0;
    public static final int UNFOLLOW_TYPE = 1;

    private static final String ENTITY_LOGIN = "LoginMongo";
    private static final String ENTITY_CHECKIN = "CheckInMongo";
    private static final String ENTITY_CHECKOUT = "CheckOutMongo";
    private static final String ALIAS_LOGIN = "Login";
    private static final String ALIAS_CHECKIN = "CHECKIN";
    private static final String ALIAS_CHECKOUT = "CHECKOUT";
    private static final String ALIAS_GET_FOLLOWING = "GET_FOLLOWING";
    private static final String ALIAS_GET_FOLLOWERS = "GET_FOLLOWERS";
    private static final String ALIAS_FOLLOW_USER = "FOLLOW_USER";
    private static final String ALIAS_UNFOLLOW_USER = "UNFOLLOW_USER";
    private static final String ALIAS_GETUSERBYID = "GET_USERBYID";
    private static final String ALIAS_GETUSERS = "GET_USERS";
    private static final String ALIAS_SEARCH_USERS = " ALIAS_FIND_FRIENDS";
    private static final String ALIAS_UPDATE_PROFILE = "CREATE_USER";
    private static final String USER_SIGN_IN = "UserSignInMongo";
    private static final String ALIAS_USER_SIGN_IN = "USERSIGNIN";

    private UtilityDtoFactory utilityDtoFactory;
    UserMapper userMapper;
    FollowMapper followMapper;

    public static final String ID_USER_FOLLOWING = "idUserFollowing";
    public static final String ID_USER_WHO_IS_FOLLOWED = "idUserFollowed";

    @Inject public UserDtoFactory(UtilityDtoFactory utilityDtoFactory, UserMapper userMapper, FollowMapper followMapper) {
        this.utilityDtoFactory = utilityDtoFactory;
        this.userMapper = userMapper;
        this.followMapper = followMapper;
    }

    public GenericDto getCheckinOperationDto(String idUser, String idEvent) {
        MetadataDto metadataDto = new MetadataDto.Builder().entity(ENTITY_CHECKIN)
          .putKey(DatabaseContract.CheckInMongo.ID, idUser)
          .putKey(DatabaseContract.CheckInMongo.ID_CHECKED_EVENT, idEvent)
          .operation(ServiceConstants.OPERATION_RETRIEVE)
          .build();

        OperationDto operationDto = new OperationDto.Builder().metadata(metadataDto).setData(null).build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_CHECKIN, operationDto);
    }

    public GenericDto getLoginOperationDto(String id, String password) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null");
        }
        if (password == null) {
            throw new IllegalArgumentException("Password must not be null");
        }
        Map<String, Object> keys = new ArrayMap<>(2);
        keys.put(id.contains("@") ? UserTable.EMAIL : UserTable.USER_NAME, id);
        keys.put(UserTable.PASSWORD, password);

        //TODO: Metadata Builder
        MetadataDto md = new MetadataDto(
                ServiceConstants.OPERATION_RETRIEVE,
                ENTITY_LOGIN,
                false, 1L, 0L, 1L, keys
        );

        OperationDto op = new OperationDto();
        op.setMetadata(md);

        Map<String, Object>[] data = new HashMap[1];
        data[0] = userMapper.toDto(null);
        op.setData(data);

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_LOGIN, op);
    }


    public GenericDto followUserDto(FollowEntity follow){
        if(follow.getIdUser() == null){
            throw new IllegalArgumentException("IdUser who follow to, can't be null");
        }
        if(follow.getFollowedUser() == null){
            throw new IllegalArgumentException("IdUser who is followed by, can't be null");
        }
        Map<String,Object> keys = new ArrayMap<>();
        MetadataDto md = new MetadataDto(ServiceConstants.OPERATION_CREATE, FollowTable.TABLE,true,1L,0L,1L,keys);
        OperationDto op = new OperationDto();
        op.setMetadata(md);

        Map<String, Object>[] data = new HashMap[1];
        data[0] = followMapper.toDto(follow);
        op.setData(data);
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_FOLLOW_USER, op);
    }

    public GenericDto unfollowUserDto(FollowEntity follow){
        if(follow.getIdUser() == null){
            throw new IllegalArgumentException("IdUser who follow to, can't be null");
        }
        if(follow.getFollowedUser() == null){
            throw new IllegalArgumentException("IdUser who is followed by, can't be null");
        }
        Map<String, Object> keys = new ArrayMap<>();
        keys.put(FollowTable.ID_USER, follow.getIdUser());
        keys.put(FollowTable.ID_FOLLOWED_USER,follow.getFollowedUser());
        MetadataDto md = new MetadataDto(ServiceConstants.OPERATION_DELETE, FollowTable.TABLE,true,1L,0L,1L,keys);
        OperationDto op = new OperationDto();
        op.setMetadata(md);

        Map<String, Object>[] data = new HashMap[1];
        data[0] = followMapper.toDto(follow);
        op.setData(data);
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_UNFOLLOW_USER, op);
    }

    public GenericDto getFollowUserDtoByIdUser(String idFromUser, String idToUser){
        if(idFromUser == null){
            throw new IllegalArgumentException("IdUser who follow to, can't be null");
        }
        if(idToUser == null){
            throw new IllegalArgumentException("IdUser who is followed by, can't be null");
        }
        Map<String, Object> keys = new ArrayMap<>(2);
        keys.put(FollowTable.ID_USER, idFromUser);
        keys.put(FollowTable.ID_FOLLOWED_USER, idToUser);

        MetadataDto md = new MetadataDto(ServiceConstants.OPERATION_RETRIEVE, FollowTable.TABLE,true,1L,0L,1L,keys);
        OperationDto op = new OperationDto();
        op.setMetadata(md);

        Map<String, Object>[] data = new HashMap[1];
        data[0] = followMapper.toDto(null);
        op.setData(data);
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_FOLLOW_USER, op);
    }

    public GenericDto getFollowingsOperationDto(String fromUserId, Long offset,Long date, boolean includeDeleted) {
        FilterDto filter = and(FollowTable.ID_USER).isEqualTo(fromUserId) //
          .and(FollowTable.ID_FOLLOWED_USER).isNotEqualTo(null) //
          .build();

        MetadataDto metadata = new MetadataDto.Builder() //
          .operation(Constants.OPERATION_RETRIEVE) //
          .entity(FollowTable.TABLE) //
          .includeDeleted(false) //
          .filter(filter) //
          .items(MAX_FOLLOWS_ITEMS) //
          .build();

        OperationDto operation = new OperationDto.Builder() //
          .metadata(metadata) //
          .putData(followMapper.toDto(null)) //
          .build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_FOLLOWING, operation);
    }

    public GenericDto getFollowersOperationDto(String fromUserId, Long offset,Long date, boolean includeDeleted) {
        FilterDto filter = and(FollowTable.ID_FOLLOWED_USER).isEqualTo(fromUserId) //
          .and(FollowTable.ID_USER).isNotEqualTo(null) //
          .build();

        MetadataDto metadata = new MetadataDto.Builder() //
          .operation(Constants.OPERATION_RETRIEVE) //
          .entity(FollowTable.TABLE) //
          .includeDeleted(false) //
          .filter(filter) //
          .items(MAX_FOLLOWS_ITEMS) //
          .build();

        OperationDto operation = new OperationDto.Builder() //
          .metadata(metadata) //
          .putData(followMapper.toDto(null)) //
          .build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_FOLLOWERS, operation);
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

    public GenericDto getUsersOperationDto(List<String> userIds) {
        FilterDto filter = and( //
          orModifiedOrDeletedAfter(0L), //
          or(UserTable.ID).isIn(userIds) //
        ).build();

        MetadataDto metadata = new MetadataDto.Builder().operation(Constants.OPERATION_RETRIEVE)
          .entity(UserTable.TABLE)
          .filter(filter)
          .items(100)
          .totalItems(100)
          .includeDeleted(false)
          .build();

        OperationDto operationDto =
          new OperationDto.Builder().metadata(metadata).putData(userMapper.reqRestUsersToDto(null)).build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GETUSERS, operationDto);
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

    public GenericDto getCreateAccountOperationDto(UserCreateAccountEntity userCreateAccountEntity) {
        MetadataDto metadataDto = new MetadataDto.Builder().entity(USER_SIGN_IN)
          .putKey(UserTable.ID, null)
          .operation(ServiceConstants.OPERATION_CREATE)
          .build();

        Map<String, Object> userCreateAccountEntityMap = userMapper.toCreateAccountDto(userCreateAccountEntity);
        OperationDto operationDto = new OperationDto.Builder().metadata(metadataDto).putData(userCreateAccountEntityMap).build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_USER_SIGN_IN, operationDto);
    }

    public GenericDto getCheckoutOperationDto(String idUser, String checkedEventID) {
        MetadataDto metadataDto = new MetadataDto.Builder().entity(ENTITY_CHECKOUT)
                .putKey(DatabaseContract.CheckOutMongo.ID, idUser)
                .putKey(DatabaseContract.CheckOutMongo.ID_CHECKED_EVENT, checkedEventID)
                .operation(ServiceConstants.OPERATION_RETRIEVE)
                .build();

        OperationDto operationDto = new OperationDto.Builder().metadata(metadataDto).setData(null).build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_CHECKOUT, operationDto);
    }
}