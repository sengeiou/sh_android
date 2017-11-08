package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.ui.model.UserModel;

@Deprecated public class UserEntityModelMapper {

    public UserModel toUserModel(UserEntity user, FollowEntity follow, boolean isMe) {
        UserModel userModel = new UserModel();
        userModel.setIdUser(user.getIdUser());
        userModel.setBio(user.getBio());
        userModel.setUsername(user.getUserName());
        userModel.setPoints(user.getPoints());
        userModel.setName(user.getName());
        userModel.setNumFollowers(user.getNumFollowers());
        userModel.setNumFollowings(user.getNumFollowings());
        userModel.setPhoto(user.getPhoto());
        userModel.setMe(isMe);
        userModel.setFollowing(user.isFollowing());
        userModel.setWebsite(user.getWebsite());
        userModel.setStreamWatchingId(user.getIdWatchingStream());
        userModel.setStreamWatchingTitle(user.getWatchingStreamTitle());
        userModel.setFavoritedStreamsCount(user.getFavoritedStreamsCount());
        userModel.setCreatedStreamsCount(user.getCreatedStreamsCount());
        userModel.setVerifiedUser(user.getVerifiedUser() == 1);
        userModel.setMuted(user.isMuted());
        return userModel;
    }
}
