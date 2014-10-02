package gm.mobi.android.service.dataservice.dto;


import android.provider.SyncStateContract;
import android.support.v4.util.ArrayMap;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Filter;

import javax.inject.Inject;

import gm.mobi.android.constant.Constants;
import gm.mobi.android.constant.ServiceConstants;
import gm.mobi.android.db.GMContract;
import gm.mobi.android.db.GMContract.UserTable;
import gm.mobi.android.db.manager.TeamManager;
import gm.mobi.android.db.mappers.FollowMapper;
import gm.mobi.android.db.mappers.TeamMapper;
import gm.mobi.android.db.mappers.UserMapper;
import gm.mobi.android.db.objects.Follow;
import gm.mobi.android.service.dataservice.generic.FilterBuilder;
import gm.mobi.android.service.dataservice.generic.FilterDto;
import gm.mobi.android.service.dataservice.generic.FilterItemDto;
import gm.mobi.android.service.dataservice.generic.GenericDto;
import gm.mobi.android.service.dataservice.generic.MetadataDto;
import gm.mobi.android.service.dataservice.generic.OperationDto;
import gm.mobi.android.db.GMContract.FollowTable;

import static gm.mobi.android.service.dataservice.generic.FilterBuilder.and;
import static gm.mobi.android.service.dataservice.generic.FilterBuilder.or;
import static gm.mobi.android.service.dataservice.generic.FilterBuilder.orModifiedOrDeletedAfter;

public class UserDtoFactory {

    static final int NUMBER_OF_DAYS_AGO = 7;

    public static final int GET_FOLLOWERS = 0;
    public static final int GET_FOLLOWING = 1;
    public static final int GET_ALL_FOLLOW = 2;
    public static final int GET_JUST_BOTHFOLLOW = 3;

    private static final String ENTITY_LOGIN = "Login";
    private static final String ALIAS_LOGIN = "Login";
    private static final String ALIAS_GET_FOLLOWINGS = "GET_FOLLOWINGS";
    private static final String ALIAS_GET_USERS = "GET_USERS";
    private static final String ALIAS_GETUSERBYID = "GET_USERBYID";
    private static final String ALIAS_RETRIEVE_TEAM_BY_ID = "GET_TEAMBYID";
    private static final String ALIAS_RETRIEVE_TEAMS_BY_TEAMIDS = "GET_TEAMS_BY_TEAMSIDS";
    private static final String ALIAS_GETFOLLOWRELATIONSHIP = "GET_FOLLOWRELATIONSHIP";

    private UtilityDtoFactory utilityDtoFactory;

    @Inject public UserDtoFactory(UtilityDtoFactory utilityDtoFactory) {
        this.utilityDtoFactory = utilityDtoFactory;
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
        data[0] = UserMapper.toDto(null);
        op.setData(data);

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_LOGIN, op);
    }

    public GenericDto getFollowOperationDto(Long userId, Long offset, int relationship, Long date) {

        OperationDto od = new OperationDto();

        FilterDto filter = getFollowsByIdUserAndRelationship(userId, relationship, date);
        MetadataDto md = new MetadataDto(Constants.OPERATION_RETRIEVE, FollowTable.TABLE, true, null, 0l, 100L, filter);
        od.setMetadata(md);

        Map<String, Object>[] array = new HashMap[1];
        array[0] = FollowMapper.toDto(null);
        od.setData(array);

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_FOLLOWINGS, od);
    }

    public GenericDto getFollowOperationForGettingRelationship(Long userId,Long currentUserId,int typeFollow){
        OperationDto od = new OperationDto();
        FilterDto filter = getFollowsRelationshipBetween2Users(userId,currentUserId,typeFollow);
        MetadataDto md = new MetadataDto(Constants.OPERATION_RETRIEVE, FollowTable.TABLE, true, null, 0L, 20L,filter);
        od.setMetadata(md);
        od.setData(getDataForFollow());
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GETFOLLOWRELATIONSHIP, od);
    }



    public GenericDto getUserByUserId(Long userId){
        OperationDto od  = new OperationDto();
        Map<String,Object> key = new HashMap<>();
        key.put(UserTable.ID,userId);
        MetadataDto md = new MetadataDto(Constants.OPERATION_RETRIEVE,UserTable.TABLE,true,null,0L,1L,key);
        od.setMetadata(md);
        Map<String,Object>[] array = new HashMap[1];
        array[0] = UserMapper.reqRestUsersToDto(null);
        od.setData(array);
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GETUSERBYID, od);
    }

    public GenericDto getTeamByTeamId(Long teamId){
        OperationDto od = new OperationDto();
        Map<String,Object> key = new HashMap<>();
        key.put(GMContract.TeamTable.ID_TEAM, teamId);
        MetadataDto md = new MetadataDto(Constants.OPERATION_RETRIEVE, GMContract.TeamTable.TABLE, true,null,0L,1L,key);
        od.setMetadata(md);
        Map<String,Object>[] array = new HashMap[1];
        array[0] = TeamMapper.toDto(null);
        od.setData(array);
        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_RETRIEVE_TEAM_BY_ID,  od);
    }

    public GenericDto getTeamsByTeamIds(Set<Long> teamIds) {

        FilterDto filter = and(
            or(GMContract.TeamTable.ID_TEAM).isIn(teamIds),
            orModifiedOrDeletedAfter(0L)
        ).build();

        MetadataDto md = new MetadataDto.Builder().operation(Constants.OPERATION_RETRIEVE)
            .entity(GMContract.TeamTable.TABLE)
            .includeDeleted(false)
            .totalItems(null)
            .items((long) teamIds.size())
            .filter(filter)
            .build();

        OperationDto od = new OperationDto.Builder()
            .metadata(md)
            .putData(TeamMapper.toDto(null))
            .build();

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_RETRIEVE_TEAMS_BY_TEAMIDS, od);
    }

    public GenericDto getUsersOperationDto(List<Long> userIds, Long offset, Long date) {
        OperationDto od = new OperationDto();
        FilterDto filter = getUsersByUserIds(userIds, new Date(date));

        MetadataDto md = new MetadataDto(Constants.OPERATION_RETRIEVE, UserTable.TABLE, true, null, 0l, 100L, filter);
        od.setMetadata(md);

        Map<String, Object>[] array = new HashMap[1];
        array[0] = UserMapper.reqRestUsersToDto(null);
        od.setData(array);

        return utilityDtoFactory.getGenericDtoFromOperation(ALIAS_GET_USERS, od);
    }


    public FilterDto getFollowsRelationship(Long userId, Long currentUserId, Long lastModifiedDate){
        FilterDto shotsFilter =
                and(FollowTable.ID_FOLLOWED_USER).isEqualTo(userId).and(FollowTable.ID_USER).isEqualTo(currentUserId)
                .and(FollowTable.ID_FOLLOWED_USER).isEqualTo(currentUserId).and(FollowTable.ID_USER).isEqualTo(userId)
                .and(FollowTable.CSYS_DELETED).isEqualTo(null)
                .and(FollowTable.CSYS_MODIFIED).greaterThan(0L)
                .build();
        return shotsFilter;
    }

    public FilterDto getFollowsRelationshipBetween2Users(Long userId, Long currentUserID,int relationship){
        FilterDto filterDto = null;
        switch (relationship) {
            case GET_FOLLOWERS:
                filterDto = new FilterDto(Constants.NEXUS_AND,
                        new FilterItemDto[]{
                                new FilterItemDto(Constants.COMPARATOR_EQUAL, FollowTable.ID_FOLLOWED_USER, currentUserID),
                                new FilterItemDto(Constants.COMPARATOR_EQUAL, FollowTable.ID_USER, userId)
                        }, utilityDtoFactory.getTimeFilterDto(0l)
                );
                break;
            case GET_FOLLOWING:
                filterDto = new FilterDto(Constants.NEXUS_AND,
                        new FilterItemDto[]{
                                new FilterItemDto(Constants.COMPARATOR_EQUAL, FollowTable.ID_FOLLOWED_USER, userId),
                                new FilterItemDto(Constants.COMPARATOR_EQUAL, FollowTable.ID_USER, currentUserID)
                        }, utilityDtoFactory.getTimeFilterDto(0l)
                );
                break;
        }
        return filterDto;
    }

    public FilterDto getFollowsByIdUserAndRelationship(Long userId, int relationship, Long lastModifiedDate) {
        FilterDto filterDto = null;
        switch (relationship) {
            case GET_FOLLOWERS:
                filterDto = new FilterDto(Constants.NEXUS_AND,
                        new FilterItemDto[]{
                                new FilterItemDto(Constants.COMPARATOR_NOT_EQUAL, FollowTable.ID_FOLLOWED_USER, userId),
                                new FilterItemDto(Constants.COMPARATOR_EQUAL, FollowTable.ID_USER, null)
                        }, utilityDtoFactory.getTimeFilterDto(lastModifiedDate)
                );
                break;
            case GET_FOLLOWING:
                filterDto = new FilterDto(Constants.NEXUS_AND,
                        new FilterItemDto[]{
                                new FilterItemDto(Constants.COMPARATOR_NOT_EQUAL, FollowTable.ID_FOLLOWED_USER, null),
                                new FilterItemDto(Constants.COMPARATOR_EQUAL, FollowTable.ID_USER, userId)
                        }, utilityDtoFactory.getTimeFilterDto(lastModifiedDate)
                );
                break;
            case GET_ALL_FOLLOW:
                filterDto = new FilterDto(Constants.NEXUS_AND,
                        new FilterItemDto[]{
                                new FilterItemDto(Constants.COMPARATOR_EQUAL, FollowTable.ID_FOLLOWED_USER, userId),
                                new FilterItemDto(Constants.COMPARATOR_EQUAL, FollowTable.ID_USER, userId)
                        }, utilityDtoFactory.getTimeFilterDto(lastModifiedDate)
                );
                break;
            case GET_JUST_BOTHFOLLOW:
                filterDto = new FilterDto(Constants.NEXUS_AND,
                        new FilterItemDto[]{
                                new FilterItemDto(Constants.COMPARATOR_EQUAL, FollowTable.ID_FOLLOWED_USER, userId),
                                new FilterItemDto(Constants.COMPARATOR_EQUAL, FollowTable.ID_USER, userId)
                        }, utilityDtoFactory.getTimeFilterDto(lastModifiedDate)
                );
                break;
        }
        return filterDto;
    }

    public FilterDto getUsersByUserIds(List<Long> userIds, Date lastModifiedDate) {
        return and(
                or(UserTable.ID).isIn(userIds),
                orModifiedOrDeletedAfter(lastModifiedDate.getTime())
        ).build();
    }

    public Map<String,Object>[] getDataForFollow(){
        Map<String,Object>[] array = new HashMap[1];
        array[0] = FollowMapper.toDto(null);
        return array;
    }

}