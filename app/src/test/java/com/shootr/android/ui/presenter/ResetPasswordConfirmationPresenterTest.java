package com.shootr.android.ui.presenter;

import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.ConfirmResetPasswordInteractor;
import com.shootr.android.ui.model.ForgotPasswordUserModel;
import com.shootr.android.ui.views.ResetPasswordConfirmationView;
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

public class ResetPasswordConfirmationPresenterTest {

    private static final String STUB_AVATAR = "avatar";
    private static final String STUB_ID_USER = "id_user";
    private static final String STUB_USERNAME = "username";
    private static final String STUB_EMAIL = "email";

    @Mock ResetPasswordConfirmationView resetPasswordConfirmationView;
    @Mock ConfirmResetPasswordInteractor confirmResetPasswordInteractor;
    @Mock ErrorMessageFactory errorMessageFactory;

    private ResetPasswordConfirmationPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new ResetPasswordConfirmationPresenter(confirmResetPasswordInteractor, errorMessageFactory);
        presenter.setView(resetPasswordConfirmationView);
        presenter.setUserModel(stubForgotPasswordUserModel());
    }

    @Test
    public void shouldShowAvatarWhenInitialized() throws Exception {
        presenter.initialize(resetPasswordConfirmationView, stubForgotPasswordUserModel());

        verify(resetPasswordConfirmationView).showAvatar(STUB_AVATAR);
    }

    @Test
    public void shouldShowUsernameWhenInitialized() throws Exception {
        presenter.initialize(resetPasswordConfirmationView, stubForgotPasswordUserModel());

        verify(resetPasswordConfirmationView).showUsername(STUB_USERNAME);
    }

    @Test
    public void shouldShowDoneButtonWhenConfirmIfInteractorCallbacksResult() throws Exception {
        setupInteractorCallbacksCompleted();

        presenter.confirm();

        verify(resetPasswordConfirmationView).showDoneButton();
    }

    @Test
    public void shouldHideConfirmButtonWhenConfirmIfInteractorCallbacksResult() throws Exception {
        setupInteractorCallbacksCompleted();

        presenter.confirm();

        verify(resetPasswordConfirmationView).hideConfirmationButton();
    }

    @Test
    public void shouldShowPostConfirmationMessageWithEmailWhenConfirmIfInteractorCallbacksResult() throws Exception {
        setupInteractorCallbacksCompleted();

        presenter.confirm();

        verify(resetPasswordConfirmationView).showPostConfirmationMessage(STUB_EMAIL);
    }

    @Test
    public void shouldShowLoadingWhenConfirm() throws Exception {
        presenter.confirm();

        verify(resetPasswordConfirmationView).showLoading();
    }

    @Test
    public void shouldHideLoadingWhenConfirmIfInteractorCallbacksResult() throws Exception {
        setupInteractorCallbacksCompleted();

        presenter.confirm();

        verify(resetPasswordConfirmationView).hideConfirmationButton();
    }

    @Test
    public void shouldShowErrorWhenConfirmIfInteractorCallbacksError() throws Exception {
        setupInteractorCallbacksError();

        presenter.confirm();

        verify(resetPasswordConfirmationView).showError(anyString());
    }

    protected void setupInteractorCallbacksCompleted() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((Interactor.CompletedCallback) invocation.getArguments()[1]).onCompleted();
                return null;
            }
        }).when(confirmResetPasswordInteractor).confirmResetPassword(anyString(), anyCallback(), anyErrorCallback());
    }

    protected void setupInteractorCallbacksError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                ((Interactor.ErrorCallback) invocation.getArguments()[2]).onError(new ShootrException() {});
                return null;
            }
        }).when(confirmResetPasswordInteractor).confirmResetPassword(anyString(), anyCallback(), anyErrorCallback());
    }

    private Interactor.CompletedCallback anyCallback() {
        return any(Interactor.CompletedCallback.class);
    }

    private Interactor.ErrorCallback anyErrorCallback() {
        return any(Interactor.ErrorCallback.class);
    }

    private ForgotPasswordUserModel stubForgotPasswordUserModel() {
        ForgotPasswordUserModel forgotPasswordUserModel = new ForgotPasswordUserModel();
        forgotPasswordUserModel.setAvatarUrl(STUB_AVATAR);
        forgotPasswordUserModel.setIdUser(STUB_ID_USER);
        forgotPasswordUserModel.setUsername(STUB_USERNAME);
        forgotPasswordUserModel.setEncryptedEmail(STUB_EMAIL);
        return forgotPasswordUserModel;
    }
}