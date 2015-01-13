package com.shootr.android.ui.model.mappers;

import android.content.Context;
import com.shootr.android.data.entity.UserEntity;
import com.shootr.android.ui.model.UserWatchingModel;

@Deprecated
public class UserEntityWatchingModelMapper {


    public UserEntityWatchingModelMapper(Context context) {
    }

    public UserWatchingModel toUserWatchingModel(UserEntity user, boolean isWatching, boolean isLive, String place) {
        UserWatchingModel userModel = new UserWatchingModel();
        userModel.setIdUser(user.getIdUser());
        userModel.setFavoriteTeamId(user.getFavoriteTeamId());
        userModel.setPhoto(user.getPhoto());
        userModel.setWatching(isWatching);
        userModel.setPlace(place);
        userModel.setUserName(user.getUserName());
        userModel.setLive(isLive && isWatching); //TODO esto es lógica de negocio, no debería estar en un mapper
        return userModel;
    }

}
