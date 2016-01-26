package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.shot.PostNewShotAsReplyInteractor;
import com.shootr.mobile.domain.interactor.shot.PostNewShotInStreamInteractor;
import com.shootr.mobile.domain.interactor.user.GetMentionedPeopleInteractor;
import com.shootr.mobile.task.events.CommunicationErrorEvent;
import com.shootr.mobile.task.events.ConnectionNotAvailableEvent;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.PostNewShotView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import timber.log.Timber;

public class PostNewShotPresenter implements Presenter {

    private static final int MAX_LENGTH = 140;

    private final Bus bus;
    private final ErrorMessageFactory errorMessageFactory;
    private final PostNewShotInStreamInteractor postNewShotInStreamInteractor;
    private final PostNewShotAsReplyInteractor postNewShotAsReplyInteractor;
    private final GetMentionedPeopleInteractor getMentionedPeopleInteractor;
    private final UserModelMapper userModelMapper;

    private PostNewShotView postNewShotView;
    private File selectedImageFile;
    private String shotCommentToSend;
    private String currentTextWritten = "";
    private boolean isReply;
    private String replyParentId;
    private boolean isInitialized = false;
    private String mentionedUser;

    @Inject
    public PostNewShotPresenter(@Main Bus bus, ErrorMessageFactory errorMessageFactory,
      PostNewShotInStreamInteractor postNewShotInStreamInteractor,
      PostNewShotAsReplyInteractor postNewShotAsReplyInteractor,
      GetMentionedPeopleInteractor getMentionedPeopleInteractor, UserModelMapper userModelMapper) {
        this.bus = bus;
        this.errorMessageFactory = errorMessageFactory;
        this.postNewShotInStreamInteractor = postNewShotInStreamInteractor;
        this.postNewShotAsReplyInteractor = postNewShotAsReplyInteractor;
        this.getMentionedPeopleInteractor = getMentionedPeopleInteractor;
        this.userModelMapper = userModelMapper;
    }

    protected void setView(PostNewShotView postNewShotView) {
        this.postNewShotView = postNewShotView;
        this.isInitialized = true;
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
            postStreamShot();
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

    private void postStreamShot() {
        postNewShotInStreamInteractor.postNewShotInStream(shotCommentToSend,
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
        this.hideLoading();
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

    public boolean isInitialized() {
        return isInitialized;
    }

    public File getSelectedImageFile() {
        return selectedImageFile;
    }

    public void autocompleteMention(String username) {
        this.mentionedUser = username;
        getMentionedPeopleInteractor.obtainMentionedPeople(username, new Interactor.Callback<List<User>>() {
            @Override public void onLoaded(List<User> users) {
                List<UserModel> mentionSuggestions = userModelMapper.transform(users);
                if (!mentionSuggestions.isEmpty()) {
                    postNewShotView.showMentionSuggestions();
                    postNewShotView.renderMentionSuggestions(mentionSuggestions);
                } else {
                    postNewShotView.hideMentionSuggestions();
                }
            }
        });
    }

    public void onMentionClicked(UserModel user, String comment) {
        Pattern pattern = Pattern.compile(mentionedUser);
        Matcher matcher = pattern.matcher(comment);
        String substring = comment;
        if (matcher.find()) {
            int termsStart = matcher.start();
            substring = comment.substring(0, termsStart - 1) + "@" +user.getUsername() + " ";
        }
        postNewShotView.mentionUser(substring);
        postNewShotView.hideMentionSuggestions();
        postNewShotView.setCursorToEndOfText();
    }

    public void onStopMentioning() {
        postNewShotView.hideMentionSuggestions();
    }

    @Override public void resume() {
        bus.register(this);
    }

    @Override public void pause() {
        bus.unregister(this);
    }
}
