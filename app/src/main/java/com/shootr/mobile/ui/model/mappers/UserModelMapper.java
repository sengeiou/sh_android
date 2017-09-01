package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.data.entity.FollowEntity;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.utils.StreamJoinDateFormatter;
import com.shootr.mobile.ui.model.UserModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class UserModelMapper {

    private final StreamJoinDateFormatter streamJoinDateFormatter;

    @Inject public UserModelMapper(StreamJoinDateFormatter streamJoinDateFormatter) {
        this.streamJoinDateFormatter = streamJoinDateFormatter;
    }

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
        userModel.setEmailConfirmed(user.isEmailConfirmed());
        userModel.setVerifiedUser(user.isVerifiedUser());
        userModel.setStreamWatchingId(user.getIdWatchingStream());
        userModel.setStreamWatchingTitle(user.getWatchingStreamTitle());
        userModel.setFavoritedStreamsCount(user.getFavoritedStreamsCount());
        userModel.setSocialLogin(user.isSocialLogin());
        userModel.setCreatedStreamsCount(user.getCreatedStreamsCount());
        userModel.setStrategic(user.isStrategic());
        userModel.setMuted(user.isMuted());
        Long joinStreamDate = user.getJoinStreamDate();
        if (joinStreamDate != null) {
            userModel.setJoinStreamDate(streamJoinDateFormatter.format(joinStreamDate));
            userModel.setJoinStreamTimestamp(joinStreamDate);
        }
        if (user.getRelationship() != null) {
            userModel.setRelationship(user.getRelationship());
        }
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
        } else if (following) {
            return FollowEntity.RELATIONSHIP_FOLLOWING;
        } else if (follower) {
            return FollowEntity.RELATIONSHIP_FOLLOWER;
        } else {
            return FollowEntity.RELATIONSHIP_NONE;
        }
    }
}
