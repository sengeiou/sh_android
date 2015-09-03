package com.shootr.android.ui.views;

import com.shootr.android.ui.model.UserModel;
import com.shootr.android.ui.views.base.DataTransferView;
import java.util.List;

public interface StreamDetailView extends DataTransferView {

    void setStreamTitle(String title);

    void setStreamShortTitle(String shortTitle);

    void setStreamAuthor(String author);

    void setStreamPicture(String picture);

    void showEditStreamPhotoOrInfo();

    void showPhotoPicker();

    void showEditPicturePlaceholder();

    void showLoadingPictureUpload();

    void hideLoadingPictureUpload();

    void zoomPhoto(String picture);

    void setWatchers(List<UserModel> watchers);

    void setWatchersCount(int watchersCount);

    void setCurrentUserWatching(UserModel userWatchingModel);

    void navigateToEditStream(String idStream);

    void navigateToUser(String userId);

    void showDetail();

    void showEditStreamButton();

    void hideEditStreamButton();

    void setMediaCount(Integer mediaCount);

    void navigateToMedia(String idStream, Integer streamMediaCount);

    void setStreamDescription(String description);

    void hideStreamDescription();
}
