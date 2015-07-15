package com.shootr.android.ui.presenter;

import com.shootr.android.ui.views.EmailConfirmationView;
import javax.inject.Inject;

public class EmailConfirmationPresenter implements Presenter {

    private EmailConfirmationView emailConfirmationView;

    @Inject public EmailConfirmationPresenter() {

    }

    protected void setView(EmailConfirmationView emailConfirmationView) {
        this.emailConfirmationView = emailConfirmationView;
    }

    public void initialize(EmailConfirmationView emailConfirmationView) {
        this.setView(emailConfirmationView);
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }
}
