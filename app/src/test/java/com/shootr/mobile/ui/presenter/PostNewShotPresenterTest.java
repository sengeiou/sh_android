package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.PostNewPrivateMessageInteractor;
import com.shootr.mobile.domain.interactor.shot.IncrementReplyCountShotInteractor;
import com.shootr.mobile.domain.interactor.shot.PostNewShotAsReplyInteractor;
import com.shootr.mobile.domain.interactor.shot.PostNewShotInStreamInteractor;
import com.shootr.mobile.domain.interactor.user.GetMentionedPeopleInteractor;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.utils.StreamJoinDateFormatter;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.PostNewShotView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class PostNewShotPresenterTest {

  private static final String ID_USER = "idUser";
  public static final String USERNAME = "username";
  public static final String PART_OF_A_USERNAME = "@use";
  public static final String COMMENT_WITH_USERNAME = "comment @username ";
  public static final String[] WORDS = { "comment", "@username" };
  public static final Integer WORD_POSITION = 1;
  public static final String TEXT = "text";
  public static final String THIS_IS_A_TEXT_WITH_MORE_THAN_140_CHARACTERS =
      "THIS_IS_A_TEXT_WITH_MORE_THAN_140_CHARACTERS_SO_ITS_INVALID_BECAUSE_ITS_TOO_LONG_"
          + "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA";
  public static final String EMPTY_TEXT = "";
  public static final String REPLY_PARENT_ID = "reply_parent_id";
  private PostNewShotPresenter presenter;

  @Mock Bus bus;
  @Mock ErrorMessageFactory errorMessageFactory;
  @Mock GetMentionedPeopleInteractor getMentionedPeopleInteractor;
  @Mock PostNewShotInStreamInteractor postNewShotInStreamInteractor;
  @Mock PostNewShotAsReplyInteractor postNewShotAsReplyInteractor;
  @Mock StreamJoinDateFormatter streamJoinDateFormatter;
  @Mock PostNewShotView postNewShotView;
  @Mock IncrementReplyCountShotInteractor incrementReplyCountShotInteractor;
  @Mock PostNewPrivateMessageInteractor postNewMessageInteractor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    UserModelMapper userModelMapper = new UserModelMapper(streamJoinDateFormatter);
    presenter = new PostNewShotPresenter(bus, errorMessageFactory, postNewShotInStreamInteractor,
        postNewShotAsReplyInteractor, postNewMessageInteractor, getMentionedPeopleInteractor,
        incrementReplyCountShotInteractor, userModelMapper);
    presenter.setView(postNewShotView);
  }

  @Test public void shouldShowMentionSuggestionsIfPeopleObtainedWhenMentioning() throws Exception {
    setupMentionedPeopleCallback();

    presenter.autocompleteMention(USERNAME, WORDS, WORD_POSITION);

    verify(postNewShotView).showMentionSuggestions();
  }

  @Test public void shouldHideImageContainerIfPeopleObtainedWhenMentioning() throws Exception {
    setupMentionedPeopleCallback();

    presenter.autocompleteMention(USERNAME, WORDS, WORD_POSITION);

    verify(postNewShotView).hideImageContainer();
  }

  @Test public void shouldRenderMentionSuggestionsIfPeopleObtainedWhenMentioning()
      throws Exception {
    setupMentionedPeopleCallback();

    presenter.autocompleteMention(USERNAME, WORDS, WORD_POSITION);

    verify(postNewShotView).renderMentionSuggestions(anyList());
  }

  @Test public void shouldhideMentionSuggestionsIfNoPeopleObtainedWhenMentioning()
      throws Exception {
    setupNoMentionedPeopleCallback();

    presenter.autocompleteMention(USERNAME, WORDS, WORD_POSITION);

    verify(postNewShotView).hideMentionSuggestions();
  }

  @Test public void shouldMentionUserWhenMentionClicked() throws Exception {
    setupMentionedPeopleCallback();

    presenter.autocompleteMention(PART_OF_A_USERNAME, WORDS, WORD_POSITION);
    presenter.onMentionClicked(userModel());

    verify(postNewShotView).mentionUser(COMMENT_WITH_USERNAME);
  }

  @Test public void shouldHideMentionSuggestionsWhenMentionClicked() throws Exception {
    setupMentionedPeopleCallback();

    presenter.autocompleteMention(PART_OF_A_USERNAME, WORDS, WORD_POSITION);
    presenter.onMentionClicked(userModel());

    verify(postNewShotView).hideMentionSuggestions();
  }

  @Test public void shouldShowImageContainerWhenMentionClickedIfThereWasNoPictureSelected()
      throws Exception {
    setupMentionedPeopleCallback();

    presenter.autocompleteMention(PART_OF_A_USERNAME, WORDS, WORD_POSITION);
    presenter.onMentionClicked(userModel());

    verify(postNewShotView, never()).showImageContainer();
  }

  @Test public void shouldSetCursonrToEndOfTextWhenMentionClicked() throws Exception {
    setupMentionedPeopleCallback();

    presenter.autocompleteMention(PART_OF_A_USERNAME, WORDS, WORD_POSITION);
    presenter.onMentionClicked(userModel());

    verify(postNewShotView).setCursorToEndOfText();
  }

  @Test public void shouldHideMentionSuggestionsWhenStopMentioning() throws Exception {
    presenter.onStopMentioning();

    verify(postNewShotView).hideMentionSuggestions();
  }

  @Test public void shouldNotShowImageContainerWhenStopMentioningIfThereWasNoImageSelected()
      throws Exception {
    presenter.onStopMentioning();

    verify(postNewShotView, never()).showImageContainer();
  }

  @Test public void shouldUpdateCharCounterWhenWriting() throws Exception {
    presenter.textChanged(TEXT);

    verify(postNewShotView).setRemainingCharactersCount(anyInt());
  }

  @Test public void shouldSetRemainingCharactersColorValidWhenLessThanLimit() throws Exception {
    presenter.textChanged(TEXT);

    verify(postNewShotView).setRemainingCharactersColorValid();
  }

  @Test public void shouldSetRemainingCharactersColorInvalidWhenMoreThanLimit() throws Exception {
    presenter.textChanged(THIS_IS_A_TEXT_WITH_MORE_THAN_140_CHARACTERS);

    verify(postNewShotView).setRemainingCharactersColorInvalid();
  }

  @Test public void shouldUpdateSendButtonToEnableIfShotCommentIsValid() throws Exception {
    presenter.textChanged(TEXT);

    verify(postNewShotView).enableSendButton();
  }

  @Test public void shouldUpdateSendButtonToDisabledIfShotCommentIsTooLong() throws Exception {
    presenter.textChanged(EMPTY_TEXT);

    verify(postNewShotView).disableSendButton();
  }

  @Test public void shouldNavigateBackIfNoDataEntered() throws Exception {
    presenter.navigateBack();

    verify(postNewShotView).performBackPressed();
  }

  @Test public void shouldShowDiscardAlertIfDataEntered() throws Exception {
    presenter.textChanged(TEXT);
    presenter.navigateBack();

    verify(postNewShotView).showDiscardAlert();
  }

  @Test public void shouldGoBackWhenDiscardConfirmed() throws Exception {
    presenter.confirmDiscard();

    verify(postNewShotView).performBackPressed();
  }

  @Test public void shouldChoosePhotoFromGallery() throws Exception {
    presenter.choosePhotoFromGallery();

    verify(postNewShotView).choosePhotoFromGallery();
  }

  @Test public void shouldTakePhotoFromCamera() throws Exception {
    presenter.takePhotoFromCamera();

    verify(postNewShotView).takePhotoFromCamera();
  }

  @Test public void shouldHideImageViewPreviewWhenRemoveImage() throws Exception {
    presenter.removeImage();

    verify(postNewShotView).hideImagePreview();
  }

  @Test public void shouldHideKeyboardWhenSendShot() throws Exception {
    presenter.sendShot(TEXT);

    verify(postNewShotView).hideKeyboard();
  }

  @Test public void shouldPostStreamShotWhenSendShot() throws Exception {
    presenter.sendShot(TEXT);

    verify(postNewShotInStreamInteractor).postNewShotInStream(anyString(), any(File.class),
        any(Interactor.CompletedCallback.class), any(Interactor.ErrorCallback.class));
  }

  @Test public void shouldShowReplyToUsernameWhenInitializeAsAReply() throws Exception {
    presenter.initializeAsReply(postNewShotView, REPLY_PARENT_ID, TEXT);

    verify(postNewShotView).showReplyToUsername(TEXT);
  }

  @Test public void shouldPostReplyShotWhenReplyingShot() throws Exception {
    presenter.initializeAsReply(postNewShotView, REPLY_PARENT_ID, TEXT);
    presenter.sendShot(TEXT);

    verify(postNewShotAsReplyInteractor).postNewShotAsReply(anyString(), any(File.class),
        anyString(), any(Interactor.CompletedCallback.class), any(Interactor.ErrorCallback.class));
  }

  @Test public void shouldShowResultOkWhenShotInStreamSent() throws Exception {
    setupPostShotCallback();

    presenter.sendShot(TEXT);

    verify(postNewShotView).setResultOk();
  }

  @Test public void shouldShowResultOkWhenReplySent() throws Exception {
    setupReplyShotCallback();

    presenter.initializeAsReply(postNewShotView, REPLY_PARENT_ID, TEXT);
    presenter.sendShot(TEXT);

    verify(postNewShotView).setResultOk();
  }

  @Test public void shouldCloseScreenOkWhenShotInStreamSent() throws Exception {
    setupPostShotCallback();

    presenter.sendShot(TEXT);

    verify(postNewShotView).closeScreen();
  }

  @Test public void shouldCloseScreenOkWhenReplySent() throws Exception {
    setupReplyShotCallback();

    presenter.initializeAsReply(postNewShotView, REPLY_PARENT_ID, TEXT);
    presenter.sendShot(TEXT);

    verify(postNewShotView).closeScreen();
  }

  @Test public void shouldShowErrorWhenPostNewShotInStreamErrorCallback() throws Exception {
    setupPostStreamShotErrorCallback();

    presenter.sendShot(TEXT);

    verify(postNewShotView).showError(anyString());
  }

  protected void setupPostStreamShotErrorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.ErrorCallback callback = (Interactor.ErrorCallback) invocation.getArguments()[3];
        callback.onError(new ShootrException() {
        });
        return null;
      }
    }).when(postNewShotInStreamInteractor)
        .postNewShotInStream(anyString(), any(File.class), any(Interactor.CompletedCallback.class),
            any(Interactor.ErrorCallback.class));
  }

  @Test public void shouldShowResultOkWhenReplyErrorCallback() throws Exception {
    setupReplyShotErrorCallback();

    presenter.initializeAsReply(postNewShotView, REPLY_PARENT_ID, TEXT);
    presenter.sendShot(TEXT);

    verify(postNewShotView).showError(anyString());
  }

  protected void setupReplyShotErrorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.ErrorCallback callback = (Interactor.ErrorCallback) invocation.getArguments()[4];
        callback.onError(new ShootrException() {
        });
        return null;
      }
    }).when(postNewShotAsReplyInteractor)
        .postNewShotAsReply(anyString(), any(File.class), anyString(),
            any(Interactor.CompletedCallback.class), any(Interactor.ErrorCallback.class));
  }

  protected void setupReplyShotCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.CompletedCallback callback =
            (Interactor.CompletedCallback) invocation.getArguments()[3];
        callback.onCompleted();
        return null;
      }
    }).when(postNewShotAsReplyInteractor)
        .postNewShotAsReply(anyString(), any(File.class), anyString(),
            any(Interactor.CompletedCallback.class), any(Interactor.ErrorCallback.class));
  }

  protected void setupPostShotCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.CompletedCallback callback =
            (Interactor.CompletedCallback) invocation.getArguments()[2];
        callback.onCompleted();
        return null;
      }
    }).when(postNewShotInStreamInteractor)
        .postNewShotInStream(anyString(), any(File.class), any(Interactor.CompletedCallback.class),
            any(Interactor.ErrorCallback.class));
  }

  private List<User> mentionSuggestions() {
    List<User> participants = new ArrayList<>();
    participants.add(user());
    participants.add(new User());
    return participants;
  }

  private User user() {
    User user = new User();
    user.setIdUser(ID_USER);
    return user;
  }

  private UserModel userModel() {
    UserModel userModel = new UserModel();
    userModel.setIdUser(ID_USER);
    userModel.setUsername(USERNAME);
    return userModel;
  }

  public void setupMentionedPeopleCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.Callback<List<User>> callback =
            (Interactor.Callback<List<User>>) invocation.getArguments()[1];
        callback.onLoaded(mentionSuggestions());
        return null;
      }
    }).when(getMentionedPeopleInteractor)
        .obtainMentionedPeople(anyString(), any(Interactor.Callback.class));
  }

  public void setupNoMentionedPeopleCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.Callback<List<User>> callback =
            (Interactor.Callback<List<User>>) invocation.getArguments()[1];
        callback.onLoaded(Collections.<User>emptyList());
        return null;
      }
    }).when(getMentionedPeopleInteractor)
        .obtainMentionedPeople(anyString(), any(Interactor.Callback.class));
  }
}
