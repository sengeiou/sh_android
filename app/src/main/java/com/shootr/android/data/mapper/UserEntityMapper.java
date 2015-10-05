package com.shootr.android.data.mapper;

import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.domain.User;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class UserEntityMapper {

    private final MetadataMapper metadataMapper;

    @Inject public UserEntityMapper(MetadataMapper metadataMapper) {
        this.metadataMapper = metadataMapper;
    }

    public User transform(UserEntity userEntity, String currentUserId, boolean isFollower, boolean isFollowing) {
        if (userEntity == null) {
            return null;
        }
        User user = new User();
        user.setIdUser(userEntity.getIdUser());
        user.setUsername(userEntity.getUserName());
        user.setName(userEntity.getName());
        user.setEmail(userEntity.getEmail());
        user.setEmailConfirmed(userEntity.getEmailConfirmed() != null && userEntity.getEmailConfirmed() == 1);
        user.setPhoto(userEntity.getPhoto());
        user.setNumFollowings(userEntity.getNumFollowings());
        user.setNumFollowers(userEntity.getNumFollowers());
        user.setWebsite(userEntity.getWebsite());
        user.setBio(userEntity.getBio());
        user.setPoints(userEntity.getPoints());

        user.setIdWatchingStream(userEntity.getIdWatchingStream());
        user.setWatchingStreamTitle(userEntity.getWatchingStreamTitle());

        user.setMe(userEntity.getIdUser().equals(currentUserId));
        user.setFollower(isFollower);
        user.setFollowing(isFollowing);

        user.setJoinStreamDate(userEntity.getJoinStreamDate());

        user.setMetadata(metadataMapper.metadataFromEntity(userEntity));

        user.setCreatedStreamsCount(userEntity.getCreatedStreamsCount());
        user.setFavoritedStreamsCount(userEntity.getFavoritedStreamsCount());

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
        userEntity.setEmailConfirmed(user.isEmailConfirmed() ? 1 : 0);
        userEntity.setPhoto(user.getPhoto());
        userEntity.setPoints(user.getPoints());
        userEntity.setNumFollowings(user.getNumFollowings());
        userEntity.setNumFollowers(user.getNumFollowers());
        userEntity.setWebsite(user.getWebsite());
        userEntity.setBio(user.getBio());

        userEntity.setIdWatchingStream(user.getIdWatchingStream());

        userEntity.setWatchingStreamTitle(user.getWatchingStreamTitle());

        userEntity.setJoinStreamDate(user.getJoinStreamDate());

        metadataMapper.fillEntityWithMetadata(userEntity, user.getMetadata());

        userEntity.setCreatedStreamsCount(user.getCreatedStreamsCount());
        userEntity.setFavoritedStreamsCount(user.getFavoritedStreamsCount());
        
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

    public List<User> transformEntities(List<UserEntity> entities) {
        List<User> users = new ArrayList<>(entities.size());
        User user;
        for (UserEntity userEntity : entities) {
            user = transform(userEntity);
            if (user != null) {
                users.add(user);
            }
        }
        return users;
    }

    public User transform(UserEntity user, String idCurrentUser) {
        return transform(user, idCurrentUser, false, false);
    }

    public User transform(UserEntity user) {
        return transform(user, "-1L", false, false);
    }
}
