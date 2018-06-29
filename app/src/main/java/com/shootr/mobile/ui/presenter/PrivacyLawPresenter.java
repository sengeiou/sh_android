package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.AcceptUserTermsInteractor;
import com.shootr.mobile.ui.views.PrivacyLawView;
import javax.inject.Inject;

public class PrivacyLawPresenter implements Presenter {

    private final AcceptUserTermsInteractor acceptUserTermsInteractor;

    private PrivacyLawView view;

     @Inject public PrivacyLawPresenter(AcceptUserTermsInteractor acceptUserTermsInteractor) {
         this.acceptUserTermsInteractor = acceptUserTermsInteractor;
     }

    protected void setView(PrivacyLawView view) {
        this.view = view;
    }

    public void initialize(PrivacyLawView view) {
        this.setView(view);
    }

    public void onAcceptTerms() {
         acceptUserTermsInteractor.acceptTerms(new Interactor.CompletedCallback() {
             @Override public void onCompleted() {
                 view.exit();
             }
         });
    }

    @Override public void resume() {
        /* no-op */
    }

    @Override public void pause() {
        /* no-op */
    }
}
