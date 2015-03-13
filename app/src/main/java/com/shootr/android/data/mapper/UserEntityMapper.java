package com.shootr.android.data.mapper;

import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.domain.User;
import javax.inject.Inject;

public class UserEntityMapper {

    @Inject public UserEntityMapper() {
    }

    public User transform(UserEntity userEntity, Long currentUserId, boolean isFollower, boolean isFollowing) {
        if (userEntity == null) {
            return null;
        }
        User user = new User();
        user.setIdUser(userEntity.getIdUser());
        user.setFavoriteTeamName(userEntity.getFavoriteTeamName());
        user.setFavoriteTeamId(userEntity.getFavoriteTeamId());
        user.setUsername(userEntity.getUserName());
        user.setName(userEntity.getName());
        user.setPhoto(userEntity.getPhoto());
        user.setNumFollowings(userEntity.getNumFollowings());
        user.setNumFollowers(userEntity.getNumFollowers());
        user.setWebsite(userEntity.getWebsite());
        user.setBio(userEntity.getBio());
        user.setPoints(userEntity.getPoints());

        user.setEventWatchingId(userEntity.getIdEvent());
        user.setEventWatchingTitle(userEntity.getEventTitle());

        user.setMe(userEntity.getIdUser().equals(currentUserId));
        user.setFollower(isFollower);
        user.setFollowing(isFollowing);

        return user;
    }

    public UserEntity transform(User user) {
        if (user == null) {
            return null;
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setIdUser(user.getIdUser());
        userEntity.setFavoriteTeamId(user.getFavoriteTeamId());
        userEntity.setFavoriteTeamName(user.getFavoriteTeamName());
        //TODO userEntity.setSessionToken();
        userEntity.setUserName(user.getUsername());
        userEntity.setEmail(user.getEmail());
        userEntity.setPhoto(user.getPhoto());
        userEntity.setPoints(user.getPoints());
        userEntity.setNumFollowings(user.getNumFollowings());
        userEntity.setNumFollowers(user.getNumFollowers());
        userEntity.setWebsite(user.getWebsite());
        userEntity.setBio(user.getBio());

        userEntity.setIdEvent(user.getEventWatchingId());
        userEntity.setEventTitle(user.getEventWatchingTitle());

        //TODO synchronized fields
        return userEntity;
    }

    public User transform(UserEntity user, Long idCurrentUser) {
        return transform(user, idCurrentUser, false, false);
    }
}
