package com.shootr.android.ui.presenter;

import com.shootr.android.ui.views.EmailRegistrationView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class EmailRegistrationPresenterTest {

    @Mock EmailRegistrationView emailRegistrationView;
    private EmailRegistrationPresenter presenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new EmailRegistrationPresenter();
    }

    @Test public void shouldShowViewLoadingWhenOnCreateAccountCalled() throws Exception {
        presenter.setView(emailRegistrationView);

        presenter.onCreateAccount();

        verify(emailRegistrationView).showLoading();
    }

    @Test public void shouldHideCreateButtonWhenOnCreateAccountCalled() throws Exception {
        presenter.setView(emailRegistrationView);

        presenter.onCreateAccount();

        verify(emailRegistrationView).hideCreateButton();
    }
}