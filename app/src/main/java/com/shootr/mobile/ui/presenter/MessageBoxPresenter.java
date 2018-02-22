package com.shootr.mobile.ui.presenter;

import android.support.annotation.NonNull;
import com.shootr.mobile.data.bus.Main;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.PostNewPrivateMessageInteractor;
import com.shootr.mobile.domain.interactor.shot.PostNewShotInStreamInteractor;
import com.shootr.mobile.domain.interactor.user.GetMentionedPeopleInteractor;
import com.shootr.mobile.domain.model.Searchable;
import com.shootr.mobile.domain.model.SearchableType;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.MessageBoxView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
  private final UserModelMapper userModelMapper;

  private MessageBoxView messageBoxView;
  private File selectedImageFile;
  private String shotCommentToSend;
  private String currentTextWritten = "";
  private boolean isInitialized = false;
  private Integer wordPosition;
  private String[] words;
  private String idTargetUser;
  private int maxLength = MAX_LENGTH;

  @Inject public MessageBoxPresenter(@Main Bus bus, ErrorMessageFactory errorMessageFactory,
      PostNewShotInStreamInteractor postNewShotInStreamInteractor,
      PostNewPrivateMessageInteractor postNewPrivateMessageInteractor,
      GetMentionedPeopleInteractor getMentionedPeopleInteractor, UserModelMapper userModelMapper) {
    this.bus = bus;
    this.errorMessageFactory = errorMessageFactory;
    this.postNewShotInStreamInteractor = postNewShotInStreamInteractor;
    this.postNewPrivateMessageInteractor = postNewPrivateMessageInteractor;
    this.getMentionedPeopleInteractor = getMentionedPeopleInteractor;
    this.userModelMapper = userModelMapper;
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
      Timber.w("Tried to closeSocket shot empty or too big: %s", shotCommentToSend);
    }
  }

  public void sendMessage(String text) {
    messageBoxView.hideKeyboard();
    shotCommentToSend = filterText(text);

    if (canSendShot(shotCommentToSend)) {
      if (idTargetUser != null) {
        postMessage(idTargetUser);
      }
    } else {
      Timber.w("Tried to closeSocket shot empty or too big: %s", shotCommentToSend);
    }
  }

  private void postMessage(String idTargetUser) {
    postNewPrivateMessageInteractor.postNewPrivateMessage(shotCommentToSend, selectedImageFile,
        idTargetUser, new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            messageBoxView.hideSendButton();
            messageBoxView.clearTextBox();
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            onShotError();
          }
        });
  }

  public void textChanged(String currentText) {
    currentTextWritten = filterText(currentText);
    updateCharCounter(currentTextWritten);
    updateSendButonEnabled(currentTextWritten);
  }

  private void updateSendButonEnabled(String filteredText) {
    if (canSendShot(filteredText)) {
      messageBoxView.enableSendButton();
      messageBoxView.showSendButton();
    } else {
      hideOrDisableSendButton(filteredText);
    }
  }

  private void hideOrDisableSendButton(String filteredText) {
    if (filteredText.length() <= 0) {
      messageBoxView.hideSendButton();
    } else {
      messageBoxView.disableSendButton();
    }
  }

  private void updateCharCounter(String filteredText) {
    int remainingLength = maxLength - filteredText.length();
    messageBoxView.setRemainingCharactersCount(remainingLength);

    boolean isValidShot = remainingLength > 0;
    if (isValidShot) {
      messageBoxView.setRemainingCharactersColorValid();
    } else {
      messageBoxView.setRemainingCharactersColorInvalid();
    }
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
    postNewShotInStreamInteractor.postNewShotInStream(shotCommentToSend, selectedImageFile,
        new Interactor.CompletedCallback() {
          @Override public void onCompleted() {
            messageBoxView.hideSendButton();
            messageBoxView.clearTextBox();
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
            onShotError();
          }
        });
  }

  private void onShotError() {
    messageBoxView.showError(errorMessageFactory.getCommunicationErrorMessage());
  }

  public boolean isInitialized() {
    return isInitialized;
  }

  public void autocompleteMention(String username, String[] words, Integer wordPosition) {
    this.words = words;
    this.wordPosition = wordPosition;
    String extractedUsername = username.substring(1);
    if (extractedUsername.length() >= 1) {
      loadMentions(extractedUsername);
    }
  }

  public void loadMentions(String extractedUsername) {
    getMentionedPeopleInteractor.searchItems(extractedUsername,
        new Interactor.Callback<List<Searchable>>() {
          @Override public void onLoaded(List<Searchable> searchables) {
            List<UserModel> mentionSuggestions = filterMentions(searchables);
            if (!mentionSuggestions.isEmpty()) {
              Collections.sort(mentionSuggestions, new UserModel.MentionComparator());
              messageBoxView.showMentionSuggestions();
              messageBoxView.renderMentionSuggestions(mentionSuggestions);
            } else {
              messageBoxView.hideMentionSuggestions();
            }
          }
        }, new Interactor.ErrorCallback() {
          @Override public void onError(ShootrException error) {
              /* no-op */
          }
        });
  }

  @NonNull private List<UserModel> filterMentions(List<Searchable> searchables) {
    List<UserModel> mentionSuggestions = new ArrayList<>();
    for (Searchable searchable : searchables) {
      if (searchable.getSearchableType().equals(SearchableType.USER)) {
        mentionSuggestions.add(userModelMapper.transform((User) searchable));
      }
    }
    return mentionSuggestions;
  }

  public void onMentionClicked(UserModel user) {
    String shotComment = mountShotComment(user);
    messageBoxView.mentionUser(shotComment);
    messageBoxView.hideMentionSuggestions();
    messageBoxView.setCursorToEndOfText();
  }

  @NonNull public String mountShotComment(UserModel user) {
    String shotComment = "";
    Integer position = 0;
    for (String word : words) {
      if (equals(position, wordPosition)) {
        shotComment += "@" + user.getUsername() + " ";
      } else {
        shotComment += word + " ";
      }
      position++;
    }
    return shotComment;
  }

  private boolean equals(Integer a, Integer b) {
    return a != null && a.equals(b);
  }

  public void onStopMentioning() {
    messageBoxView.hideMentionSuggestions();
  }


}
