package com.shootr.android.ui.presenter;

import com.shootr.android.domain.interactor.user.CreateAccountInteractor;
import com.shootr.android.ui.views.EmailRegistrationView;
import com.shootr.android.util.ErrorMessageFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class EmailRegistrationPresenterTest {

    @Mock EmailRegistrationView emailRegistrationView;
    @Mock CreateAccountInteractor createAccountInteractor;
    @Mock ErrorMessageFactory errorMessageFactory;

    private EmailRegistrationPresenter presenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new EmailRegistrationPresenter(createAccountInteractor, errorMessageFactory);
    }

    @Test public void shouldShowViewLoadingWhenOnCreateAccountCalled() throws Exception {
        presenter.setView(emailRegistrationView);

        presenter.createAccount();

        verify(emailRegistrationView).showLoading();
    }

    @Test public void shouldHideCreateButtonWhenOnCreateAccountCalled() throws Exception {
        presenter.setView(emailRegistrationView);

        presenter.createAccount();

        verify(emailRegistrationView).hideCreateButton();
    }
}