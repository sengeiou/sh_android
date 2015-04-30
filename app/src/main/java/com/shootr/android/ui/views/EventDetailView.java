package com.shootr.android.ui.views;

import com.shootr.android.ui.model.EventModel;
import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.views.base.LoadDataView;
import java.util.List;

public interface EventDetailView extends LoadDataView {

    void setEventTitle(String title);

    void setEventDate(String date);

    void setEventAuthor(String author);

    void setEventPicture(String picture);

    void showEditEventPhotoOrInfo();

    void showPhotoPicker();

    void showEditPicture(String picture);

    void hideEditPicture();

    void showLoadingPictureUpload();

    void hideLoadingPictureUpload();

    void zoomPhoto(String picture);

    void setWatchers(List<UserModel> watchers);

    void setWatchersCount(int watchersCount);

    void setCurrentUserWatching(UserModel userWatchingModel);

    void navigateToEditStatus(EventModel eventModel, String currentStatus);

    void navigateToEditEvent(String idEvent);

    void navigateToUser(String userId);

    void showContent();

    void hideContent();

    void showDetail();

    void showEditEventButton();

    void hideEditEventButton();

    void showCheckin();

    void hideCheckinLoading();

    void showCheckinLoading();

    void hideCheckin();

    void showCheckinErrorRetry(String errorMessage);

    void showCheckinConfirmation();
}
