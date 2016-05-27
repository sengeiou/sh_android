package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.SuggestedPeople;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.FollowInteractor;
import com.shootr.mobile.domain.interactor.user.GetSuggestedPeopleInteractor;
import com.shootr.mobile.domain.interactor.user.UnfollowInteractor;
import com.shootr.mobile.domain.utils.DateRangeTextProvider;
import com.shootr.mobile.domain.utils.StreamJoinDateFormatter;
import com.shootr.mobile.domain.utils.TimeUtils;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.SuggestedPeopleView;
import com.shootr.mobile.util.ErrorMessageFactory;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

public class SuggestedPeoplePresenterTest {

  private static final String USER_ID = "userId";
  @Mock GetSuggestedPeopleInteractor getSuggestedPeopleInteractor;
  @Mock FollowInteractor followInteractor;
  @Mock UnfollowInteractor unfollowInteractor;
  @Mock ErrorMessageFactory errorMessageFactory;
  @Mock SuggestedPeopleView suggestedPeopleView;
  @Mock DateRangeTextProvider dateRangeTextProvider;
  @Mock TimeUtils timeUtils;

  private SuggestedPeoplePresenter presenter;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    UserModelMapper userModelMapper =
        new UserModelMapper(new StreamJoinDateFormatter(dateRangeTextProvider, timeUtils));
    presenter = new SuggestedPeoplePresenter(getSuggestedPeopleInteractor, followInteractor,
        unfollowInteractor, userModelMapper, errorMessageFactory);
    presenter.setView(suggestedPeopleView);
  }

  @Test public void shouldRenderSuggestedPeopleWhenObtainSuggestedPeople() throws Exception {
    setupGetSuggestedPeopleCallback();

    presenter.initialize(suggestedPeopleView);

    verify(suggestedPeopleView).renderSuggestedPeopleList(anyList());
  }

  @Test public void shouldShowErrorWhenObtainSuggestedPeopleThrowsError() throws Exception {
    setupSugestedPeopleErrorCallback();

    presenter.initialize(suggestedPeopleView);

    verify(suggestedPeopleView).showError(anyString());
  }

  @Test public void shouldRefreshSuggestedPeopleWhenFollowUser() throws Exception {
    setupFollowInteractorCallback();
    presenter.setSuggestedPeople(Collections.singletonList(userModel()));

    presenter.followUser(userModel());

    verify(suggestedPeopleView).refreshSuggestedPeople(anyList());
  }

  @Test public void shouldShowErrorWhenFollowUserAndThrowError() throws Exception {
    setupFollowInteractorErrorCallback();

    presenter.followUser(userModel());

    verify(suggestedPeopleView).showError(anyString());
  }

  @Test public void shouldRefreshSuggestedPeopleWhenUnfollowUser() throws Exception {
    setupUnfollowInteractorCallback();
    presenter.setSuggestedPeople(Collections.singletonList(userModel()));

    presenter.unfollowUser(userModel());

    verify(suggestedPeopleView).refreshSuggestedPeople(anyList());
  }

  @Test public void shouldRenderSuggestedPeopleWhenResume() throws Exception {
    setupGetSuggestedPeopleCallback();
    presenter.pause();

    presenter.resume();

    verify(suggestedPeopleView).renderSuggestedPeopleList(anyList());
  }

  private void setupUnfollowInteractorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.CompletedCallback completedCallback =
            (Interactor.CompletedCallback) invocation.getArguments()[1];
        completedCallback.onCompleted();
        return null;
      }
    }).when(unfollowInteractor).unfollow(anyString(), any(Interactor.CompletedCallback.class));
  }

  private void setupFollowInteractorErrorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.ErrorCallback errorCallback =
            (Interactor.ErrorCallback) invocation.getArguments()[2];
        errorCallback.onError(any(ShootrException.class));
        return null;
      }
    }).when(followInteractor)
        .follow(anyString(), any(Interactor.CompletedCallback.class),
            any(Interactor.ErrorCallback.class));
  }

  private void setupFollowInteractorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.CompletedCallback completedCallback =
            (Interactor.CompletedCallback) invocation.getArguments()[1];
        completedCallback.onCompleted();
        return null;
      }
    }).when(followInteractor)
        .follow(anyString(), any(Interactor.CompletedCallback.class),
            any(Interactor.ErrorCallback.class));
  }

  private void setupSugestedPeopleErrorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.ErrorCallback errorCallback =
            (Interactor.ErrorCallback) invocation.getArguments()[1];
        errorCallback.onError(any(ShootrException.class));
        return null;
      }
    }).when(getSuggestedPeopleInteractor)
        .loadSuggestedPeople(any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
  }

  private void setupGetSuggestedPeopleCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.Callback<List<SuggestedPeople>> callback =
            (Interactor.Callback<List<SuggestedPeople>>) invocation.getArguments()[0];
        callback.onLoaded(suggestedPeople());
        return null;
      }
    }).when(getSuggestedPeopleInteractor)
        .loadSuggestedPeople(any(Interactor.Callback.class), any(Interactor.ErrorCallback.class));
  }

  private List<SuggestedPeople> suggestedPeople() {
    SuggestedPeople suggestedPeople = new SuggestedPeople();
    suggestedPeople.setUser(user());
    return Collections.singletonList(suggestedPeople);
  }

  private User user() {
    User user = new User();
    user.setIdUser(USER_ID);

    return user;
  }

  private UserModel userModel() {
    UserModel userModel = new UserModel();
    userModel.setIdUser(USER_ID);
    return userModel;
  }
}
