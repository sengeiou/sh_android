package com.shootr.android.ui.presenter;

import com.shootr.android.ui.views.ResetPasswordRequestView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class ResetPasswordRequestPresenterTest {

    @Mock ResetPasswordRequestView resetPasswordRequestView;

    private ResetPasswordRequestPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new ResetPasswordRequestPresenter();
    }

    @Test
    public void shouldDisableNextButtonWhenInitialized() throws Exception {
        presenter.initialize(resetPasswordRequestView);

        verify(resetPasswordRequestView).disableNextButton();
    }
}