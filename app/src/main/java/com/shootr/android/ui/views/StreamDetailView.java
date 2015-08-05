package com.shootr.android.ui.views;

import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.views.base.LoadDataView;
import java.util.List;

public interface StreamDetailView extends LoadDataView {

    void setStreamTitle(String title);

    void setStreamAuthor(String author);

    void setStreamPicture(String picture);

    void showEditStreamPhotoOrInfo();

    void showPhotoPicker();

    void showEditPicture(String picture);

    void hideEditPicture();

    void showLoadingPictureUpload();

    void hideLoadingPictureUpload();

    void zoomPhoto(String picture);

    void setWatchers(List<UserModel> watchers);

    void setWatchersCount(int watchersCount);

    void setCurrentUserWatching(UserModel userWatchingModel);

    void navigateToEditStream(String idStream);

    void navigateToUser(String userId);

    void showContent();

    void hideContent();

    void showDetail();

    void showEditStreamButton();

    void hideEditStreamButton();

    void setMediaCount(Integer mediaCount);

    void navigateToMedia(String idStream, Integer streamMediaCount);

    void showMediaCount();

    void setStreamDescription(String description);

    void hideStreamDescription();
}
