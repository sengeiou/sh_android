package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.GetLandingStreamsInteractor;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.FollowStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.HideStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.MuteInteractor;
import com.shootr.mobile.domain.interactor.stream.ShareStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.UnfollowStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.UnmuteInteractor;
import com.shootr.mobile.domain.interactor.stream.UnwatchStreamInteractor;
import com.shootr.mobile.domain.model.HotStreams;
import com.shootr.mobile.domain.model.PromotedLandingItem;
import com.shootr.mobile.domain.model.PromotedItems;
import com.shootr.mobile.domain.model.UserStreams;
import com.shootr.mobile.domain.model.stream.LandingStreams;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.model.LandingStreamsModel;
import com.shootr.mobile.ui.model.StreamModel;
import com.shootr.mobile.ui.model.StreamResultModel;
import com.shootr.mobile.ui.model.mappers.ImageMediaModelMapper;
import com.shootr.mobile.ui.model.mappers.StreamModelMapper;
import com.shootr.mobile.ui.model.mappers.StreamResultModelMapper;
import com.shootr.mobile.ui.views.StreamsListView;
import com.shootr.mobile.util.ErrorMessageFactory;
import com.squareup.otto.Bus;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

public class StreamsListPresenterTest {

  private static final String SELECTED_STREAM_ID = "selected_stream";
  private static final String SELECTED_STREAM_TITLE = "title";
  private static final String STREAM_AUTHOR_ID = "author";
  public static final String ID_STREAM = "idStream";

  @Mock Bus bus;
  @Mock GetLandingStreamsInteractor streamsListInteractor;
  @Mock FollowStreamInteractor followStreamInteractor;
  @Mock UnwatchStreamInteractor unwatchStreamInteractor;
  @Mock ShareStreamInteractor shareStreamInteractor;
  @Mock ErrorMessageFactory errorMessageFactory;
  @Mock SessionRepository sessionRepository;
  @Mock StreamsListView streamsListView;
  @Mock UnfollowStreamInteractor unfollowStreamInteractor;
  @Mock MuteInteractor muteInteractor;
  @Mock UnmuteInteractor unmuteInterator;
  @Mock HideStreamInteractor hideStreamInteractor;

  private StreamsListPresenter presenter;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    StreamModelMapper streamModelMapper = new StreamModelMapper(sessionRepository);
    ImageMediaModelMapper imageMediaModelMapper = new ImageMediaModelMapper();
    StreamResultModelMapper streamResultModelMapper =
        new StreamResultModelMapper(streamModelMapper);
    presenter = new StreamsListPresenter(streamsListInteractor, followStreamInteractor,
        unfollowStreamInteractor, unwatchStreamInteractor, shareStreamInteractor, muteInteractor,
        hideStreamInteractor, unmuteInterator, streamResultModelMapper, imageMediaModelMapper,
        streamModelMapper, sessionRepository, errorMessageFactory, bus);
    presenter.setView(streamsListView);
  }

  @Test public void shouldNavigateToStreamTimelineWhenStreamSelected() throws Exception {
    presenter.selectStream(selectedStreamModel());

    verify(streamsListView).navigateToStreamTimeline(SELECTED_STREAM_ID, SELECTED_STREAM_TITLE,
        STREAM_AUTHOR_ID);
  }

  @Test public void shouldNavigateToStreamDetailWhenNewStreamCreated() throws Exception {
    presenter.streamCreated(SELECTED_STREAM_ID);

    verify(streamsListView).navigateToCreatedStreamDetail(SELECTED_STREAM_ID);
  }

  @Test
  public void shouldNavigateToStreamDetailWhenNewStreamCreatedIfSelectStreamInteractorCallbacksStreamId()
      throws Exception {
    presenter.streamCreated(SELECTED_STREAM_ID);

    verify(streamsListView).navigateToCreatedStreamDetail(SELECTED_STREAM_ID);
  }

  @Test public void shouldRenderStreamListWhenStreamListInteractorCallbacksResults()
      throws Exception {
    setupStreamListInteractorCallbacks(Arrays.asList(selectedStream(), selectedStream()));

    presenter.loadLandingStreams();

    verify(streamsListView).renderLanding(any(LandingStreamsModel.class));
  }

  @Test public void shouldHideLoadingWhenStreamListInteractorCallbacksResults() throws Exception {
    setupStreamListInteractorCallbacks(Collections.singletonList(selectedStream()));

    presenter.loadLandingStreams();

    verify(streamsListView).hideLoading();
  }

  @Test public void shouldShowLoadingWhenStreamListInteractorCallbacksEmpty() throws Exception {
    setupStreamListInteractorCallbacks(new ArrayList<Stream>());

    presenter.loadLandingStreams();

    verify(streamsListView).showLoading();
  }

  @Test public void shouldShowSharedStreamWhenShareStreamsCompletedCallback() throws Exception {
    setupShareStreamCompletedCallback();

    presenter.shareStream(streamModel());

    verify(streamsListView).showStreamShared();
  }

  @Test public void shouldShowErrorStreamWhenShareStreamsErrorCallback() throws Exception {
    setupShareStreamErrorCallback();

    presenter.shareStream(streamModel());

    verify(streamsListView).showError(anyString());
  }

  @Test public void shouldShowContextMenuWithMuteStreamIfStreamNotMutedWhenLongPress()
      throws Exception {
    setupMuteStreamCompletedCallback();

    presenter.onStreamLongClicked(streamModel());

    verify(streamsListView).showContextMenuWithMute(any(StreamModel.class));
  }

  @Test public void shouldShowContextMenuWithUnmuteStreamIfStreamMutedWhenLongPress()
      throws Exception {
    setupUnMuteStreamCompletedCallback();

    presenter.onStreamLongClicked(streamModelMuted());

    verify(streamsListView).showContextMenuWithUnmute(any(StreamModel.class));
  }

  @Test public void shouldCallbackMuteInteractorWhenMutePressed() throws Exception {
    presenter.onMuteClicked(streamModel());

    verify(muteInteractor).mute(anyString(), any(Interactor.CompletedCallback.class));
  }

  @Test public void shouldCallbackUnmuteInteractorWhenUnmutePressed() throws Exception {
    presenter.onUnmuteClicked(streamModel());

    verify(unmuteInterator).unmute(anyString(), any(Interactor.CompletedCallback.class));
  }

  private void setupShareStreamErrorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.ErrorCallback errorCallback =
            (Interactor.ErrorCallback) invocation.getArguments()[2];
        errorCallback.onError(new ShootrException() {
        });
        return null;
      }
    }).when(shareStreamInteractor)
        .shareStream(anyString(), any(Interactor.CompletedCallback.class), anyErrorCallback());
  }

  private void setupShareStreamCompletedCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.CompletedCallback completedCallback =
            (Interactor.CompletedCallback) invocation.getArguments()[1];
        completedCallback.onCompleted();
        return null;
      }
    }).when(shareStreamInteractor)
        .shareStream(anyString(), any(Interactor.CompletedCallback.class), anyErrorCallback());
  }

  private void setupMuteStreamCompletedCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.CompletedCallback completedCallback =
            (Interactor.CompletedCallback) invocation.getArguments()[1];
        completedCallback.onCompleted();
        return null;
      }
    }).when(muteInteractor).mute(anyString(), any(Interactor.CompletedCallback.class));
  }

  private void setupUnMuteStreamCompletedCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.CompletedCallback completedCallback =
            (Interactor.CompletedCallback) invocation.getArguments()[1];
        completedCallback.onCompleted();
        return null;
      }
    }).when(unmuteInterator).unmute(anyString(), any(Interactor.CompletedCallback.class));
  }

  private StreamModel streamModel() {
    StreamResultModel streamResultModel = new StreamResultModel();
    StreamModel streamModel = new StreamModel();
    streamModel.setIdStream(ID_STREAM);
    return streamModel;
  }

  private StreamModel streamModelMuted() {
    StreamModel streamModel = new StreamModel();
    streamModel.setIdStream(ID_STREAM);
    streamModel.setMuted(true);
    return streamModel;
  }

  private Interactor.ErrorCallback anyErrorCallback() {
    return any(Interactor.ErrorCallback.class);
  }

  private Interactor.Callback<LandingStreams> anyStreamsCallback() {
    return any(Interactor.Callback.class);
  }

  private void setupStreamListInteractorCallbacks(final List<Stream> result) {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.Callback<LandingStreams> callback =
            (Interactor.Callback<LandingStreams>) invocation.getArguments()[0];
        LandingStreams landingStreams = new LandingStreams();
        HotStreams hotStreams = new HotStreams();
        hotStreams.setStreams(new ArrayList<>(result));
        landingStreams.setHotStreams(hotStreams);
        UserStreams userStreams = new UserStreams();
        userStreams.setStreams(new ArrayList<>(result));
        landingStreams.setUserStreams(userStreams);

        PromotedItems promotedItems = new PromotedItems();
        promotedItems.setPromotedItems(new ArrayList<PromotedLandingItem>());
        landingStreams.setPromoted(promotedItems);
        callback.onLoaded(landingStreams);
        return null;
      }
    }).when(streamsListInteractor).getLandingStreams(anyStreamsCallback(), anyErrorCallback());
  }

  private StreamModel selectedStreamModel() {
    StreamModel streamModel = new StreamModel();
    streamModel.setIdStream(SELECTED_STREAM_ID);
    streamModel.setTitle(SELECTED_STREAM_TITLE);
    streamModel.setAuthorId(STREAM_AUTHOR_ID);
    return streamModel;
  }

  private Stream selectedStream() {
    Stream stream = new Stream();
    stream.setId(SELECTED_STREAM_ID);
    stream.setTitle(SELECTED_STREAM_TITLE);
    stream.setAuthorId(STREAM_AUTHOR_ID);
    return stream;
  }
}