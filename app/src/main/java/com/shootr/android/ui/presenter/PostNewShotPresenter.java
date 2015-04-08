package com.shootr.android.ui.presenter;

import com.shootr.android.data.bus.Main;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.shot.PostNewShotInteractor;
import com.shootr.android.task.events.CommunicationErrorEvent;
import com.shootr.android.task.events.ConnectionNotAvailableEvent;
import com.shootr.android.ui.views.PostNewShotView;
import com.shootr.android.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.io.File;
import javax.inject.Inject;
import timber.log.Timber;

public class PostNewShotPresenter implements Presenter {

    private static final int MAX_LENGTH = 140;

    private final Bus bus;
    private final ErrorMessageFactory errorMessageFactory;
    private final PostNewShotInteractor postNewShotInteractor;

    private PostNewShotView postNewShotView;
    private String placeholder;
    private File selectedImageFile;
    private String shotCommentToSend;
    private String currentTextWritten;

    @Inject public PostNewShotPresenter(@Main Bus bus, ErrorMessageFactory errorMessageFactory, PostNewShotInteractor postNewShotInteractor) {
        this.bus = bus;
        this.errorMessageFactory = errorMessageFactory;
        this.postNewShotInteractor = postNewShotInteractor;
    }

    public void initialize(PostNewShotView postNewShotView) {
        initialize(postNewShotView, null);
    }

    public void initialize(PostNewShotView postNewShotView, String placeholder) {
        this.postNewShotView = postNewShotView;
        this.placeholder = placeholder;
        if (placeholder != null) {
            postNewShotView.setPlaceholder(placeholder);
        }
    }

    public void textChanged(String currentText) {
        currentTextWritten = filterText(currentText);
        updateCharCounter(currentTextWritten);
        updateSendButonEnabled(currentTextWritten);
    }

    public void choosePhotoFromGallery() {
        postNewShotView.choosePhotoFromGallery();
    }

    public void takePhotoFromCamera() {
        postNewShotView.takePhotoFromCamera();
    }

    public void selectImage(File selectedImageFile) {
        if (selectedImageFile != null && selectedImageFile.exists()) {
            postNewShotView.showImagePreview(selectedImageFile);
            this.selectedImageFile = selectedImageFile;
            updateSendButonEnabled(currentTextWritten);
        } else {
            Timber.w("Tried to set invalid file as image: %s", selectedImageFile);
        }
    }

    public void removeImage() {
        selectedImageFile = null;
        postNewShotView.hideImagePreview();
        updateSendButonEnabled(currentTextWritten);
    }

    public void sendShot(String text) {
        postNewShotView.hideKeyboard();
        shotCommentToSend = filterText(text);

        if (canSendShot(shotCommentToSend)) {
            this.showLoading();
            startSendingShot();
        } else {
            Timber.w("Tried to send shot empty or too big: %s", shotCommentToSend);
        }
    }

    private boolean canSendShot(String filteredText) {
        return (hasImage() && isLessThanMaxLength(filteredText)) || isNotEmptyAndLessThanMaxLenght(filteredText);
    }

    private boolean hasImage() {
        return selectedImageFile != null;
    }

    private void startSendingShot() {
        postNewShotInteractor.postNewShot(shotCommentToSend, selectedImageFile, new PostNewShotInteractor.Callback() {
            @Override public void onLoaded() {
                postNewShotView.setResultOk();
                postNewShotView.closeScreen();
            }
        }, new Interactor.ErrorCallback() {
            @Override public void onError(ShootrException error) {
                postNewShotView.showError(errorMessageFactory.getCommunicationErrorMessage());
            }
        });
    }

    private void updateCharCounter(String filteredText) {
        int remainingLength = MAX_LENGTH - filteredText.length();
        postNewShotView.setRemainingCharactersCount(remainingLength);

        boolean isValidShot = remainingLength > 0;
        if (isValidShot) {
            postNewShotView.setRemainingCharactersColorValid();
        } else {
            postNewShotView.setRemainingCharactersColorInvalid();
        }
    }

    private void updateSendButonEnabled(String filteredText) {
        if (canSendShot(filteredText)) {
            postNewShotView.enableSendButton();
        } else {
            postNewShotView.disableSendButton();
        }
    }

    private boolean isNotEmptyAndLessThanMaxLenght(String text) {
        return text.length() > 0 && isLessThanMaxLength(text);
    }

    private boolean isLessThanMaxLength(String text) {
        return text.length() <= MAX_LENGTH;
    }

    private String filterText(String originalText) {
        String trimmed = originalText.trim();
        while (trimmed.contains("\n\n\n")) {
            trimmed = trimmed.replace("\n\n\n", "\n\n");
        }
        return trimmed;
    }

    @Subscribe
    public void onCommunicationError(CommunicationErrorEvent event) {
        this.hideLoading();
        postNewShotView.showError(errorMessageFactory.getCommunicationErrorMessage());
    }

    @Subscribe
    public void onConnectionNotAvailable(ConnectionNotAvailableEvent event) {
        this.hideLoading();
        postNewShotView.showError(errorMessageFactory.getConnectionNotAvailableMessage());
    }

    public void navigateBack() {
        if (hasEnteredData()) {
            postNewShotView.showDiscardAlert();
        } else {
            postNewShotView.performBackPressed();
        }
    }

    public void confirmDiscard() {
        postNewShotView.performBackPressed();
    }

    private boolean hasEnteredData() {
        return ((currentTextWritten != null) && !currentTextWritten.isEmpty()) || (selectedImageFile != null);
    }


    private void showLoading() {
        postNewShotView.showLoading();
        postNewShotView.hideSendButton();
        postNewShotView.disableSendButton();
    }

    private void hideLoading() {
        postNewShotView.hideLoading();
        postNewShotView.enableSendButton();
        postNewShotView.showSendButton();
    }

    @Override public void resume() {
        bus.register(this);
    }

    @Override public void pause() {
        bus.unregister(this);
    }

    public File getSelectedImageFile() {
        return selectedImageFile;
    }
}
