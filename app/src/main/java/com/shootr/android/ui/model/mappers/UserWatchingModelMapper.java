package com.shootr.android.ui.model.mappers;

import android.content.res.Resources;
import com.shootr.android.R;
import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import com.shootr.android.ui.model.UserWatchingModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class UserWatchingModelMapper {

    private final String watchingText;
    private final String notWatchingText;

    @Inject public UserWatchingModelMapper(Resources resources) {
        watchingText = resources.getString(R.string.watching_text);
        notWatchingText = resources.getString(R.string.watching_not_text);
    }

    public UserWatchingModel transform(Watch watch) {
        UserWatchingModel model = new UserWatchingModel();
        model.setPlace(userStatus(watch));
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

    private String userStatus(Watch watch) {
        if (watch.getUserStatus() != null) {
            return watch.getUserStatus();
        } else {
            return watch.isWatching() ? watchingText : notWatchingText;
        }
    }

}
