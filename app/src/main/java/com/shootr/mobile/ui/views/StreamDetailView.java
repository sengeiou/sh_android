package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.views.base.DataTransferView;
import java.util.List;

public interface StreamDetailView extends DataTransferView {

    void setStreamTitle(String title);

    void setStreamAuthor(String author);

    void setStreamPicture(String picture);

    void showEditStreamPhotoOrInfo();

    void showPhotoOptions();

    void showEditPicturePlaceholder();

    void showLoadingPictureUpload();

    void hideLoadingPictureUpload();

    void zoomPhoto(String picture);

    void setWatchers(List<UserModel> watchers);

    void setCurrentUserWatching(UserModel userWatchingModel);

    void navigateToEditStream(String idStream);

    void navigateToUser(String userId);

    void showDetail();

    void showEditStreamButton();

    void navigateToMedia(String idStream, Integer streamMediaCount);

    void setStreamDescription(String description);

    void hideStreamDescription();

    void showAllParticipantsButton();

    void setFollowingNumber(Integer numberOfFollowing, Integer totalWatchers);

    void showStreamShared();

    void shareStreamVia(StreamModel stream);

    void goToAllParticipants(String idStream);

    void setMuteStatus(Boolean isChecked);

    void goToStreamDataInfo(StreamModel streamModel);

    void showPhotoPicker();
}
