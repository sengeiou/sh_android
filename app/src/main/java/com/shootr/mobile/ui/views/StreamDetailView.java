package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.views.base.DataTransferView;
import java.util.List;

public interface StreamDetailView extends DataTransferView {

    void setStreamTitle(String title);

    void setStreamVerified(boolean isVerified);

    void setStreamAuthor(String author);

    void setStream(StreamModel streamModel);

    void setStreamPicture(String picture);

    void showEditPicturePlaceholder();

    void zoomPhoto(String picture);

    void setWatchers(List<UserModel> watchers);

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

    void setFollowingStream(Boolean isFollowing);

    void goToContributorsActivityAsHolder(String idStream);

    void goToContributorsActivity(String idStream);

    void hideContributorsNumber(boolean isAuthorStream);

    void showContributorsNumber(Integer contributorsNumber, boolean isAuthorStream);

    void showRestoreStreamButton();

    void showRemoveStreamButton();

    void askRemoveStreamConfirmation();

    void hideRestoreButton();

    void hideRemoveButton();

    void showRestoreStreamFeedback();

    void showRemovedFeedback();

    void setupStreamInitials(StreamModel streamModel);

    void loadBlurStreamPicture(String picture);

    void showPicture();

    void hideNoTextPicture();

    void hidePicture();

    void showNoTextPicture();

    void showStreamFollower(int streamFollowers);
}
