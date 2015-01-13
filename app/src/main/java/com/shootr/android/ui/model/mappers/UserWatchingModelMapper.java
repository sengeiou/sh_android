package com.shootr.android.ui.model.mappers;

import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import com.shootr.android.ui.model.UserWatchingModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class UserWatchingModelMapper {

    @Inject public UserWatchingModelMapper() {
    }

    public UserWatchingModel transform(Watch watch) {
        UserWatchingModel model = new UserWatchingModel();
        model.setPlace(watch.getUserStatus());
        model.setWatching(watch.isWatching());

        User user = watch.getUser();
        model.setIdUser(user.getIdUser());
        model.setUserName(user.getUsername());
        model.setPhoto(user.getPhoto());
        return model;
    }

    public List<UserWatchingModel> transform(List<Watch> watchList) {
        List<UserWatchingModel> userWatchingModels = new ArrayList<>(watchList.size());
        for (Watch watch : watchList) {
            userWatchingModels.add(transform(watch));
        }
        return userWatchingModels;
    }

}
