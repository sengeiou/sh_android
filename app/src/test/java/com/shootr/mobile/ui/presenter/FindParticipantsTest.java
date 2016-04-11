package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.FindParticipantsInteractor;
import com.shootr.mobile.domain.interactor.user.FollowInteractor;
import com.shootr.mobile.domain.interactor.user.UnfollowInteractor;
import com.shootr.mobile.domain.utils.DateRangeTextProvider;
import com.shootr.mobile.domain.utils.StreamJoinDateFormatter;
import com.shootr.mobile.domain.utils.TimeUtils;
import com.shootr.mobile.ui.model.UserModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.FindParticipantsView;
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
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

public class FindParticipantsTest {

    public static final String QUERY = "query";
    public static final String ID_USER = "idUser";

    @Mock FindParticipantsInteractor findParticipantsInteractor;
    @Mock FollowInteractor followInteractor;
    @Mock UnfollowInteractor unfollowInteractor;
    @Mock ErrorMessageFactory errorMessageFactory;

    @Mock FindParticipantsView findParticipantsView;
    @Mock DateRangeTextProvider dateRangeTextProvider;
    @Mock TimeUtils timeUtils;

    private FindParticipantsPresenter presenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        UserModelMapper userModelMapper =
          new UserModelMapper(new StreamJoinDateFormatter(dateRangeTextProvider, timeUtils));
        presenter = new FindParticipantsPresenter(findParticipantsInteractor,
          followInteractor,
          unfollowInteractor,
          userModelMapper,
          errorMessageFactory);
        presenter.setView(findParticipantsView);
    }

    @Test public void shouldHideEmptyWhenSearchParticipants() throws Exception {
        presenter.searchParticipants(QUERY);

        verify(findParticipantsView).hideEmpty();
    }

    @Test public void shouldHideKeyboardWhenSearchParticipants() throws Exception {
        presenter.searchParticipants(QUERY);

        verify(findParticipantsView).hideKeyboard();
    }

    @Test public void shouldSetCurrentQueryWhenSearchParticipants() throws Exception {
        presenter.searchParticipants(QUERY);

        verify(findParticipantsView).setCurrentQuery(QUERY);
    }

    @Test public void shouldRenderParticipantsWhenSearchParticipants() throws Exception {
        setupFindParticipantsCallback();

        presenter.searchParticipants(QUERY);

        verify(findParticipantsView).renderParticipants(anyList());
    }

    @Test public void shouldShowEmptyWhenSearchParticipantsReturnsEmpty() throws Exception {
        setupFindParticipantsEmptyCallback();

        presenter.searchParticipants(QUERY);

        verify(findParticipantsView).showEmpty();
    }

    @Test public void shouldShowErrorWhenSearchParticipantsThrowsError() throws Exception {
        setupFindParticipantsErrorCallback();

        presenter.searchParticipants(QUERY);

        verify(findParticipantsView).showError(anyString());
    }

    @Test public void shouldRenderParticipantsWhenRefreshParticipants() throws Exception {
        setupFindParticipantsCallback();

        presenter.refreshParticipants();

        verify(findParticipantsView).renderParticipants(anyList());
    }

    @Test public void shouldShowErrorWhenRefreshParticipantsThrowsError() throws Exception {
        setupFindParticipantsErrorCallback();

        presenter.refreshParticipants();

        verify(findParticipantsView).showError(anyString());
    }

    @Test public void shouldRenderParticipantsWhenFollowAParticipant() throws Exception {
        setupFindParticipantsCallback();
        setupFollowUserCallback();

        presenter.searchParticipants(QUERY);
        presenter.followUser(userModel());

        verify(findParticipantsView, atLeastOnce()).renderParticipants(anyList());
    }

    @Test public void shouldRenderParticipantsWhenUnfollowAParticipant() throws Exception {
        setupFindParticipantsCallback();
        setupFollowUserCallback();

        presenter.searchParticipants(QUERY);
        presenter.unfollowUser(userModel());

        verify(findParticipantsView, atLeastOnce()).renderParticipants(anyList());
    }

    private UserModel userModel() {
        UserModel userModel = new UserModel();
        userModel.setIdUser(ID_USER);
        return userModel;
    }

    private void setupFindParticipantsCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<User>> callback =
                  (Interactor.Callback<List<User>>) invocation.getArguments()[2];
                callback.onLoaded(participants());
                return null;
            }
        }).when(findParticipantsInteractor)
          .obtainAllParticipants(anyString(),
            anyString(),
            any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));
    }

    private void setupFindParticipantsEmptyCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<User>> callback =
                  (Interactor.Callback<List<User>>) invocation.getArguments()[2];
                callback.onLoaded(Collections.EMPTY_LIST);
                return null;
            }
        }).when(findParticipantsInteractor)
          .obtainAllParticipants(anyString(),
            anyString(),
            any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));
    }

    private void setupFindParticipantsErrorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.ErrorCallback errorCallback = (Interactor.ErrorCallback) invocation.getArguments()[3];
                errorCallback.onError(new ShootrException() {
                });
                return null;
            }
        }).when(findParticipantsInteractor)
          .obtainAllParticipants(anyString(),
            anyString(),
            any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));
    }

    private void setupFollowUserCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.CompletedCallback callback = (Interactor.CompletedCallback) invocation.getArguments()[1];
                callback.onCompleted();
                return null;
            }
        }).when(followInteractor)
          .follow(anyString(), any(Interactor.CompletedCallback.class), any(Interactor.ErrorCallback.class));
    }

    private List<User> participants() {
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
}
