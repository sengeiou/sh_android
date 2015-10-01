package com.shootr.android.service.dataservice.dto;

import android.support.v4.util.ArrayMap;
import com.shootr.android.constant.ServiceConstants;
import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.db.DatabaseContract.FollowTable;
import com.shootr.android.db.mappers.FollowMapper;
import com.shootr.android.db.mappers.UserMapper;
import com.shootr.android.service.dataservice.generic.GenericDto;
import com.shootr.android.service.dataservice.generic.MetadataDto;
import com.shootr.android.service.dataservice.generic.OperationDto;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

public class UserDtoFactory {

    public static final long MAX_FOLLOWS_ITEMS = 100L;

    public static final Integer GET_FOLLOWERS = 0;
    public static final Integer GET_FOLLOWING = 1;

    public static final int FOLLOW_TYPE = 0;
    public static final int UNFOLLOW_TYPE = 1;

    private static final String ALIAS_FOLLOW_USER = "FOLLOW_USER";
    private static final String ALIAS_UNFOLLOW_USER = "UNFOLLOW_USER";
    private static final String ALIAS_SEARCH_USERS = " ALIAS_FIND_FRIENDS";
    private static final String ALIAS_UPDATE_PROFILE = "CREATE_USER";

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

}