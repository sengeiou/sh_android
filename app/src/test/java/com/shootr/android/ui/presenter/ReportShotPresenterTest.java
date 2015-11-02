package com.shootr.android.ui.presenter;

import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.shot.DeleteShotInteractor;
import com.shootr.android.domain.interactor.user.BlockUserInteractor;
import com.shootr.android.domain.interactor.user.GetBlockedIdUsersInteractor;
import com.shootr.android.domain.interactor.user.GetFollowingInteractor;
import com.shootr.android.domain.interactor.user.UnblockUserInteractor;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.utils.DateRangeTextProvider;
import com.shootr.android.domain.utils.TimeUtils;
import com.shootr.android.ui.model.ShotModel;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.ReportShotView;
import com.shootr.android.util.ErrorMessageFactory;
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

    @Test public void shouldShowConfirmationWhenBlockUserClicked() throws Exception {
        presenter.blockUserClicked(shotModel());

        verify(reportShotView).showBlockUserConfirmation();
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
