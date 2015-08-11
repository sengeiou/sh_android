package com.shootr.android.ui.model.mappers;

import com.shootr.android.data.entity.FollowEntity;
import com.shootr.android.domain.User;
import com.shootr.android.ui.model.UserModel;
import java.util.ArrayList;
import java.util.List;

public class UserModelMapper {

    public UserModel transform(User user) {
        UserModel userModel = new UserModel();
        userModel.setIdUser(user.getIdUser());
        userModel.setBio(user.getBio());
        userModel.setUsername(user.getUsername());
        userModel.setPoints(user.getPoints());
        userModel.setName(user.getName());
        userModel.setNumFollowers(user.getNumFollowers());
        userModel.setNumFollowings(user.getNumFollowings());
        userModel.setPhoto(user.getPhoto());
        userModel.setRelationship(user.isMe() ? FollowEntity.RELATIONSHIP_OWN : getRelationShip(user));
        userModel.setWebsite(user.getWebsite());
        userModel.setEmail(user.getEmail());
        userModel.setEmailConfirmed(user.getEmailConfirmed() == 1);
        userModel.setStreamWatchingId(user.getIdWatchingStream());
        userModel.setStreamWatchingTitle(user.getWatchingStreamTitle());
        userModel.setJoinStreamDate(user.getJoinStreamDate());
        return userModel;
    }

    public List<UserModel> transform(List<User> userList) {
        List<UserModel> userModels = new ArrayList<>(userList.size());
        for (User user : userList) {
            userModels.add(transform(user));
        }
        return userModels;
    }

    private int getRelationShip(User user) {
        boolean following = user.isFollowing();
        boolean follower = user.isFollower();
        if (following && follower) {
            return FollowEntity.RELATIONSHIP_BOTH;
        } else if(following) {
            return FollowEntity.RELATIONSHIP_FOLLOWING;
        }else if (follower) {
            return FollowEntity.RELATIONSHIP_FOLLOWER;
        } else {
            return FollowEntity.RELATIONSHIP_NONE;
        }
    }
}
