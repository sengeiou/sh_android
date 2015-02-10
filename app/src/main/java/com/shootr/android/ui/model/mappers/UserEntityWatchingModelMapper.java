package com.shootr.android.ui.model.mappers;

import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.ui.model.UserWatchingModel;

@Deprecated
public class UserEntityWatchingModelMapper {


    public UserEntityWatchingModelMapper() {
    }

    public UserWatchingModel toUserWatchingModel(UserEntity user, String status) {
        UserWatchingModel userModel = new UserWatchingModel();
        userModel.setIdUser(user.getIdUser());
        userModel.setFavoriteTeamId(user.getFavoriteTeamId());
        userModel.setPhoto(user.getPhoto());
        userModel.setStatus(status);
        userModel.setUserName(user.getUserName());
        return userModel;
    }

}
