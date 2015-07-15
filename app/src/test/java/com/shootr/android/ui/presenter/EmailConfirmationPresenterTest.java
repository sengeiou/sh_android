package com.shootr.android.ui.presenter;

import com.shootr.android.ui.views.EmailConfirmationView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class EmailConfirmationPresenterTest {

    public static final String EMAIL = "email@email.com";
    @Mock EmailConfirmationView emailConfirmationView;

    private EmailConfirmationPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new EmailConfirmationPresenter();
        presenter.setView(emailConfirmationView);
    }

    @Test
    public void shouldSHowAlertWhenPresenterInitialized() {
        presenter.initialize(emailConfirmationView, EMAIL);
        verify(emailConfirmationView).showConfirmationToUser(EMAIL);
    }

}
