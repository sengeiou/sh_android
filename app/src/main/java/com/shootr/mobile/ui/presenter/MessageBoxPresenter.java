package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.PostNewPrivateMessageInteractor;
import com.shootr.mobile.domain.interactor.shot.PostNewShotInStreamInteractor;
import com.shootr.mobile.domain.interactor.user.GetMentionedPeopleInteractor;
import com.shootr.mobile.ui.views.MessageBoxView;
import com.shootr.mobile.ui.views.PostNewShotView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import java.io.File;
import javax.inject.Inject;
import timber.log.Timber;

public class MessageBoxPresenter {

  private static final int MAX_LENGTH = 140;
  private static final int MAX_MESSAGE_LENGTH = 5000;

  private final Bus bus;
  private final ErrorMessageFactory errorMessageFactory;
  private final PostNewShotInStreamInteractor postNewShotInStreamInteractor;
  private final PostNewPrivateMessageInteractor postNewPrivateMessageInteractor;
  private final GetMentionedPeopleInteractor getMentionedPeopleInteractor;

  private MessageBoxView messageBoxView;
  private File selectedImageFile;
  private String shotCommentToSend;
  private String currentTextWritten = "";
  private boolean isReply;
  private String replyParentId;
  private boolean isInitialized = false;
  private Integer wordPosition;
  private String[] words;
  private String idTargetUser;
  private int maxLength = MAX_LENGTH;

  @Inject public MessageBoxPresenter(@Main Bus bus, ErrorMessageFactory errorMessageFactory,
      PostNewShotInStreamInteractor postNewShotInStreamInteractor,
      PostNewPrivateMessageInteractor postNewPrivateMessageInteractor,
      GetMentionedPeopleInteractor getMentionedPeopleInteractor) {
    this.bus = bus;
    this.errorMessageFactory = errorMessageFactory;
    this.postNewShotInStreamInteractor = postNewShotInStreamInteractor;
    this.postNewPrivateMessageInteractor = postNewPrivateMessageInteractor;
    this.getMentionedPeopleInteractor = getMentionedPeopleInteractor;
  }

  protected void setView(MessageBoxView postNewShotView) {
    this.messageBoxView = postNewShotView;
    this.isInitialized = true;
  }

  public void initializeAsNewShot(MessageBoxView postNewShotView) {
    this.setView(postNewShotView);
  }

  public void initializeAsNewMessage(MessageBoxView postNewShotView, String idTargetUser) {
    this.setView(postNewShotView);
    this.idTargetUser = idTargetUser;
    this.maxLength = MAX_MESSAGE_LENGTH;
  }

  public void sendShot(String text) {
    messageBoxView.hideKeyboard();
    shotCommentToSend = filterText(text);

    if (canSendShot(shotCommentToSend)) {
      postStreamShot();
    } else {
      Timber.w("Tried to send shot empty or too big: %s", shotCommentToSend);
    }
  }

  public void sendMessage(String text) {
    messageBoxView.hideKeyboard();
    shotCommentToSend = filterText(text);

    if (canSendShot(shotCommentToSend)) {
      if (idTargetUser != null) {
        //postMessage(idTargetUser);
      }
    } else {
      Timber.w("Tried to send shot empty or too big: %s", shotCommentToSend);
    }
  }

  private void postMessage(String idTargetUser) {
    postNewPrivateMessageInteractor.postNewPrivateMessage(shotCommentToSend,
        selectedImageFile, idTargetUser,
        new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            //TODO
          }
        },
        new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            onShotError();
          }
        });
  }


  private String filterText(String originalText) {
    String trimmed = originalText.trim();
    while (trimmed.contains("\n\n\n")) {
      trimmed = trimmed.replace("\n\n\n", "\n\n");
    }
    return trimmed;
  }

  private boolean canSendShot(String filteredText) {
    return (isNotEmptyAndLessThanMaxLenght(filteredText));
  }

  private boolean isNotEmptyAndLessThanMaxLenght(String text) {
    return text.length() > 0 && isLessThanMaxLength(text);
  }

  private boolean isLessThanMaxLength(String text) {
    return text.length() <= maxLength;
  }

  private void postStreamShot() {
    postNewShotInStreamInteractor.postNewShotInStream(shotCommentToSend,
        selectedImageFile,
        new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            //TODO
          }
        },
        new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            onShotError();
          }
        });
  }

  private void onShotError() {
    messageBoxView.showError(errorMessageFactory.getCommunicationErrorMessage());
  }



}
