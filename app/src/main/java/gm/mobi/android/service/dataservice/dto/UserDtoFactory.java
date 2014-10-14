package gm.mobi.android.service.dataservice.dto;

import android.support.v4.util.ArrayMap;
import gm.mobi.android.constant.Constants;
import gm.mobi.android.constant.ServiceConstants;
import gm.mobi.android.db.GMContract.FollowTable;
import gm.mobi.android.db.GMContract.UserTable;
import gm.mobi.android.db.mappers.FollowMapper;
import gm.mobi.android.db.mappers.TeamMapper;
import gm.mobi.android.db.mappers.UserMapper;
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.service.dataservice.generic.FilterDto;
import gm.mobi.android.service.dataservice.generic.GenericDto;
import gm.mobi.android.service.dataservice.generic.MetadataDto;
import gm.mobi.android.service.dataservice.generic.OperationDto;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

import static gm.mobi.android.service.dataservice.generic.FilterBuilder.and;
import static gm.mobi.android.service.dataservice.generic.FilterBuilder.or;
import static gm.mobi.android.service.dataservice.generic.FilterBuilder.orModifiedOrDeletedAfter;

public class UserDtoFactory {

    static final int NUMBER_OF_DAYS_AGO = 7;

    public static final Integer GET_FOLLOWERS = 0;
    public static final Integer GET_FOLLOWING = 1;

    public static final int FOLLOW_TYPE = 0;
    public static final int UNFOLLOW_TYPE = 1;

    private static final String ENTITY_LOGIN = "Login";
    private static final String ALIAS_LOGIN = "Login";
    private static final String ALIAS_GET_FOLLOWING = "GET_FOLLOWING";
    private static final String ALIAS_GET_FOLLOWERS = "GET_FOLLOWERS";
    private static final String ALIAS_GET_USERS = "GET_USERS";
    private static final String ALIAS_FOLLOW_USER = "FOLLOW_USER";
    private static final String ALIAS_UNFOLLOW_USER = "UNFOLLOW_USER";
    private static final String ALIAS_GETUSERBYID = "GET_USERBYID";
    private static final String ALIAS_RETRIEVE_TEAM_BY_ID = "GET_TEAMBYID";
    private static final String ALIAS_RETRIEVE_TEAMS_BY_TEAMIDS = "GET_TEAMS_BY_TEAMSIDS";
    private static final String ALIAS_GETFOLLOWRELATIONSHIP = "GET_FOLLOWRELATIONSHIP";
    private static final String ALIAS_SEARCH_USERS = " ALIAS_FIND_FRIENDS";

    private UtilityDtoFactory utilityDtoFactory;
    UserMapper userMapper;
    TeamMapper teamMapper;
    FollowMapper followMapper;

    public static final String ID_USER_FOLLOWING = "idUserFollowing";
    public static final String ID_USER_WHO_IS_FOLLOWED = "idUserFollowed";

    @Inject public UserDtoFactory(UtilityDtoFactory utilityDtoFactory, UserMapper userMapper, TeamMapper teamMapper, FollowMapper followMapper) {
        this.utilityDtoFactory = utilityDtoFactory;
        this.userMapper = userMapper;
        this.teamMapper = teamMapper;
        this.followMapper = followMapper;
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


    public GenericDto followUserDto(Follow follow){
        if(follow.getIdUser() == null){
            throw new IllegalArgumentException("IdUser who follow to, can't be null");
        }
        if(follow.getFollowedUser() == null){
            throw new IllegalArgumentException("IdUser who is followed by, can't be null");
        }
        Map<String,Object> keys = new ArrayMap<>();
        MetadataDto md = new MetadataDto(ServiceConstants.OPERATION_CREATE, "Follow",true,1L,0L,1L,keys);
        OperationDto op = new OperationDto();
        op.setMetadata(md);

        Map<String,Object>[] data = new HashMap[1];
        data[0] = followMapper.toDto(follow);
        op.setData(data);
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_FOLLOW_USER, op);
    }

    public GenericDto unfollowUserDto(Follow follow){
        if(follow.getIdUser() == null){
            throw new IllegalArgumentException("IdUser who follow to, can't be null");
        }
        if(follow.getFollowedUser() == null){
            throw new IllegalArgumentException("IdUser who is followed by, can't be null");
        }
        Map<String,Object> keys = new ArrayMap<>();
        keys.put(FollowTable.ID_USER, follow.getIdUser());
        keys.put(FollowTable.ID_FOLLOWED_USER,follow.getFollowedUser());
        MetadataDto md = new MetadataDto(ServiceConstants.OPERATION_DELETE, "Follow",true,1L,0L,1L,keys);
        OperationDto op = new OperationDto();
        op.setMetadata(md);

        Map<String,Object>[] data = new HashMap[1];
        data[0] = followMapper.toDto(follow);
        op.setData(data);
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_UNFOLLOW_USER, op);
    }

    public GenericDto getFollowUserDtoByIdUser(Long idFromUser, Long idToUser){
        if(idFromUser == null){
            throw new IllegalArgumentException("IdUser who follow to, can't be null");
        }
        if(idToUser == null){
            throw new IllegalArgumentException("IdUser who is followed by, can't be null");
        }
        Map<String,Object> keys = new ArrayMap<>(2);
        keys.put(FollowTable.ID_USER, idFromUser);
        keys.put(FollowTable.ID_FOLLOWED_USER, idToUser);

        MetadataDto md = new MetadataDto(ServiceConstants.OPERATION_RETRIEVE, "Follow",true,1L,0L,1L,keys);
        OperationDto op = new OperationDto();
        op.setMetadata(md);

        Map<String,Object>[] data = new HashMap[1];
        data[0] = followMapper.toDto(null);
        op.setData(data);
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_FOLLOW_USER, op);
    }

    public GenericDto getFollowingsOperationDto(Long idUserFollowing, Long offset,Long date, boolean includeDeleted) {

        OperationDto od = new OperationDto();
        FilterDto filter = and(orModifiedOrDeletedAfter(date), or(ID_USER_FOLLOWING).isEqualTo(idUserFollowing)).build();

        MetadataDto md = new MetadataDto(Constants.OPERATION_RETRIEVE,"Following", includeDeleted, null, null, null, filter);
        od.setMetadata(md);

        Map<String, Object>[] array = new HashMap[1];
        array[0] = userMapper.reqRestUsersToDto(null);
        od.setData(array);


        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_FOLLOWING, od);
    }

    public GenericDto getFollowersOperationDto(Long idUserWhoFollow, Long offset,Long date, boolean includeDeleted) {

        OperationDto od = new OperationDto();
        FilterDto filter = and(orModifiedOrDeletedAfter(date), or(ID_USER_WHO_IS_FOLLOWED).isEqualTo(idUserWhoFollow)).build();

        MetadataDto md = new MetadataDto(Constants.OPERATION_RETRIEVE,"Followers", includeDeleted, null, null, null, filter);
        od.setMetadata(md);

        Map<String, Object>[] array = new HashMap[1];
        array[0] = userMapper.reqRestUsersToDto(null);
        od.setData(array);

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_FOLLOWERS, od);
    }

    public GenericDto getUserByUserId(Long userId){
        OperationDto od  = new OperationDto();
        Map<String,Object> key = new HashMap<>();
        key.put(UserTable.ID,userId);
        MetadataDto md = new MetadataDto(Constants.OPERATION_RETRIEVE,UserTable.TABLE,true,null,0L,1L,key);
        od.setMetadata(md);
        Map<String,Object>[] array = new HashMap[1];
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

    public Map<String,Object>[] getDataForFollow(){
        Map<String,Object>[] array = new HashMap[1];
        array[0] = followMapper.toDto(null);
        return array;
    }

}