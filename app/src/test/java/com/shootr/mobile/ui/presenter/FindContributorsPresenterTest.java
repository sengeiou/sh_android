package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.contributor.ManageContributorsInteractor;
import com.shootr.mobile.domain.interactor.user.contributor.FindContributorsInteractor;
import com.shootr.mobile.domain.utils.DateRangeTextProvider;
import com.shootr.mobile.domain.utils.StreamJoinDateFormatter;
import com.shootr.mobile.domain.utils.TimeUtils;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.FindContributorsView;
import com.shootr.mobile.util.ErrorMessageFactory;
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
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

public class FindContributorsPresenterTest {

  private static final String QUERY = "query";
  private static final String STREAM_ID = "streamId";
  @Mock FindContributorsInteractor findContributorsInteractor;
  @Mock ManageContributorsInteractor manageContributorsInteractor;
  @Mock UserModelMapper userModelMapper;
  @Mock ErrorMessageFactory errorMessageFactory;
  @Mock FindContributorsView findContributorsView;
  @Mock DateRangeTextProvider dateRangeTextProvider;
  @Mock TimeUtils timeUtils;

  private FindContributorsPresenter presenter;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    UserModelMapper userModelMapper =
        new UserModelMapper(new StreamJoinDateFormatter(dateRangeTextProvider, timeUtils));
    presenter = new FindContributorsPresenter(findContributorsInteractor,
        manageContributorsInteractor,
        userModelMapper, errorMessageFactory);
  }

  @Test public void shouldRenderContributorsWhenSearchAndContributorsListIsNotEmpty()
      throws Exception {
    setupFindContributorsCallback();

    presenter.initialize(findContributorsView, STREAM_ID);
    presenter.searchContributors(QUERY);

    verify(findContributorsView).renderContributors(anyList());
  }

  @Test public void shouldShowEmptyWhenSearchContributorsAndObtainsEmptyList() throws Exception {
    setupFindContributorsEmptyCallback();

    presenter.initialize(findContributorsView, STREAM_ID);
    presenter.searchContributors(QUERY);

    verify(findContributorsView).showEmpty();
  }

  @Test public void shouldFinishActivityWhenAddContributor() throws Exception {
    setupContributorInteractor();

    presenter.initialize(findContributorsView, STREAM_ID);
    presenter.addContributor(userModel());

    verify(findContributorsView).finishActivity();
  }

  @Test public void shouldShowErrorWhenAddContributorAndReturnError() throws Exception {
    setupAddContributorErrorCallback();

    presenter.initialize(findContributorsView, STREAM_ID);
    presenter.addContributor(userModel());

    verify(findContributorsView).showError(anyString());
  }

  @Test public void shouldShowErrorWhenFindContributorsAndCallbackThrowsError() throws Exception {
    setupFindContributorsErrorCallback();

    presenter.initialize(findContributorsView, STREAM_ID);
    presenter.searchContributors(QUERY);

    verify(findContributorsView).showError(anyString());
  }

  private void setupFindContributorsErrorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.ErrorCallback errorCallback =
            (Interactor.ErrorCallback) invocation.getArguments()[4];
        errorCallback.onError(new ShootrException() {
        });
        return null;
      }
    }).when(findContributorsInteractor)
        .findContributors(anyString(), anyString(), anyInt(), any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));
  }

  @Test public void shouldFinishActivityWhenRemoveContributor() throws Exception {
    setupContributorInteractor();

    presenter.initialize(findContributorsView, STREAM_ID);
    presenter.removeContributor(userModel());

    verify(findContributorsView).finishActivity();
  }

  @Test public void shouldShowErrorWhenRemoveContributorAndCallbackThrowsError() throws Exception {
    setupAddContributorErrorCallback();

    presenter.initialize(findContributorsView, STREAM_ID);
    presenter.removeContributor(userModel());

    verify(findContributorsView).showError(anyString());
  }

  @Test public void shouldShowAddConfirmationOnContributorClick() throws Exception {
    presenter.initialize(findContributorsView, STREAM_ID);

    presenter.onContributorClick(userModel());

    verify(findContributorsView).showAddConfirmation(any(UserModel.class));
  }

  @Test public void shouldRenderCOntributorsWhenResume() throws Exception {
    setupFindContributorsCallback();
    presenter.initialize(findContributorsView, STREAM_ID);

    presenter.pause();
    presenter.resume();

    verify(findContributorsView).renderContributors(anyList());
  }

  private void setupAddContributorErrorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.ErrorCallback errorCallback =
            (Interactor.ErrorCallback) invocation.getArguments()[4];
        errorCallback.onError(any(ShootrException.class));
        return null;
      }
    }).when(manageContributorsInteractor)
        .manageContributor(anyString(), anyString(), anyBoolean(),
            any(Interactor.CompletedCallback.class), any(Interactor.ErrorCallback.class));
  }

  private void setupContributorInteractor() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.CompletedCallback callback =
            (Interactor.CompletedCallback) invocation.getArguments()[3];
        callback.onCompleted();
        return null;
      }
    }).when(manageContributorsInteractor)
        .manageContributor(anyString(), anyString(), anyBoolean(),
            any(Interactor.CompletedCallback.class), any(Interactor.ErrorCallback.class));
  }

  private void setupFindContributorsEmptyCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.Callback<List<User>> callback =
            (Interactor.Callback<List<User>>) invocation.getArguments()[3];
        callback.onLoaded(new ArrayList<User>());
        return null;
      }
    }).when(findContributorsInteractor)
        .findContributors(anyString(), anyString(), anyInt(), any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));
  }

  private void setupFindContributorsCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.Callback<List<User>> callback =
            (Interactor.Callback<List<User>>) invocation.getArguments()[3];
        callback.onLoaded(users());
        return null;
      }
    }).when(findContributorsInteractor)
        .findContributors(anyString(), anyString(), anyInt(), any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));
  }

  private List<User> users() {
    User user = new User();
    return Collections.singletonList(user);
  }

  private UserModel userModel() {
    return new UserModel();
  }
}
