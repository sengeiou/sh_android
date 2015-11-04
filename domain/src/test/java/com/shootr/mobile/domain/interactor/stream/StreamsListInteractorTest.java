package com.shootr.mobile.domain.interactor.stream;

import com.shootr.mobile.domain.StreamSearchResultList;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.SpyCallback;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
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
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StreamsListInteractorTest {

    private static final Long NOW = 10000L;
    private static final Long SECONDS_AGO_31 = NOW - (31L * 1000);
    private static final Long SECONDS_AGO_29 = NOW - (29L * 1000);
    private static final String ID_CURRENT_USER = "current_user";

    @Mock com.shootr.mobile.domain.repository.StreamSearchRepository remoteStreamSearchRepository;
    @Mock com.shootr.mobile.domain.repository.StreamSearchRepository localStreamSearchRepository;
    @Mock com.shootr.mobile.domain.repository.StreamListSynchronizationRepository streamListSynchronizationRepository;
    @Mock com.shootr.mobile.domain.repository.SessionRepository sessionRepository;
    @Mock com.shootr.mobile.domain.repository.UserRepository localUserRepository;
    @Mock com.shootr.mobile.domain.utils.TimeUtils timeUtils;
    @Spy SpyCallback<StreamSearchResultList> spyCallback = new SpyCallback<>();
    @Mock com.shootr.mobile.domain.interactor.Interactor.ErrorCallback dummyErrorCallback;
    @Mock com.shootr.mobile.domain.utils.LocaleProvider localeProvider;
    @Mock com.shootr.mobile.domain.repository.StreamRepository localStreamRepository;
    @Mock com.shootr.mobile.domain.repository.WatchersRepository watchersRepository;

    private StreamsListInteractor interactor;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler = new TestInteractorHandler();
        com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread = new TestPostExecutionThread();

        interactor = new StreamsListInteractor(interactorHandler,
          postExecutionThread, remoteStreamSearchRepository, localStreamSearchRepository,
          streamListSynchronizationRepository, localStreamRepository,
          watchersRepository,
          sessionRepository,
          localUserRepository,
          timeUtils,
          localeProvider);

        when(timeUtils.getCurrentTime()).thenReturn(NOW);
        setupLocalRepositoryReturnsCurrentUser();
    }

    @Test public void shouldCallbackTwoStreamResultsFirstWhenLocalRepositoryReturnsTwoStreamResults() throws Exception {
        when(localStreamSearchRepository.getDefaultStreams(anyString())).thenReturn(twoStreamResults());

        interactor.loadStreams(spyCallback, dummyErrorCallback);

        verify(spyCallback).onLoaded(any(com.shootr.mobile.domain.StreamSearchResultList.class));
        assertThat(spyCallback.firstResult().getStreamSearchResults()).hasSize(2);
    }

    @Test public void shouldLoadRemoteStreamsWhenLastRefreshMoreThanThirtySecondsAgoIfLocalRepositoryReturnsStreams()
      throws Exception {
        when(localStreamSearchRepository.getDefaultStreams(anyString())).thenReturn(twoStreamResults());
        when(streamListSynchronizationRepository.getStreamsRefreshDate()).thenReturn(SECONDS_AGO_31);

        interactor.loadStreams(spyCallback, dummyErrorCallback);

        verify(remoteStreamSearchRepository).getDefaultStreams(anyString());
    }

    @Test public void shouldNotLoadRemoteStreamsWhenRefreshLessThanThirtySecondsAgoIfLocalRepositoryReturnsStreams()
      throws Exception {
        when(localStreamSearchRepository.getDefaultStreams(anyString())).thenReturn(twoStreamResults());
        when(streamListSynchronizationRepository.getStreamsRefreshDate()).thenReturn(SECONDS_AGO_29);

        interactor.loadStreams(spyCallback, dummyErrorCallback);

        verify(remoteStreamSearchRepository, never()).getDefaultStreams(anyString());
    }

    @Test public void shouldLoadRemoteStreamsWhenLastRefreshLessThanTirtySecondsAgoIfLocalRepositoryReturnsEmpty()
      throws Exception {
        when(localStreamSearchRepository.getDefaultStreams(anyString())).thenReturn(emptyResults());
        when(streamListSynchronizationRepository.getStreamsRefreshDate()).thenReturn(SECONDS_AGO_29);

        interactor.loadStreams(spyCallback, dummyErrorCallback);

        verify(remoteStreamSearchRepository).getDefaultStreams(anyString());
    }

    @Test public void shouldCallbackRemoteStreamResultsWhenRefreshed() throws Exception {
        when(localStreamSearchRepository.getDefaultStreams(anyString())).thenReturn(emptyResults());
        when(remoteStreamSearchRepository.getDefaultStreams(anyString())).thenReturn(twoStreamResults());

        interactor.loadStreams(spyCallback, dummyErrorCallback);

        verify(spyCallback).onLoaded(emptyResultList());
        verify(spyCallback).onLoaded(twoStreamResultList());
    }

    @Test public void shouldOverwriteRemoteStreamsInLocalRepositoryWhenRefreshed() throws Exception {
        when(localStreamSearchRepository.getDefaultStreams(anyString())).thenReturn(emptyResults());
        when(remoteStreamSearchRepository.getDefaultStreams(anyString())).thenReturn(twoStreamResults());

        interactor.loadStreams(spyCallback, dummyErrorCallback);

        verify(localStreamSearchRepository).deleteDefaultStreams();
        verify(localStreamSearchRepository).putDefaultStreams(twoStreamResults());
    }

    @Test public void shouldCallbackRemoteStreamResultsBeforeOverwrittingLocalRepository() throws Exception {
        when(localStreamSearchRepository.getDefaultStreams(anyString())).thenReturn(emptyResults());
        when(remoteStreamSearchRepository.getDefaultStreams(anyString())).thenReturn(twoStreamResults());

        interactor.loadStreams(spyCallback, dummyErrorCallback);

        InOrder inOrder = inOrder(spyCallback, localStreamSearchRepository);
        inOrder.verify(spyCallback).onLoaded(twoStreamResultList());
        inOrder.verify(localStreamSearchRepository).deleteDefaultStreams();
        inOrder.verify(localStreamSearchRepository).putDefaultStreams(twoStreamResults());
    }

    @Test public void shouldSetNowAsRefresDateWhenRemoteStreamsRefreshed() throws Exception {
        when(localStreamSearchRepository.getDefaultStreams(anyString())).thenReturn(emptyResults());
        when(remoteStreamSearchRepository.getDefaultStreams(anyString())).thenReturn(twoStreamResults());

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
        when(remoteStreamSearchRepository.getDefaultStreams(anyString())).thenThrow(new com.shootr.mobile.domain.exception.ShootrException("test exception") {});

        interactor.loadStreams(spyCallback, dummyErrorCallback);

        verify(dummyErrorCallback).onError(any(com.shootr.mobile.domain.exception.ShootrException.class));
    }

    private void setupNeedsRefresh() {
        when(localStreamSearchRepository.getDefaultStreams(anyString())).thenReturn(emptyResults());
        when(streamListSynchronizationRepository.getStreamsRefreshDate()).thenReturn(0L);
    }

    private void setupNotNeedRefresh() {
        when(localStreamSearchRepository.getDefaultStreams(anyString())).thenReturn(twoStreamResults());
        when(streamListSynchronizationRepository.getStreamsRefreshDate()).thenReturn(NOW);
    }

    private List<com.shootr.mobile.domain.StreamSearchResult> twoStreamResults() {
        return Arrays.asList(streamResult(), streamResult());
    }

    private com.shootr.mobile.domain.StreamSearchResultList twoStreamResultList() {
        return new com.shootr.mobile.domain.StreamSearchResultList(twoStreamResults());
    }

    private List<com.shootr.mobile.domain.StreamSearchResult> emptyResults() {
        return Collections.emptyList();
    }

    private com.shootr.mobile.domain.StreamSearchResultList emptyResultList() {
        return new com.shootr.mobile.domain.StreamSearchResultList(emptyResults());
    }

    private com.shootr.mobile.domain.StreamSearchResult streamResult() {
        com.shootr.mobile.domain.StreamSearchResult result = new com.shootr.mobile.domain.StreamSearchResult();
        result.setStream(new com.shootr.mobile.domain.Stream());
        return result;
    }

    private void setupLocalRepositoryReturnsCurrentUser() {
        when(sessionRepository.getCurrentUserId()).thenReturn(ID_CURRENT_USER);
        when(localUserRepository.getUserById(ID_CURRENT_USER)).thenReturn(currentUser());
    }

    private com.shootr.mobile.domain.User currentUser() {
        return new com.shootr.mobile.domain.User();
    }
}