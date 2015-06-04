package com.shootr.android.ui.presenter;

import com.shootr.android.domain.ForgotPasswordResult;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.ResetPasswordInteractor;
import com.shootr.android.ui.model.ForgotPasswordUserModel;
import com.shootr.android.ui.model.mappers.ForgotPasswordUserModelMapper;
import com.shootr.android.ui.views.ResetPasswordRequestView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class ResetPasswordRequestPresenterTest {

    private static final String STUB_USERNAME_OR_EMAIL = "username_or_email";
    private static final String EMPTY = "";
    private static final String WHITESPACES = "   ";

    @Mock ResetPasswordInteractor resetPasswordInteractor;
    @Mock ResetPasswordRequestView resetPasswordRequestView;

    private ResetPasswordRequestPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ForgotPasswordUserModelMapper forgotPasswordMapper = new ForgotPasswordUserModelMapper();
        presenter = new ResetPasswordRequestPresenter(resetPasswordInteractor, forgotPasswordMapper);
        presenter.setView(resetPasswordRequestView);
    }

    //region Next button
    @Test
    public void shouldDisableNextButtonWhenInitialized() throws Exception {
        presenter.initialize(resetPasswordRequestView);

        verify(resetPasswordRequestView).disableNextButton();
    }

    @Test
    public void shouldEnableNextButtonWhenUsernameOrEmailChangedIfHasText() throws Exception {
        presenter.onUsernameOrEmailChanged(STUB_USERNAME_OR_EMAIL);

        verify(resetPasswordRequestView).enableNextButton();
    }

    @Test
    public void shouldNotEnableNextButtonWhenUsernameOrEmailChangedIfHasEmptyText() throws Exception {
        presenter.onUsernameOrEmailChanged(EMPTY);

        verify(resetPasswordRequestView, never()).enableNextButton();
    }

    @Test
    public void shouldNotEnableNextButtonWhenUsernameOrEmailChangedIfHasWhitespacesOnly() throws Exception {
        presenter.onUsernameOrEmailChanged(WHITESPACES);

        verify(resetPasswordRequestView, never()).enableNextButton();
    }

    @Test
    public void shouldDisableNextButtonWhenUsernameOrEmailChangedIfHasEmptyText() throws Exception {
        presenter.onUsernameOrEmailChanged(EMPTY);

        verify(resetPasswordRequestView).disableNextButton();
    }

    @Test
    public void shouldDisableNextButtonWhenUsernameOrEmailChangedIfHasWhitespacesOnly() throws Exception {
        presenter.onUsernameOrEmailChanged(WHITESPACES);

        verify(resetPasswordRequestView).disableNextButton();
    }
    //endregion

    @Test
    public void shouldNavigateToConfirmationWhenNextIfUserFound() throws Exception {
        setupResetPasswordInteractorCallbacksResult();

        presenter.next();

        verify(resetPasswordRequestView).navigateToResetPasswordConfirmation(any(ForgotPasswordUserModel.class));
    }

    protected void setupResetPasswordInteractorCallbacksResult() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.Callback<ForgotPasswordResult> callback =
                  (Interactor.Callback<ForgotPasswordResult>) invocation.getArguments()[1];
                callback.onLoaded(dummyResult());
                return null;
            }
        }).when(resetPasswordInteractor)
          .attempResetPassword(anyString(), anyCallback(), anyErrorCallback());
    }

    protected ForgotPasswordResult dummyResult() {
        return new ForgotPasswordResult();
    }

    protected Interactor.Callback<ForgotPasswordResult> anyCallback() {
        return any(Interactor.Callback.class);
    }

    protected Interactor.ErrorCallback anyErrorCallback() {
        return any(Interactor.ErrorCallback.class);
    }
}