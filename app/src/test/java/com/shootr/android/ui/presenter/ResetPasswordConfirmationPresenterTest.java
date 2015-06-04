package com.shootr.android.ui.presenter;

import com.shootr.android.ui.model.ForgotPasswordUserModel;
import com.shootr.android.ui.views.ResetPasswordConfirmationView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class ResetPasswordConfirmationPresenterTest {

    private static final String STUB_AVATAR = "avatar";
    private static final String STUB_ID_USER = "id_user";
    private static final String STUB_USERNAME = "username";
    private static final String STUB_EMAIL = "email";

    @Mock ResetPasswordConfirmationView resetPasswordConfirmationView;

    private ResetPasswordConfirmationPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new ResetPasswordConfirmationPresenter();
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
    
    private ForgotPasswordUserModel stubForgotPasswordUserModel() {
        ForgotPasswordUserModel forgotPasswordUserModel = new ForgotPasswordUserModel();
        forgotPasswordUserModel.setAvatarUrl(STUB_AVATAR);
        forgotPasswordUserModel.setIdUser(STUB_ID_USER);
        forgotPasswordUserModel.setUsername(STUB_USERNAME);
        forgotPasswordUserModel.setEncryptedEmail(STUB_EMAIL);
        return forgotPasswordUserModel;
    }
}