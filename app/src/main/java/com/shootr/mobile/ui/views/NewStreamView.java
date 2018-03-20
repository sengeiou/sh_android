package com.shootr.mobile.ui.views;

import com.shootr.mobile.ui.views.base.DataTransferView;
import java.io.File;

public interface NewStreamView extends DataTransferView {

    void setStreamTitle(String title);

    void showTitleError(String errorMessage);

    void showVideoUrlError();

    void closeScreenWithResult(String streamId);

    void doneButtonEnabled(boolean enable);

    void hideKeyboard();

    void showNotificationConfirmation();

    void showDescription(String description);

    void showVideoUrl(String videoUrl);

    void setModeValue(Integer readWriteMode);

    void showPhotoOptions();

    void showPhotoPicker();

    void zoomPhoto(String picture);

    void setStreamPhoto(String picture, String title);

    void loadDefaultPhoto();

    void showPhotoSelected(File photoFile);

    void goToShareStream(String id);

    void showEditPhotoPlaceHolder();

    void hideVideoUrlFields();
}
