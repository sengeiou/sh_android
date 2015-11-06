package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.ForgotPasswordResult;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.ResetPasswordInteractor;
import com.shootr.mobile.domain.service.ResetPasswordException;
import com.shootr.mobile.ui.model.ForgotPasswordUserModel;
import com.shootr.mobile.ui.model.mappers.ForgotPasswordUserModelMapper;
import com.shootr.mobile.ui.views.ResetPasswordRequestView;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class ResetPasswordRequestPresenterTest {

    private static final String STUB_USERNAME_OR_EMAIL = "username_or_email";
    private static final String EMPTY = "";
    private static final String WHITESPACES = "   ";

    @Mock ResetPasswordInteractor resetPasswordInteractor;
    @Mock ResetPasswordRequestView resetPasswordRequestView;
    @Mock ErrorMessageFactory errorMessageFactory;

    private ResetPasswordRequestPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ForgotPasswordUserModelMapper forgotPasswordMapper = new ForgotPasswordUserModelMapper();
        presenter = new ResetPasswordRequestPresenter(resetPasswordInteractor, forgotPasswordMapper,
          errorMessageFactory);
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

    //region Next action

    @Test
    public void shouldHideKeyboardWhenNext() throws Exception {
        presenter.next();

        verify(resetPasswordRequestView).hideKeyboard();
    }

    @Test
    public void shouldNavigateToConfirmationWhenNextIfInteractorCallbacksResult() throws Exception {
        setupResetPasswordInteractorCallbacksResult();

        presenter.next();

        verify(resetPasswordRequestView).navigateToResetPasswordConfirmation(any(ForgotPasswordUserModel.class));
    }

    @Test
    public void shouldShowLoadingWhenNext() throws Exception {
        presenter.next();

        verify(resetPasswordRequestView).showLoading();
    }

    @Test
    public void shouldHideLoadingWhenNextIfCallbacksException() throws Exception {
        setupResetPasswordInteractorCallbacksShootrException();

        presenter.next();

        verify(resetPasswordRequestView).hideLoading();
    }

    @Test
    public void shouldDisableNextButtonWhenNext() throws Exception {
        presenter.next();

        verify(resetPasswordRequestView).disableNextButton();
    }

    @Test
    public void shouldEnableNextButtonWhenNextIfInteractorCallbackResetPasswordException() throws Exception {
        setupResetPasswordInteractorCallbacksResetPasswordException();

        presenter.next();

        verify(resetPasswordRequestView).enableNextButton();
    }

    @Test
    public void shouldEnableNextButtonWhenNextIfInteractorCallbackShootrException() throws Exception {
        setupResetPasswordInteractorCallbacksShootrException();

        presenter.next();

        verify(resetPasswordRequestView).enableNextButton();
    }
    //endregion

    //region Errors
    @Test
    public void shouldShowResetPasswordErrorWhenNextIfInteractorCallbacksResetPasswordException() throws Exception {
        setupResetPasswordInteractorCallbacksResetPasswordException();

        presenter.next();

        verify(resetPasswordRequestView).showResetPasswordError();
    }

    @Test
    public void shouldShowErrorWhenNextIfInteractorCallbacksException() throws Exception {
        setupResetPasswordInteractorCallbacksShootrException();

        presenter.next();

        verify(resetPasswordRequestView).showError(anyString());
    }
    //endregion

    private void setupResetPasswordInteractorCallbacksResetPasswordException() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((Interactor.ErrorCallback) invocation.getArguments()[2]).onError(new ResetPasswordException("test"));
                return null;
            }
        }).when(resetPasswordInteractor).attempResetPassword(anyString(), anyCallback(), anyErrorCallback());
    }

    private void setupResetPasswordInteractorCallbacksShootrException() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((Interactor.ErrorCallback) invocation.getArguments()[2]).onError(new ShootrException(){});
                return null;
            }
        }).when(resetPasswordInteractor).attempResetPassword(anyString(), anyCallback(), anyErrorCallback());
    }

    private void setupResetPasswordInteractorCallbacksResult() {
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

    private ForgotPasswordResult dummyResult() {
        return new ForgotPasswordResult();
    }

    private Interactor.Callback<ForgotPasswordResult> anyCallback() {
        return any(Interactor.Callback.class);
    }

    private Interactor.ErrorCallback anyErrorCallback() {
        return any(Interactor.ErrorCallback.class);
    }
}