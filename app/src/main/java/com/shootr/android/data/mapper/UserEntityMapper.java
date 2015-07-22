package com.shootr.android.data.mapper;

import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.domain.User;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class UserEntityMapper {

    @Inject public UserEntityMapper() {
    }

    public User transform(UserEntity userEntity, String currentUserId, boolean isFollower, boolean isFollowing) {
        if (userEntity == null) {
            return null;
        }
        User user = new User();
        user.setIdUser(userEntity.getIdUser());
        user.setUsername(userEntity.getUserName());
        user.setName(userEntity.getName());
        user.setPhoto(userEntity.getPhoto());
        user.setNumFollowings(userEntity.getNumFollowings());
        user.setNumFollowers(userEntity.getNumFollowers());
        user.setWebsite(userEntity.getWebsite());
        user.setBio(userEntity.getBio());
        user.setPoints(userEntity.getPoints());

        user.setIdWatchingStream(userEntity.getIdWatchingEvent());
        user.setWatchingStreamTitle(userEntity.getWatchingEventTitle());

        user.setMe(userEntity.getIdUser().equals(currentUserId));
        user.setFollower(isFollower);
        user.setFollowing(isFollowing);

        if(userEntity.getJoinEventDate() != null){
            user.setJoinStreamDate(userEntity.getJoinEventDate());
        }

        return user;
    }

    public UserEntity transform(User user) {
        if (user == null) {
            return null;
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setIdUser(user.getIdUser());
        userEntity.setUserName(user.getUsername());
        userEntity.setName(user.getName());
        userEntity.setEmail(user.getEmail());
        userEntity.setPhoto(user.getPhoto());
        userEntity.setPoints(user.getPoints());
        userEntity.setNumFollowings(user.getNumFollowings());
        userEntity.setNumFollowers(user.getNumFollowers());
        userEntity.setWebsite(user.getWebsite());
        userEntity.setBio(user.getBio());

        userEntity.setIdWatchingEvent(user.getIdWatchingStream());

        userEntity.setWatchingEventTitle(user.getWatchingEventTitle());

        userEntity.setJoinEventDate(user.getJoinStreamDate());

        //TODO synchronized fields
        return userEntity;
    }

    public List<UserEntity> transform(List<User> users) {
        List<UserEntity> userEntities = new ArrayList<>(users.size());
        UserEntity userEntity;
        for (User user : users) {
            userEntity = transform(user);
            if (userEntity != null) {
                userEntities.add(userEntity);
            }
        }
        return userEntities;
    }

    public User transform(UserEntity user, String idCurrentUser) {
        return transform(user, idCurrentUser, false, false);
    }

    public User transform(UserEntity user) {
        return transform(user, "-1L", false, false);
    }
}
