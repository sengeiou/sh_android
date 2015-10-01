package com.shootr.android.service.dataservice.dto;

import com.shootr.android.db.mappers.FollowMapper;
import com.shootr.android.db.mappers.UserMapper;
import javax.inject.Inject;

public class UserDtoFactory {

    public static final long MAX_FOLLOWS_ITEMS = 100L;

    public static final Integer GET_FOLLOWERS = 0;
    public static final Integer GET_FOLLOWING = 1;

    public static final int FOLLOW_TYPE = 0;
    public static final int UNFOLLOW_TYPE = 1;

    private static final String ALIAS_FOLLOW_USER = "FOLLOW_USER";
    private static final String ALIAS_UNFOLLOW_USER = "UNFOLLOW_USER";
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

    private UtilityDtoFactory utilityDtoFactory;
    UserMapper userMapper;
    FollowMapper followMapper;

    @Inject public UserDtoFactory(UtilityDtoFactory utilityDtoFactory, UserMapper userMapper, FollowMapper followMapper) {
        this.utilityDtoFactory = utilityDtoFactory;
        this.userMapper = userMapper;
        this.followMapper = followMapper;
    }

}