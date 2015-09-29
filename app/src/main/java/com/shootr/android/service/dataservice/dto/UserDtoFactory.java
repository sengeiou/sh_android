package com.shootr.android.service.dataservice.dto;

import android.support.v4.util.ArrayMap;
import com.shootr.android.constant.Constants;
import com.shootr.android.constant.ServiceConstants;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.data.entity.UserEntity;
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

    public static final int FOLLOW_TYPE = 0;
    public static final int UNFOLLOW_TYPE = 1;

    private static final String ENTITY_SUGGESTED_PEOPLE = "SuggestedPeopleMongo";
    private static final String ALIAS_SUGGESTED_PEOPLE = "SUGGESTED_PEOPLE";
    private static final String ALIAS_FOLLOW_USER = "FOLLOW_USER";
    private static final String ALIAS_UNFOLLOW_USER = "UNFOLLOW_USER";
    private static final String ALIAS_GETUSERBYID = "GET_USERBYID";
    private static final String ALIAS_GETUSERBYUSERNAME = "GET_USERBYUSERNAME";
    private static final String ALIAS_SEARCH_USERS = " ALIAS_FIND_FRIENDS";
    private static final String ALIAS_UPDATE_PROFILE = "CREATE_USER";
    public static final String SUGGESTED_PEOPLE_ID_USER = "idUser";

    private UtilityDtoFactory utilityDtoFactory;
    UserMapper userMapper;
    FollowMapper followMapper;

    @Inject public UserDtoFactory(UtilityDtoFactory utilityDtoFactory, UserMapper userMapper, FollowMapper followMapper) {
        this.utilityDtoFactory = utilityDtoFactory;
        this.userMapper = userMapper;
        this.followMapper = followMapper;
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