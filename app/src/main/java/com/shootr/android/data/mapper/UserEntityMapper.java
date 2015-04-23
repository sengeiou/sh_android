package com.shootr.android.data.mapper;

import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.domain.User;
import java.util.ArrayList;
import java.util.List;
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

        user.setVisibleEventId(userEntity.getIdEvent());
        user.setVisibleEventTitle(userEntity.getEventTitle());

        user.setMe(userEntity.getIdUser().equals(currentUserId));
        user.setFollower(isFollower);
        user.setFollowing(isFollowing);

        user.setStatus(userEntity.getStatus());

        if(userEntity.getCheckIn() == null){
            user.setCheckedIn(false);
        }else{
            user.setCheckedIn(userEntity.getCheckIn() == 1);
        }


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
        userEntity.setName(user.getName());
        userEntity.setEmail(user.getEmail());
        userEntity.setPhoto(user.getPhoto());
        userEntity.setPoints(user.getPoints());
        userEntity.setNumFollowings(user.getNumFollowings());
        userEntity.setNumFollowers(user.getNumFollowers());
        userEntity.setWebsite(user.getWebsite());
        userEntity.setBio(user.getBio());

        userEntity.setIdEvent(user.getVisibleEventId());
        userEntity.setEventTitle(user.getVisibleEventTitle());

        userEntity.setStatus(user.getStatus());
        userEntity.setCheckIn(user.isCheckedIn() ? 1 : 0);

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

    public User transform(UserEntity user, Long idCurrentUser) {
        return transform(user, idCurrentUser, false, false);
    }

    public User transform(UserEntity user) {
        return transform(user, -1L, false, false);
    }
}
