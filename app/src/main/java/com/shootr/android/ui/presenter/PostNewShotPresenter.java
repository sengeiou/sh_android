package com.shootr.android.ui.presenter;

import com.shootr.android.data.bus.Main;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.shot.PostNewShotAsReplyInteractor;
import com.shootr.android.domain.interactor.shot.PostNewShotInEventInteractor;
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
    private final PostNewShotInEventInteractor postNewShotInEventInteractor;
    private final PostNewShotAsReplyInteractor postNewShotAsReplyInteractor;

    private PostNewShotView postNewShotView;
    private File selectedImageFile;
    private String shotCommentToSend;
    private String currentTextWritten = "";
    private boolean isReply;
    private String replyParentId;

    @Inject
    public PostNewShotPresenter(@Main Bus bus, ErrorMessageFactory errorMessageFactory,
      PostNewShotInEventInteractor postNewShotInEventInteractor,
      PostNewShotAsReplyInteractor postNewShotAsReplyInteractor) {
        this.bus = bus;
        this.errorMessageFactory = errorMessageFactory;
        this.postNewShotInEventInteractor = postNewShotInEventInteractor;
        this.postNewShotAsReplyInteractor = postNewShotAsReplyInteractor;
    }

    protected void setView(PostNewShotView postNewShotView) {
        this.postNewShotView = postNewShotView;
    }

    public void initializeAsNewShot(PostNewShotView postNewShotView) {
        this.setView(postNewShotView);
    }

    public void initializeAsReply(PostNewShotView postNewShotView, String replyParentId, String replyToUsername) {
        this.setView(postNewShotView);
        if (replyParentId == null || replyToUsername == null) {
            throw new IllegalArgumentException(String.format("Invalid reply data: parentId=%d username=%s",
              replyParentId,
              replyToUsername));
        }
        this.isReply = true;
        this.replyParentId = replyParentId;
        postNewShotView.showReplyToUsername(replyToUsername);
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
        if (!isReply) {
            postEventShot();
        } else {
            postReplyShot();
        }
    }

    private void postReplyShot() {
        postNewShotAsReplyInteractor.postNewShotAsReply(shotCommentToSend,
          selectedImageFile,
          replyParentId,
          new Interactor.CompletedCallback() {
              @Override public void onCompleted() {
                  onShotSending();
              }
          },
          new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  onShotError();
              }
          });
    }

    private void postEventShot() {
        postNewShotInEventInteractor.postNewShotInEvent(shotCommentToSend,
          selectedImageFile,
          new Interactor.CompletedCallback() {
              @Override public void onCompleted() {
                  onShotSending();
              }
          },
          new Interactor.ErrorCallback() {
              @Override public void onError(ShootrException error) {
                  onShotError();
              }
          });
    }

    private void onShotSending() {
        postNewShotView.setResultOk();
        postNewShotView.closeScreen();
    }

    private void onShotError() {
        postNewShotView.showError(errorMessageFactory.getCommunicationErrorMessage());
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
