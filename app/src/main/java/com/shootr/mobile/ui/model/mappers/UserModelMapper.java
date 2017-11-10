package com.shootr.mobile.ui.model.mappers;

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
        userModel.setMe(user.isMe());
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
        userModel.setFollowing(user.isFollowing());
        Long joinStreamDate = user.getJoinStreamDate();
        if (joinStreamDate != null) {
            userModel.setJoinStreamDate(streamJoinDateFormatter.format(joinStreamDate));
            userModel.setJoinStreamTimestamp(joinStreamDate);
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
}
