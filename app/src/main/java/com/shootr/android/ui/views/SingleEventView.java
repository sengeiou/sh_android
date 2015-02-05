package com.shootr.android.ui.views;

import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.UserWatchingModel;
import com.shootr.android.ui.views.base.LoadDataView;
import java.util.List;

public interface SingleEventView extends LoadDataView {

    void setEventTitle(String title);

    void setEventDate(String date);

    void setEventPicture(String picture);

    void showEditPicture(String picture);

    void hideEditPicture();

    void showLoadingPictureUpload();

    void hideLoadingPictureUpload();

    void setWatchers(List<UserWatchingModel> watchers);

    void setWatchersCount(int watchersCount);

    void setEventsCount(int eventsCount);

    void setCurrentUserWatching(UserWatchingModel userWatchingModel);

    void setIsWatching(boolean watching);

    void setNotificationsEnabled(boolean enabled);

    void alertNotificationsEnabled();

    void alertNotificationsDisabled();

    void navigateToEditStatus(EventModel eventModel, UserWatchingModel currentUserWatchingModel);

    void navigateToEditEvent(Long idEvent);

    void showContent();

    void hideContent();

    void showDetail();

    void showEditEventButton();

    void hideEditEventButton();
}
