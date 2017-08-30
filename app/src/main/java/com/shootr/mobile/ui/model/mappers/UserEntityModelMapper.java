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
        userModel.setRelationship(isMe ? FollowEntity.RELATIONSHIP_OWN : getRelationShip(follow));
        userModel.setWebsite(user.getWebsite());
        userModel.setStreamWatchingId(user.getIdWatchingStream());
        userModel.setStreamWatchingTitle(user.getWatchingStreamTitle());
        userModel.setFavoritedStreamsCount(user.getFavoritedStreamsCount());
        userModel.setCreatedStreamsCount(user.getCreatedStreamsCount());
        userModel.setVerifiedUser(user.getVerifiedUser() == 1);
        userModel.setMuted(user.isMuted());
        return userModel;
    }

    private int getRelationShip(FollowEntity f) {
        if (f != null && f.getBirth() != null && f.getDeleted() == null) {
            return FollowEntity.RELATIONSHIP_FOLLOWING;
        } else {
            return FollowEntity.RELATIONSHIP_NONE;
        }
    }
}
