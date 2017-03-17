package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.SpyCallback;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.domain.model.stream.StreamSearchResultList;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.stream.InternalStreamSearchRepository;
import com.shootr.mobile.domain.repository.stream.StreamListSynchronizationRepository;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import com.shootr.mobile.domain.repository.stream.StreamSearchRepository;
import com.shootr.mobile.domain.repository.user.UserRepository;
import com.shootr.mobile.domain.utils.LocaleProvider;
import com.shootr.mobile.domain.utils.TimeUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StreamsListInteractorTest {

  private static final Long NOW = 10000L;
  private static final Long SECONDS_AGO_31 = NOW - (31L * 1000);
  private static final Long SECONDS_AGO_29 = NOW - (29L * 1000);
  private static final String ID_CURRENT_USER = "current_user";
  private static final String USER_ID = "userId";
  private static final String WATCHING_STREAM = "streamId";

  @Mock StreamSearchRepository remoteStreamSearchRepository;
  @Mock InternalStreamSearchRepository localStreamSearchRepository;
  @Mock StreamListSynchronizationRepository streamListSynchronizationRepository;
  @Mock SessionRepository sessionRepository;
  @Mock UserRepository localUserRepository;
  @Mock TimeUtils timeUtils;
  @Spy SpyCallback<StreamSearchResultList> spyCallback = new SpyCallback<>();
  @Mock Interactor.ErrorCallback dummyErrorCallback;
  @Mock LocaleProvider localeProvider;
  @Mock StreamRepository localStreamRepository;

  private StreamsListInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();

    interactor = new StreamsListInteractor(interactorHandler, postExecutionThread,
        remoteStreamSearchRepository, localStreamSearchRepository,
        streamListSynchronizationRepository, localStreamRepository,
        sessionRepository, localUserRepository, timeUtils, localeProvider);

    when(timeUtils.getCurrentTime()).thenReturn(NOW);
    setupLocalRepositoryReturnsCurrentUser();
  }

  @Test public void shouldCallbackTwoStreamResultsFirstWhenLocalRepositoryReturnsTwoStreamResults()
      throws Exception {
    when(localStreamSearchRepository.getDefaultStreams(anyString(), anyArray())).thenReturn(
        twoStreamResults());

    interactor.loadStreams(spyCallback, dummyErrorCallback);

    verify(spyCallback).onLoaded(any(StreamSearchResultList.class));
    assertThat(spyCallback.firstResult().getStreamSearchResults()).hasSize(2);
  }

  @Test
  public void shouldLoadRemoteStreamsWhenLastRefreshMoreThanThirtySecondsAgoIfLocalRepositoryReturnsStreams()
      throws Exception {
    when(localStreamSearchRepository.getDefaultStreams(anyString(), anyArray())).thenReturn(
        twoStreamResults());
    when(streamListSynchronizationRepository.getStreamsRefreshDate()).thenReturn(SECONDS_AGO_31);

    interactor.loadStreams(spyCallback, dummyErrorCallback);

    verify(remoteStreamSearchRepository).getDefaultStreams(anyString(), anyArray());
  }

  @Test
  public void shouldNotLoadRemoteStreamsWhenRefreshLessThanThirtySecondsAgoIfLocalRepositoryReturnsStreams()
      throws Exception {
    when(localStreamSearchRepository.getDefaultStreams(anyString(), anyArray())).thenReturn(
        twoStreamResults());
    when(streamListSynchronizationRepository.getStreamsRefreshDate()).thenReturn(SECONDS_AGO_29);

    interactor.loadStreams(spyCallback, dummyErrorCallback);

    verify(remoteStreamSearchRepository, never()).getDefaultStreams(anyString(), anyArray());
  }

  @Test
  public void shouldLoadRemoteStreamsWhenLastRefreshLessThanTirtySecondsAgoIfLocalRepositoryReturnsEmpty()
      throws Exception {
    when(localStreamSearchRepository.getDefaultStreams(anyString(), anyArray())).thenReturn(
        emptyResults());
    when(streamListSynchronizationRepository.getStreamsRefreshDate()).thenReturn(SECONDS_AGO_29);

    interactor.loadStreams(spyCallback, dummyErrorCallback);

    verify(remoteStreamSearchRepository).getDefaultStreams(anyString(), anyArray());
  }

  @Test public void shouldCallbackRemoteStreamResultsWhenRefreshed() throws Exception {
    when(localStreamSearchRepository.getDefaultStreams(anyString(), anyArray())).thenReturn(
        emptyResults());
    when(remoteStreamSearchRepository.getDefaultStreams(anyString(), anyArray())).thenReturn(
        twoStreamResults());

    interactor.loadStreams(spyCallback, dummyErrorCallback);

    verify(spyCallback).onLoaded(emptyResultList());
    verify(spyCallback).onLoaded(twoStreamResultList());
  }

  @Test public void shouldOverwriteRemoteStreamsInLocalRepositoryWhenRefreshed() throws Exception {
    when(localStreamSearchRepository.getDefaultStreams(anyString(), anyArray())).thenReturn(
        emptyResults());
    when(remoteStreamSearchRepository.getDefaultStreams(anyString(), anyArray())).thenReturn(
        twoStreamResults());

    interactor.loadStreams(spyCallback, dummyErrorCallback);

    verify(localStreamSearchRepository).deleteDefaultStreams();
    verify(localStreamSearchRepository).putDefaultStreams(twoStreamResults());
  }

  @Test public void shouldCallbackRemoteStreamResultsBeforeOverwrittingLocalRepository()
      throws Exception {
    when(localStreamSearchRepository.getDefaultStreams(anyString(), anyArray())).thenReturn(
        emptyResults());
    when(remoteStreamSearchRepository.getDefaultStreams(anyString(), anyArray())).thenReturn(
        twoStreamResults());

    interactor.loadStreams(spyCallback, dummyErrorCallback);

    InOrder inOrder = inOrder(spyCallback, localStreamSearchRepository);
    inOrder.verify(spyCallback).onLoaded(twoStreamResultList());
    inOrder.verify(localStreamSearchRepository).deleteDefaultStreams();
    inOrder.verify(localStreamSearchRepository).putDefaultStreams(twoStreamResults());
  }

  @Test public void shouldSetNowAsRefresDateWhenRemoteStreamsRefreshed() throws Exception {
    when(localStreamSearchRepository.getDefaultStreams(anyString(), anyArray())).thenReturn(
        emptyResults());
    when(remoteStreamSearchRepository.getDefaultStreams(anyString(), anyArray())).thenReturn(
        twoStreamResults());

    interactor.loadStreams(spyCallback, dummyErrorCallback);

    verify(streamListSynchronizationRepository).setStreamsRefreshDate(NOW);
  }

  @Test public void shouldNotSetRefreshDateWhenRemoteStreamsNotRefreshed() throws Exception {
    setupNotNeedRefresh();

    interactor.loadStreams(spyCallback, dummyErrorCallback);

    verify(streamListSynchronizationRepository, never()).setStreamsRefreshDate(anyLong());
  }

  @Test public void shouldNotifyErrorCallbackWhenRefreshRemoteFails() throws Exception {
    setupNeedsRefresh();
    when(remoteStreamSearchRepository.getDefaultStreams(anyString(), anyArray())).thenThrow(
        new ShootrException("test exception") {
        });

    interactor.loadStreams(spyCallback, dummyErrorCallback);

    verify(dummyErrorCallback).onError(any(ShootrException.class));
  }

  @Test public void shouldNotifyNotNullStreamSearchResultWhenLocalRepositoryReturnUser()
      throws Exception {
    when(localUserRepository.getUserById(ID_CURRENT_USER)).thenReturn(user());
    when(localStreamRepository.getStreamById(WATCHING_STREAM, StreamMode.TYPES_STREAM)).thenReturn(
        stream());
    when(localStreamSearchRepository.getDefaultStreams(anyString(), anyArray())).thenReturn(
        emptyResults());
    when(remoteStreamSearchRepository.getDefaultStreams(anyString(), anyArray())).thenReturn(
        twoStreamResults());

    interactor.loadStreams(spyCallback, dummyErrorCallback);

    verify(spyCallback, atLeastOnce()).onLoaded(any(StreamSearchResultList.class));
  }

  private void setupNeedsRefresh() {
    when(localStreamSearchRepository.getDefaultStreams(anyString(), anyArray())).thenReturn(
        emptyResults());
    when(streamListSynchronizationRepository.getStreamsRefreshDate()).thenReturn(0L);
  }

  private void setupNotNeedRefresh() {
    when(localStreamSearchRepository.getDefaultStreams(anyString(), anyArray())).thenReturn(
        twoStreamResults());
    when(streamListSynchronizationRepository.getStreamsRefreshDate()).thenReturn(NOW);
  }

  private List<StreamSearchResult> twoStreamResults() {
    return Arrays.asList(streamResult(), streamResult());
  }

  private StreamSearchResultList twoStreamResultList() {
    return new StreamSearchResultList(twoStreamResults());
  }

  private List<StreamSearchResult> emptyResults() {
    return Collections.emptyList();
  }

  private StreamSearchResultList emptyResultList() {
    return new StreamSearchResultList(emptyResults());
  }

  private StreamSearchResult streamResult() {
    StreamSearchResult result = new StreamSearchResult();
    result.setStream(new Stream());
    return result;
  }

 private void setupLocalRepositoryReturnsCurrentUser() {
    when(sessionRepository.getCurrentUserId()).thenReturn(ID_CURRENT_USER);
    when(localUserRepository.getUserById(ID_CURRENT_USER)).thenReturn(currentUser());
  }

  private User currentUser() {
    return new User();
  }

  private User user() {
    User user = new User();
    user.setIdUser(USER_ID);
    user.setIdWatchingStream(WATCHING_STREAM);
    return user;
  }

  private Stream stream() {
    return new Stream();
  }

  private String[] anyArray() {
    return any(String[].class);
  }
}