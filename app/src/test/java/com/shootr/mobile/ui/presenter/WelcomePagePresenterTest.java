package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.GetLandingStreamsInteractor;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.model.stream.LandingStreams;
import com.shootr.mobile.ui.views.WelcomePageView;
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

public class WelcomePagePresenterTest {

  @Mock GetLandingStreamsInteractor streamsListInteractor;
  @Mock ErrorMessageFactory errorMessageFactory;
  @Mock WelcomePageView welcomePageView;

  private WelcomePagePresenter presenter;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    presenter = new WelcomePagePresenter(streamsListInteractor, errorMessageFactory);
    presenter.setView(welcomePageView);
  }

  @Test public void shouldGoToStreamListWhenLoadDefaultStreams() throws Exception {
    setupStreamsListInteractorCallback();
    presenter.getStartedClicked();

    presenter.initialize(welcomePageView);

    verify(welcomePageView).goToStreamList();
  }

  @Test public void shouldShowErrorWhenStreamsListInteractorThrowsError() throws Exception {
    setupStreamsListInteractorErrorCallback();
    presenter.getStartedClicked();

    presenter.initialize(welcomePageView);

    verify(welcomePageView).showError(anyString());
  }

  @Test public void shouldGoToStreamListWhenGetStartedClickedAndStreamsAreLoaded()
      throws Exception {
    setupStreamsListInteractorCallback();
    presenter.initialize(welcomePageView);

    presenter.getStartedClicked();

    verify(welcomePageView).goToStreamList();
  }

  private void setupStreamsListInteractorErrorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.ErrorCallback errorCallback =
            (Interactor.ErrorCallback) invocation.getArguments()[1];
        errorCallback.onError(any(ShootrException.class));
        return null;
      }
    }).when(streamsListInteractor)
        .getLandingStreams(any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
  }

  private void setupStreamsListInteractorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.Callback<LandingStreams> callback =
            (Interactor.Callback<LandingStreams>) invocation.getArguments()[0];
        callback.onLoaded(new LandingStreams());
        return null;
      }
    }).when(streamsListInteractor)
        .getLandingStreams(any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
  }
}
