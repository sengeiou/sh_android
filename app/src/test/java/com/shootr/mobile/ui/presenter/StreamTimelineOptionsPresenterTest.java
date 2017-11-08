package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.stream.FollowStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.GetStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.MuteInteractor;
import com.shootr.mobile.domain.interactor.stream.UnfollowStreamInteractor;
import com.shootr.mobile.domain.interactor.stream.UnmuteInteractor;
import com.shootr.mobile.ui.views.StreamTimelineOptionsView;
import com.shootr.mobile.util.ErrorMessageFactory;
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

public class StreamTimelineOptionsPresenterTest {

  private static final String STUB_STREAM_ID = "stream_id";

  @Mock FollowStreamInteractor followStreamInteractor;
  @Mock StreamTimelineOptionsView streamTimelineOptionsView;
  @Mock UnfollowStreamInteractor unfollowStreamInteractor;
  @Mock ErrorMessageFactory errorMessageFactory;
  @Mock MuteInteractor muteInteractor;
  @Mock UnmuteInteractor unmuteInteractor;
  @Mock GetStreamInteractor getStreamInteractor;

  private StreamTimelineOptionsPresenter presenter;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    presenter =
        new StreamTimelineOptionsPresenter(followStreamInteractor,
            unfollowStreamInteractor, muteInteractor, unmuteInteractor, getStreamInteractor,
            errorMessageFactory);
    presenter.setView(streamTimelineOptionsView);
  }

  @Test public void shouldHideAddToFavoritesButtonWhenAddToFavorite() throws Exception {
    setupAddToFavoriteCallbacks();

    presenter.addToFavorites();

    verify(streamTimelineOptionsView).hideAddToFavoritesButton();
  }

  @Test public void shouldShowRemoveFromFavoritesButtonWhenAddToFavorites() throws Exception {
    setupAddToFavoriteCallbacks();

    presenter.addToFavorites();

    verify(streamTimelineOptionsView).showRemoveFromFavoritesButton();
  }

  @Test public void shouldHideRemoveFromFavoritesButtonWhenRemoveFromFavorites() throws Exception {
    setupRemoveFromFavoriteCallbacks();

    presenter.removeFromFavorites();

    verify(streamTimelineOptionsView).hideRemoveFromFavoritesButton();
  }

  @Test public void shouldShowAddToFavoritesButtonWhenRemoveFromFavorites() throws Exception {
    setupRemoveFromFavoriteCallbacks();

    presenter.removeFromFavorites();

    verify(streamTimelineOptionsView).showAddToFavoritesButton();
  }

  private void setupRemoveFromFavoriteCallbacks() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        ((Interactor.CompletedCallback) invocation.getArguments()[1]).onCompleted();
        return null;
      }
    }).when(unfollowStreamInteractor)
        .unfollow(anyString(), any(Interactor.CompletedCallback.class));
  }

  private void setupAddToFavoriteCallbacks() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        ((Interactor.CompletedCallback) invocation.getArguments()[1]).onCompleted();
        return null;
      }
    }).when(followStreamInteractor)
        .follow(anyString(), any(Interactor.CompletedCallback.class),
            any(Interactor.ErrorCallback.class));
  }

}