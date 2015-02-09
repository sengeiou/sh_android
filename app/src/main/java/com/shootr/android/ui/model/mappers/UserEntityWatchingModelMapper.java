package com.shootr.android.ui.model.mappers;

import android.content.Context;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.ui.model.UserWatchingModel;

@Deprecated
public class UserEntityWatchingModelMapper {


    public UserEntityWatchingModelMapper() {
    }

    public UserWatchingModel toUserWatchingModel(UserEntity user, String place) {
        UserWatchingModel userModel = new UserWatchingModel();
        userModel.setIdUser(user.getIdUser());
        userModel.setFavoriteTeamId(user.getFavoriteTeamId());
        userModel.setPhoto(user.getPhoto());
        userModel.setPlace(place);
        userModel.setUserName(user.getUserName());
        return userModel;
    }

}
