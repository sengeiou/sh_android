package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.AddToFavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.ChangeStreamPhotoInteractor;
import com.shootr.mobile.domain.interactor.stream.GetFavoriteStatusInteractor;
import com.shootr.mobile.domain.interactor.stream.GetStreamInfoInteractor;
import com.shootr.mobile.domain.interactor.stream.MuteInteractor;
import com.shootr.mobile.domain.interactor.stream.RemoveFromFavoritesInteractor;
import com.shootr.mobile.domain.interactor.stream.RemoveStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.RestoreStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.SelectStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.ShareStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.UnmuteInteractor;
import com.shootr.mobile.domain.interactor.user.FollowInteractor;
import com.shootr.mobile.domain.interactor.user.UnfollowInteractor;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamInfo;
import com.shootr.mobile.domain.model.user.Contributor;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.utils.DateRangeTextProvider;
import com.shootr.mobile.domain.utils.StreamJoinDateFormatter;
import com.shootr.mobile.domain.utils.TimeUtils;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.mappers.StreamModelMapper;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.StreamDetailView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StreamDetailPresenterTest {

  public static final int FIFTY_PLUS_WATCHERS = 55;
  private static final String SELECTED_STREAM_ID = "selected_stream";
  private static final String SELECTED_STREAM_TITLE = "title";
  private static final String STREAM_AUTHOR_ID = "author";
  private static final String ID_USER = "id_user";
  public static final int THREE_WATCHERS = 3;
  public static final String ID_STREAM = "id_stream";
  public static final int NO_WATCHERS = 0;
  public static final long CONTRIBUTORS_COUNT = 0;
  public static final String PICTURE_URL = "picture_url";

  private StreamDetailPresenter presenter;
  @Mock GetStreamInfoInteractor streamInfoInteractor;
  @Mock ChangeStreamPhotoInteractor changeStreamPhotoInteractor;
  @Mock ShareStreamInteractor shareStreamInteractor;
  @Mock FollowInteractor followInteractor;
  @Mock UnfollowInteractor unfollowInteractor;
  @Mock ErrorMessageFactory errorMessageFactory;
  @Mock SessionRepository sessionRepository;
  @Mock DateRangeTextProvider dateRangeTextProvider;
  @Mock TimeUtils timeUtils;
  @Mock StreamDetailView streamDetailView;
  @Mock SelectStreamInteractor selectStreamInteractor;
  @Mock MuteInteractor muteInteractor;
  @Mock UnmuteInteractor unmuteInteractor;
  @Mock RemoveStreamInteractor removeStreamInteractor;
  @Mock RestoreStreamInteractor restoreStreamInteractor;
  @Mock GetFavoriteStatusInteractor getFavoriteStatusInteractor;
  @Mock AddToFavoritesInteractor addToFavoritesInteractor;
  @Mock RemoveFromFavoritesInteractor removeFromFavoritesInteractor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    StreamModelMapper streamModelMapper = new StreamModelMapper(sessionRepository);
    UserModelMapper userModelMapper =
        new UserModelMapper(new StreamJoinDateFormatter(dateRangeTextProvider, timeUtils));
    presenter =
        new StreamDetailPresenter(streamInfoInteractor, shareStreamInteractor, followInteractor,
            unfollowInteractor, getFavoriteStatusInteractor, selectStreamInteractor, muteInteractor,
            unmuteInteractor, removeStreamInteractor, restoreStreamInteractor,
            addToFavoritesInteractor, removeFromFavoritesInteractor, streamModelMapper,
            userModelMapper, errorMessageFactory);
    presenter.setView(streamDetailView);
  }

  @Test public void shouldShowTotalWatchers() throws Exception {
    setupStreamInfoCallback();

    presenter.initialize(streamDetailView, ID_STREAM);

    verify(streamDetailView).setFollowingNumber(anyInt(), anyInt());
  }

  @Test public void shouldShowTotalWatchersFromStreamInfoIfMoreThan50() throws Exception {
    setupStreamInfoWith50PlusParticipantsCallback();

    presenter.initialize(streamDetailView, ID_STREAM);

    verify(streamDetailView).setFollowingNumber(0, FIFTY_PLUS_WATCHERS);
  }

  @Test public void shouldZoomPhotoIfIAmNotAuthorAndPhotoNotNull() throws Exception {
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
    setupStreamInfoCallbackWithPhoto();

    presenter.initialize(streamDetailView, ID_STREAM);
    presenter.photoClick();

    verify(streamDetailView).zoomPhoto(PICTURE_URL);
  }

  @Test public void shouldNotZoomPhotoIfIAmNotAuthorAndPhotoNull() throws Exception {
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
    setupStreamWithoutPictureInfoCallback();

    presenter.initialize(streamDetailView, ID_STREAM);
    presenter.photoClick();

    verify(streamDetailView, never()).zoomPhoto(PICTURE_URL);
  }

  @Test public void shouldNeverSelectStreamIfNoStreamInOnResume() throws Exception {
    presenter.resume();

    verify(selectStreamInteractor, never()).selectStream(anyString(),
        any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
  }

  @Test public void shouldGoToStreamDetailOnToolbarClicked() throws Exception {
    presenter.setStreamModel(streamModel());
    presenter.dataInfoClicked();

    verify(streamDetailView).goToStreamDataInfo(any(StreamModel.class));
  }

  @Test public void shouldGoToContributorsActivityAsHolderOncontributorsClicked() throws Exception {
    when(sessionRepository.getCurrentUserId()).thenReturn(STREAM_AUTHOR_ID);
    setupStreamInfoCallback();
    presenter.initialize(streamDetailView, ID_STREAM);

    presenter.contributorsClicked();

    verify(streamDetailView).goToContributorsActivityAsHolder(anyString());
  }

  @Test public void shouldGoToContributorsActivityOncontributorsClicked() throws Exception {
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
    setupStreamInfoCallback();
    presenter.initialize(streamDetailView, ID_STREAM);

    presenter.contributorsClicked();

    verify(streamDetailView).goToContributorsActivity(anyString());
  }

  @Test public void shouldShowContributorsNumberWhenContributorsListSizeIsMoreThanZero()
      throws Exception {
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
    setupStreamInfoCallback();

    presenter.initialize(streamDetailView, ID_STREAM);

    verify(streamDetailView).showContributorsNumber(anyInt(), anyBoolean());
  }

  @Test public void shouldHideContributorsNumberWhenContributorsListSizeIsZero() throws Exception {
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
    setupStreamInfoWithoutContributorsCallback();

    presenter.initialize(streamDetailView, ID_STREAM);

    verify(streamDetailView).hideContributorsNumber(anyBoolean());
  }

  @Test public void shouldShowRemoveStreamIfStreamNotRemoved() throws Exception {
    when(sessionRepository.getCurrentUserId()).thenReturn(STREAM_AUTHOR_ID);
    setupStreamInfoCallback();

    presenter.initialize(streamDetailView, ID_STREAM);

    verify(streamDetailView).showRemoveStreamButton();
  }

  @Test public void shouldShowRestoreStreamIfStreamRemoved() throws Exception {
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_USER);
    setupRemovedStreamInfoCallback();

    presenter.initialize(streamDetailView, ID_STREAM);

    verify(streamDetailView).showRestoreStreamButton();
  }

  @Test public void shouldShowRemoveStreamConfirmationWhenRemoveClicked() throws Exception {
    setupRemovedStreamInfoCallback();

    presenter.removeStream();

    verify(streamDetailView).askRemoveStreamConfirmation();
  }

  @Test public void shouldSetStreamPictureIfExists() throws Exception {
    presenter.onStreamInfoLoaded(streamInfoWithPhoto());

    verify(streamDetailView).setStreamPicture(anyString());
  }

  @Test public void shouldSetupStreamInitialsIfNotStreamPicture() throws Exception {
    presenter.onStreamInfoLoaded(streamInfoWithoutPhoto());

    verify(streamDetailView).setupStreamInitials(any(StreamModel.class));
  }

  @Test public void shouldNotSetupStreamInitialsIfNotStreamPictureAndImAuthor() throws Exception {
    when(sessionRepository.getCurrentUserId()).thenReturn(STREAM_AUTHOR_ID);
    setupStreamInfoCallback();

    presenter.onStreamInfoLoaded(streamInfoWithoutPhoto());

    verify(streamDetailView, never()).setupStreamInitials(any(StreamModel.class));
  }

  @Test public void shouldShowPictureIfNotStreamPictureAndImAuthor() throws Exception {
    when(sessionRepository.getCurrentUserId()).thenReturn(STREAM_AUTHOR_ID);
    setupStreamInfoCallback();

    presenter.onStreamInfoLoaded(streamInfoWithoutPhoto());

    verify(streamDetailView).showPicture();
  }

  @Test public void shouldNavigateToEditStream() throws Exception {
    presenter.editStreamInfo();

    verify(streamDetailView).navigateToEditStream(anyString());
  }

  @Test public void shouldShowErrorWhenStreamInfoInteractorErrorCallback() throws Exception {
    setupStreamInfoErrorCallback();

    presenter.getStreamInfo();

    verify(streamDetailView).showError(anyString());
  }

  private void setupRemovedStreamInfoCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        GetStreamInfoInteractor.Callback callback =
            (GetStreamInfoInteractor.Callback) invocation.getArguments()[1];
        callback.onLoaded(removedStreamInfo());
        return null;
      }
    }).when(streamInfoInteractor)
        .obtainStreamInfo(anyString(),
            (GetStreamInfoInteractor.Callback) any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));
  }

  private void setupStreamWithoutPictureInfoCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        GetStreamInfoInteractor.Callback callback =
            (GetStreamInfoInteractor.Callback) invocation.getArguments()[1];
        callback.onLoaded(streamInfoWithoutPhoto());
        return null;
      }
    }).when(streamInfoInteractor)
        .obtainStreamInfo(anyString(),
            (GetStreamInfoInteractor.Callback) any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));
  }

  private void setupStreamInfoCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        GetStreamInfoInteractor.Callback callback =
            (GetStreamInfoInteractor.Callback) invocation.getArguments()[1];
        callback.onLoaded(streamInfoWith3Participants());
        return null;
      }
    }).when(streamInfoInteractor)
        .obtainStreamInfo(anyString(),
            (GetStreamInfoInteractor.Callback) any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));
  }

  private void setupStreamInfoWithoutContributorsCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        GetStreamInfoInteractor.Callback callback =
            (GetStreamInfoInteractor.Callback) invocation.getArguments()[1];
        StreamInfo streamInfo = streamInfoWith3Participants();
        streamInfo.getStream().setContributorCount(0L);
        callback.onLoaded(streamInfo);
        return null;
      }
    }).when(streamInfoInteractor)
        .obtainStreamInfo(anyString(),
            (GetStreamInfoInteractor.Callback) any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));
  }

  public void setupStreamInfoCallbackWithPhoto() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        GetStreamInfoInteractor.Callback callback =
            (GetStreamInfoInteractor.Callback) invocation.getArguments()[1];
        callback.onLoaded(streamInfoWithPhoto());
        return null;
      }
    }).when(streamInfoInteractor)
        .obtainStreamInfo(anyString(),
            (GetStreamInfoInteractor.Callback) any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));
  }

  public void setupStreamInfoWith50PlusParticipantsCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        GetStreamInfoInteractor.Callback callback =
            (GetStreamInfoInteractor.Callback) invocation.getArguments()[1];
        callback.onLoaded(streamInfoWith50plusParticipants());
        return null;
      }
    }).when(streamInfoInteractor)
        .obtainStreamInfo(anyString(),
            (GetStreamInfoInteractor.Callback) any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));
  }

  public void setupStreamInfoErrorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        GetStreamInfoInteractor.ErrorCallback callback =
            (GetStreamInfoInteractor.ErrorCallback) invocation.getArguments()[2];
        callback.onError(new ShootrException() {
        });
        return null;
      }
    }).when(streamInfoInteractor)
        .obtainStreamInfo(anyString(),
            (GetStreamInfoInteractor.Callback) any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));
  }

  private StreamInfo streamInfoWith50plusParticipants() {
    return StreamInfo.builder()
        .stream(streamWith50plusParticipants())
        .watchers(moreThan50watchers())
        .currentUserWatching(new User())
        .numberOfFollowing(NO_WATCHERS)
        .hasMoreParticipants(true)
        .isDataComplete(true)
        .build();
  }

  private StreamInfo streamInfoWith3Participants() {
    return StreamInfo.builder()
        .stream(stream())
        .watchers(watchers())
        .currentUserWatching(new User())
        .numberOfFollowing(NO_WATCHERS)
        .hasMoreParticipants(false)
        .isDataComplete(true)
        .build();
  }

  private StreamInfo removedStreamInfo() {
    Stream stream = stream();
    stream.setRemoved(true);
    stream.setAuthorId(ID_USER);
    return StreamInfo.builder()
        .stream(stream)
        .watchers(watchers())
        .currentUserWatching(new User())
        .numberOfFollowing(NO_WATCHERS)
        .hasMoreParticipants(false)
        .isDataComplete(true)
        .build();
  }

  private StreamInfo streamInfoWithPhoto() {
    return StreamInfo.builder()
        .stream(streamWithPhoto())
        .watchers(watchers())
        .currentUserWatching(new User())
        .numberOfFollowing(NO_WATCHERS)
        .hasMoreParticipants(false)
        .isDataComplete(true)
        .build();
  }

  private StreamInfo streamInfoWithoutPhoto() {
    return StreamInfo.builder()
        .stream(streamWithoutPhoto())
        .watchers(watchers())
        .currentUserWatching(new User())
        .numberOfFollowing(NO_WATCHERS)
        .hasMoreParticipants(false)
        .isDataComplete(true)
        .build();
  }

  private List<User> watchers() {
    List<User> participants = new ArrayList<>();
    participants.add(user());
    participants.add(new User());
    participants.add(new User());
    return participants;
  }

  private List<User> moreThan50watchers() {
    List<User> participants = new ArrayList<>();
    participants.add(user());
    for (int i = 0; i < 50; i++) {
      participants.add(new User());
    }
    return participants;
  }

  private User user() {
    User user = new User();
    user.setIdUser(ID_USER);
    return user;
  }

  private Stream stream() {
    Stream stream = new Stream();
    stream.setId(SELECTED_STREAM_ID);
    stream.setTitle(SELECTED_STREAM_TITLE);
    stream.setAuthorId(STREAM_AUTHOR_ID);
    stream.setTotalWatchers(NO_WATCHERS);
    stream.setTotalFavorites(THREE_WATCHERS);
    stream.setPicture(PICTURE_URL);
    stream.setContributorCount(1L);
    return stream;
  }

  private StreamModel streamModel() {
    StreamModel streamModel = new StreamModel();
    streamModel.setIdStream(SELECTED_STREAM_ID);
    streamModel.setTitle(SELECTED_STREAM_TITLE);
    streamModel.setAuthorId(STREAM_AUTHOR_ID);
    streamModel.setTotalFavorites(THREE_WATCHERS);
    streamModel.setTotalWatchers(NO_WATCHERS);
    streamModel.setPicture(PICTURE_URL);
    streamModel.setContributorCount(CONTRIBUTORS_COUNT);
    return streamModel;
  }

  private Stream streamWithPhoto() {
    Stream stream = new Stream();
    stream.setId(SELECTED_STREAM_ID);
    stream.setTitle(SELECTED_STREAM_TITLE);
    stream.setAuthorId(STREAM_AUTHOR_ID);
    stream.setTotalWatchers(NO_WATCHERS);
    stream.setTotalFavorites(THREE_WATCHERS);
    stream.setPicture(PICTURE_URL);
    stream.setContributorCount(CONTRIBUTORS_COUNT);
    return stream;
  }

  private Stream streamWithoutPhoto() {
    Stream stream = new Stream();
    stream.setId(SELECTED_STREAM_ID);
    stream.setTitle(SELECTED_STREAM_TITLE);
    stream.setAuthorId(STREAM_AUTHOR_ID);
    stream.setTotalFavorites(THREE_WATCHERS);
    stream.setTotalWatchers(NO_WATCHERS);
    stream.setContributorCount(CONTRIBUTORS_COUNT);
    return stream;
  }

  private Stream streamWith50plusParticipants() {
    Stream stream = new Stream();
    stream.setId(SELECTED_STREAM_ID);
    stream.setTitle(SELECTED_STREAM_TITLE);
    stream.setAuthorId(STREAM_AUTHOR_ID);
    stream.setTotalFavorites(THREE_WATCHERS);
    stream.setTotalWatchers(FIFTY_PLUS_WATCHERS);
    stream.setContributorCount(CONTRIBUTORS_COUNT);
    return stream;
  }

  private List<Contributor> contributorList() {
    List<Contributor> contributors = new ArrayList<>();
    Contributor contributor = new Contributor();
    contributor.setIdStream(ID_STREAM);
    contributor.setUser(user());
    contributor.setIdUser(ID_USER);
    contributors.add(contributor);
    return contributors;
  }
}
