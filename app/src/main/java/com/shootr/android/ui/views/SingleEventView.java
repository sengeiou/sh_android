package com.shootr.android.ui.views;

import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.UserWatchingModel;
import java.util.List;

public interface SingleEventView extends LoadDataView{

    void setEventTitle(String title);

    void setEventDate(String date);

    void setWatchers(List<UserWatchingModel> watchers);

    void setWatchersCount(int watchersCount);

    void setEventsCount(int eventsCount);

    void setCurrentUserWatching(UserWatchingModel userWatchingModel);

    void setIsWatching(boolean watching);

    void setNotificationsEnabled(boolean enabled);

    void alertNotificationsEnabled();

    void alertNotificationsDisabled();

    void navigateToEdit(EventModel eventModel, UserWatchingModel currentUserWatchingModel);

    void showContent();
}
