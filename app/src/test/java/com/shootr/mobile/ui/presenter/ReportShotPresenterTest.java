package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.shot.DeleteShotInteractor;
import com.shootr.mobile.domain.interactor.user.BlockUserInteractor;
import com.shootr.mobile.domain.interactor.user.GetBlockedIdUsersInteractor;
import com.shootr.mobile.domain.interactor.user.GetFollowingInteractor;
import com.shootr.mobile.domain.interactor.user.UnblockUserInteractor;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.utils.DateRangeTextProvider;
import com.shootr.mobile.domain.utils.TimeUtils;
import com.shootr.mobile.ui.model.ShotModel;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.ReportShotView;
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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

public class ReportShotPresenterTest {

    public static final String ID_USER = "idUser";
    @Mock ReportShotView reportShotView;
    @Mock DateRangeTextProvider dateRangeTextProvider;
    @Mock TimeUtils timeUtils;
    @Mock DeleteShotInteractor getAllParticipantsInteractor;
    @Mock ErrorMessageFactory selectStreamInteractor;
    @Mock SessionRepository followInteractor;
    @Mock UserModelMapper unfollowInteractor;
    @Mock GetBlockedIdUsersInteractor errorMessageFactory;
    @Mock BlockUserInteractor blockUserInteractor;
    @Mock UnblockUserInteractor unblockUserInteractor;
    @Mock GetFollowingInteractor getFollowingInteractor;

    private ReportShotPresenter presenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new ReportShotPresenter(getAllParticipantsInteractor, selectStreamInteractor, followInteractor, unfollowInteractor, errorMessageFactory, blockUserInteractor, unblockUserInteractor, getFollowingInteractor);
        presenter.setView(reportShotView);
    }

    @Test public void shouldShowConfirmationWhenBlockUserClickedAndNotFollowing() throws Exception {
        setupGetFollowingCallbacksNotFollowingUser();

        presenter.blockUserClicked(shotModel());

        verify(reportShotView).showBlockUserConfirmation();
    }

    @Test public void shouldShowAlertWhenBlockUserClickedAndFollowing() throws Exception {
        setupGetFollowingCallbacksFollowingUser();

        presenter.blockUserClicked(shotModel());

        verify(reportShotView).showBlockFollowingUserAlert();
    }

    @Test public void shouldShowUserBlockedWhenConfirmsBlockUserAndCallbackCompleted() throws Exception {
        setupUserBlockedCallback();

        presenter.blockUser(ID_USER);

        verify(reportShotView).showUserBlocked();
    }

    @Test public void shouldShowErrorWhenConfirmsBlockUserAndErrorCallback() throws Exception {
        setupUserBlockedErrorCallback();

        presenter.blockUser(ID_USER);

        verify(reportShotView).showError(anyString());
    }

    private void setupGetFollowingCallbacksFollowingUser() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<User>> callback =
                  (Interactor.Callback<List<User>>) invocation.getArguments()[0];
                callback.onLoaded(userFollowingList());
                return null;
            }
        }).when(getFollowingInteractor).obtainPeople(any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));
    }

    private void setupGetFollowingCallbacksNotFollowingUser() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<List<User>> callback =
                  (Interactor.Callback<List<User>>) invocation.getArguments()[0];
                callback.onLoaded(userList());
                return null;
            }
        }).when(getFollowingInteractor).obtainPeople(any(Interactor.Callback.class),
          any(Interactor.ErrorCallback.class));
    }

    private List<User> userFollowingList() {
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setIdUser(ID_USER);
        users.add(user);
        return users;
    }

    private List<User> userList() {
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setIdUser("another_id");
        users.add(user);
        return users;
    }

    private void setupUserBlockedErrorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.ErrorCallback callback =
                  (Interactor.ErrorCallback) invocation.getArguments()[2];
                callback.onError(new ShootrException() {});
                return null;
            }
        }).when(blockUserInteractor).block(anyString(),
          any(Interactor.CompletedCallback.class),
          any(Interactor.ErrorCallback.class));
    }

    private void setupUserBlockedCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.CompletedCallback callback =
                  (Interactor.CompletedCallback) invocation.getArguments()[1];
                callback.onCompleted();
                return null;
            }
        }).when(blockUserInteractor).block(anyString(),
          any(Interactor.CompletedCallback.class),
          any(Interactor.ErrorCallback.class));
    }

    private ShotModel shotModel() {
        ShotModel shotModel = new ShotModel();
        shotModel.setIdUser(ID_USER);
        return shotModel;
    }
}