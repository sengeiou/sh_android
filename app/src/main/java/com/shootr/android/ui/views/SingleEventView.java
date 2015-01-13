package com.shootr.android.ui.views;

import com.shootr.android.ui.model.MatchModel;
import com.shootr.android.ui.model.UserWatchingModel;
import java.util.List;

public interface SingleEventView extends LoadDataView{

    void setEventTitle(String title);

    void setEventDate(String date);

    void setWatchers(List<UserWatchingModel> watchers);

    void setWatchersCount(int watchersCount);

    void setCurrentUserWatching(UserWatchingModel userWatchingModel);

    void setIsWatching(boolean watching);

    void setNotificationsEnabled(boolean enabled);

    void navigateToEdit(MatchModel eventModel, UserWatchingModel currentUserWatchingModel);
}
